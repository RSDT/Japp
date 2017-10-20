package nl.rsdt.japp.jotial.maps.management.controllers;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.jotial.data.structures.area348.BaseInfo;
import nl.rsdt.japp.jotial.data.structures.area348.VosInfo;
import nl.rsdt.japp.jotial.io.AppData;
import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier;
import nl.rsdt.japp.jotial.maps.management.StandardMapItemController;
import nl.rsdt.japp.jotial.maps.management.transformation.AbstractTransducer;
import nl.rsdt.japp.jotial.maps.misc.VosUtils;
import nl.rsdt.japp.jotial.maps.sighting.SightingIcon;
import nl.rsdt.japp.jotial.maps.wrapper.ICircle;
import nl.rsdt.japp.jotial.maps.wrapper.IMarker;
import nl.rsdt.japp.jotial.net.apis.VosApi;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
public abstract class VosController extends StandardMapItemController<VosInfo, VosController.VosTransducer.Result> implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Handler handler = new Handler();

    private UpdateCircleRunnable runnable = new UpdateCircleRunnable();

    public abstract String getTeam();

    public VosController() {
        JappPreferences.getVisiblePreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public Call<ArrayList<VosInfo>> update(String mode) {
        Call<ArrayList<VosInfo>> call = new Call<ArrayList<VosInfo>>() {
            private VosApi api = Japp.getApi(VosApi.class);
            private Call<ArrayList<VosInfo>> apiCall = api.getAll(JappPreferences.getAccountKey(), getTeam());
            private SimpleDateFormat format  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            @Override
            public Response<ArrayList<VosInfo>> execute() throws IOException {
                return apiCall.execute();
            }

            @Override
            public void enqueue(final Callback<ArrayList<VosInfo>> callback) {
                Callback<ArrayList<VosInfo>> callback2 = new Callback<ArrayList<VosInfo>>() {
                    @Override
                    public void onResponse(Call<ArrayList<VosInfo>> call, Response<ArrayList<VosInfo>> response) {
                        if (JappPreferences.onlyToday()) {
                            ArrayList<VosInfo> delete = new ArrayList<>();
                            for (VosInfo info : response.body()) {
                                try {
                                    Date date = format.parse(info.getDatetime());
                                    Calendar c = new GregorianCalendar();
                                    c.set(Calendar.HOUR_OF_DAY, 0);
                                    c.set(Calendar.MINUTE, 0);
                                    c.set(Calendar.SECOND, 0);
                                    Calendar d = new GregorianCalendar();
                                    d.setTime(date);
                                    if (c.after(d)) {
                                        delete.add(info);
                                    }
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            response.body().removeAll(delete);
                        }
                        callback.onResponse(call, response);
                    }

                    @Override
                    public void onFailure(Call<ArrayList<VosInfo>> call, Throwable t) {
                        callback.onFailure(call, t);
                    }
                };
                apiCall.enqueue(callback2);
            }

            @Override
            public boolean isExecuted() {
                return apiCall.isExecuted();
            }

            @Override
            public void cancel() {
                apiCall.cancel();
            }

            @Override
            public boolean isCanceled() {
                return apiCall.isCanceled();
            }

            @Override
            public Call<ArrayList<VosInfo>> clone() {
                return apiCall.clone();
            }

            @Override
            public Request request() {
                return null;
            }
        };
        switch (mode) {
            case MODE_ALL:

                return call;
            case MODE_LATEST:
                return call;
        }
        return null;
    }

    @Override
    public VosTransducer getTransducer() {
        return new VosTransducer(getStorageId(), getBundleId());
    }

    @Override
    public IMarker searchFor(String query) {
        ArrayList<BaseInfo> results = new ArrayList<>();
        VosInfo info;
        for(int i = 0; i < items.size(); i++) {
            info = items.get(i);
            if(info != null) {

                String current;
                String[] items = new String[] { info.getNote(),  info.getExtra() };
                for(int x = 0; x < items.length; x++) {
                    current = items[x];
                    if(current.toLowerCase(Locale.ROOT).startsWith(query)) results.add(info);
                }
            }
        }
        return null;
    }

    @Override
    protected void processResult(VosTransducer.Result result) {
        super.processResult(result);
        handler.post(runnable);
    }

    @Override
    public List<String> provide() {
        ArrayList<String> entries = new ArrayList<>();
        VosInfo info;
        for(int i = 0; i < items.size(); i++) {
            info = items.get(i);
            if(info != null) {
                entries.add(info.getNote());
                entries.add(info.getExtra());
            }
        }
        return entries;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        super.onSharedPreferenceChanged(sharedPreferences, key);
        switch (key) {
            case JappPreferences.AUTO_ENLARGMENT:
                handler.removeCallbacks(runnable);
                if(sharedPreferences.getBoolean(key, true)) {
                    handler.post(runnable);
                } else {
                    handler.removeCallbacks(runnable);
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(handler != null) {
            if(runnable != null) {
                handler.removeCallbacks(runnable);
            }
            handler = null;
            runnable = null;
        }

        JappPreferences.getVisiblePreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public static class VosTransducer extends AbstractTransducer<ArrayList<VosInfo>, VosTransducer.Result>
    {
        private String storageId;

        private String bundleId;

        public VosTransducer(String storageId, String bundleId) {
            this.storageId = storageId;
            this.bundleId = bundleId;
        }

        @Override
        public ArrayList<VosInfo> load() {
            return AppData.getObject(storageId, new TypeToken<ArrayList<VosInfo>>(){}.getType());
        }

        @Override
        public void transduceToBundle(Bundle bundle) {
            bundle.putParcelable(bundleId, generate(load()));
        }

        @Override
        public Result generate(ArrayList<VosInfo> data) {
            if(data == null || data.isEmpty()) return new Result();
            Collections.sort(data, new Comparator<VosInfo>() {
                @Override
                public int compare(VosInfo info1, VosInfo info2) {
                    Date firstDate = null;
                    Date secondDate = null;
                    try {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT);
                        firstDate = format.parse(info1.getDatetime());
                        secondDate = format.parse(info2.getDatetime());
                    } catch (ParseException e) {
                        Log.e(TAG, e.toString(), e);
                    }
                    if(firstDate != null && secondDate != null) {
                        return firstDate.compareTo(secondDate);
                    }
                    return 0;
                }
            });

            Result result = new Result();
            result.setBundleId(bundleId);
            result.addItems(data);

            if(saveEnabled)
            {
                /**
                 * Saves the generated data.
                 * */
                AppData.saveObjectAsJson(data, storageId);
            }

            PolylineOptions pOptions = new PolylineOptions();
            pOptions.color(data.get(0).getAssociatedColor());
            pOptions.width(5);

            VosInfo current;

            for(int i = 0; i < data.size(); i++)
            {
                current = data.get(i);

                MarkerIdentifier identifier  = new MarkerIdentifier.Builder()
                        .setType(MarkerIdentifier.TYPE_VOS)
                        .add("team", current.getTeam())
                        .add("note", current.getNote())
                        .add("extra", current.getExtra())
                        .add("time", current.getDatetime())
                        .add("icon", String.valueOf(current.getAssociatedDrawable()))
                        .add("color", String.valueOf(current.getAssociatedColor(130)))
                        .create();

                MarkerOptions mOptions = new MarkerOptions();
                mOptions.anchor(0.5f, 0.5f);
                mOptions.title(new Gson().toJson(identifier));
                mOptions.position(current.getLatLng());

                int last = data.size() - 1;
                if(i == last) {
                    if(current.getIcon() == SightingIcon.DEFAULT || current.getIcon() == SightingIcon.INVALID) {
                        current.setIcon(SightingIcon.LAST_LOCATION);
                    }

                    double diff = VosUtils.calculateTimeDifferenceInHoursFromNow(VosUtils.parseDate(current.getDatetime()));

                    /**
                     * If the VosInfo is younger or equal than 2 hours: show a circle indicating where the vos team could have walked
                     * */
                    if(diff > 0 && diff <= 2) {
                        CircleOptions cOptions = new CircleOptions();
                        cOptions.center(current.getLatLng());
                        cOptions.fillColor(current.getAssociatedColor(80));
                        cOptions.strokeWidth(0);
                        cOptions.radius(VosUtils.calculateRadius(current.getDatetime(), JappPreferences.getWalkSpeed()));
                        result.add(cOptions);
                    }
                }

                BitmapFactory.Options options = new BitmapFactory.Options();
                switch (current.getIcon()) {
                    case SightingIcon.DEFAULT:
                        options.inSampleSize = 4;
                        break;
                    case SightingIcon.HUNT:
                        options.inSampleSize = 2;
                        break;
                    case SightingIcon.SPOT:
                        options.inSampleSize = 2;
                        break;
                    case SightingIcon.LAST_LOCATION:
                        options.inSampleSize = 2;
                        break;
                    default:
                        options.inSampleSize = 2;
                        break;
                }
                Bitmap bitmap = BitmapFactory.decodeResource(Japp.getAppResources(), current.getAssociatedDrawable(), options);
                mOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                result.add(new Pair<>(mOptions,bitmap));

                pOptions.add(current.getLatLng());
            }
            result.add(pOptions);

            return result;
        }


        public static class Result extends AbstractTransducer.StandardResult<VosInfo>
        {
            public Result() {}

            /**
             * Reconstructs the result.
             *
             * @param in The parcel where the result was written to
             */
            protected Result(Parcel in) {
                super(in);
                items = in.createTypedArrayList(VosInfo.CREATOR);
            }


            @Override
            public void writeToParcel(Parcel dest, int flags) {
                super.writeToParcel(dest, flags);
                dest.writeTypedList(items);
            }

            public static final Creator<Result> CREATOR = new Creator<Result>() {
                @Override
                public Result createFromParcel(Parcel in) {
                    return new Result(in);
                }

                @Override
                public Result[] newArray(int size) {
                    return new Result[size];
                }
            };
        }

    }

    private class UpdateCircleRunnable implements Runnable {

        @Override
        public void run() {
            ArrayList<ICircle> circles = new ArrayList<>(VosController.this.circles.keySet());
            if(!circles.isEmpty()) {
                ICircle circle = circles.get(0);

                int last = items.size() - 1;
                VosInfo info = items.get(last);

                /**
                 * Parse the date.
                 * */
                Date date = VosUtils.parseDate(info.getDatetime());

                /**
                 * Calculate the difference in time between the vos date and now.
                 * */
                double diff = VosUtils.calculateTimeDifferenceInHoursFromNow(date);

                /**
                 * If the VosInfo is younger or equal than 2 hours: show a circle indicating where the vos team could have walked
                 * */
                if(diff > 0 && diff <= 2) {
                    circle.setRadius(VosUtils.calculateRadius(date, JappPreferences.getWalkSpeed()));
                    if(JappPreferences.isAutoEnlargementEnabled()) {
                        handler.postDelayed(this, (long)JappPreferences.getAutoEnlargementIntervalInMs());
                    }
                } else {
                    circle.remove();
                    circles.remove(circle);
                }
            }
        }
    }

}
