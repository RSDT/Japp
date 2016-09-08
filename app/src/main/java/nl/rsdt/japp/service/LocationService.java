package nl.rsdt.japp.service;

import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.jotial.data.bodies.HunterPostBody;
import nl.rsdt.japp.jotial.maps.locations.LocationProviderService;
import nl.rsdt.japp.jotial.net.apis.HunterApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-7-2016
 * Description...
 */
public class LocationService extends LocationProviderService {


    public static final String TAG = "LocationService";

    private final LocationBinder binder = new LocationBinder();

    Calendar lastUpdate = Calendar.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public LocationRequest getStandard() {
        return new LocationRequest()
                .setInterval(Float.floatToIntBits(JappPreferences.getLocationUpdateIntervalInMs()))
                .setFastestInterval(Float.floatToIntBits(JappPreferences.getLocationUpdateIntervalInMs()))
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);

        long dif = Calendar.getInstance().getTimeInMillis() - lastUpdate.getTimeInMillis();

        if(dif >= Math.round(JappPreferences.getLocationUpdateIntervalInMs())) {
            HunterPostBody builder = HunterPostBody.getDefault();
            builder.setLatLng(new LatLng(location.getLatitude(), location.getLongitude()));

            HunterApi api = Japp.getApi(HunterApi.class);
            api.post(builder).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.i(TAG, "Location was sent!");
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, t.toString(), t);
                }
            });

            lastUpdate = Calendar.getInstance();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        super.onConnected(bundle);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocationBinder extends Binder {
        public LocationService getInstance() {
            return LocationService.this;
        }
    }

}
