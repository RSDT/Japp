package nl.rsdt.japp.jotial.maps.management.transformation


import android.graphics.Bitmap
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Pair
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.PolylineOptions
import nl.rsdt.japp.jotial.BundleIdentifiable
import nl.rsdt.japp.jotial.maps.management.transformation.async.AsyncTransduceTask
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
abstract class AbstractTransducer<I, O : AbstractTransducer.Result> {

    var isSaveEnabled = true

    abstract fun load(): I?

    abstract fun generate(data: I): O

    abstract fun transduceToBundle(bundle: Bundle)

    fun enqueue(data: I, callback: AsyncTransduceTask.OnTransduceCompletedCallback<O>) {
        val task = AsyncTransduceTask<I, O>()
        task.transducer = this
        task.data = data
        task.callback = callback
        task.execute()
    }

    abstract class Result : Parcelable, BundleIdentifiable {

        /**
         * The id of the result.
         */
        /**
         * Gets the id of the result.
         */
        /**
         * Sets the id of the result.
         */
        override var bundleId: String = ""

        /**
         * The Marker list.
         */
        var markers: MutableList<Pair<MarkerOptions, Bitmap?>>
            protected set

        /**
         * The Polyline list.
         */
        var polylines: MutableList<PolylineOptions> = ArrayList()
            protected set

        /**
         * The Polygon list.
         */
        var polygons: MutableList<PolygonOptions> = ArrayList()
            protected set

        /**
         * The Circle list.
         */
        /**
         * Gets the circlesController.
         */
        var circles: MutableList<CircleOptions> = ArrayList()
            protected set

        /**
         * Adds a MarkerOptions object to the markers list.
         */
        fun add(options: Pair<MarkerOptions, Bitmap?>) {
            markers.add(options)
        }

        /**
         * Adds a PolylineOptions object to the polyline list.
         */
        fun add(options: PolylineOptions) {
            polylines.add(options)
        }

        /**
         * Adds a PolygonOptions object to the polygon list.
         */
        fun add(options: PolygonOptions) {
            polygons.add(options)
        }

        /**
         * Adds a CircleOptions object to the polygon list.
         */
        fun add(options: CircleOptions) {
            circles.add(options)
        }

        protected constructor() {
            markers = ArrayList()
            polylines = ArrayList()
            polygons = ArrayList()
            circles = ArrayList()
        }

        /**
         * Reconstructs the result.
         *
         * @param in The parcel where the result was written to
         */
        protected constructor(`in`: Parcel) {
            bundleId = `in`.readString()?:"null"
            markers = ArrayList()
            val optionsList = `in`.createTypedArrayList(MarkerOptions.CREATOR)
            val bitmapList = `in`.createTypedArrayList(Bitmap.CREATOR)
            val markers = ArrayList<Pair<MarkerOptions, Bitmap?>>()

            polylines = `in`.createTypedArrayList(PolylineOptions.CREATOR)!!
            polygons = `in`.createTypedArrayList(PolygonOptions.CREATOR)!!
            circles = `in`.createTypedArrayList(CircleOptions.CREATOR)!!
            if (bitmapList!!.size != optionsList!!.size) {
                throw RuntimeException("optionlist and bitmapList are not equal in size")
            }
            for (i in optionsList.indices) {
                markers.add(Pair(optionsList[i], bitmapList[i]))
            }
            this.markers = markers
        }

        override fun writeToParcel(dest: Parcel, i: Int) {
            dest.writeString(bundleId)
            val optionsList = ArrayList<MarkerOptions>()
            val bitmapList = ArrayList<Bitmap?>()
            for (p in markers) {
                optionsList.add(p.first)
                bitmapList.add(p.second)
            }

            dest.writeTypedList(optionsList)
            dest.writeTypedList(bitmapList)
            dest.writeTypedList(polylines)
            dest.writeTypedList(polygons)
            dest.writeTypedList(circles)
        }
    }

    abstract class StandardResult<T : Parcelable> : Result {

        var items = ArrayList<T>()
            protected set

        constructor()

        constructor(`in`: Parcel) : super(`in`)

        fun addItems(items: List<T>) {
            this.items.addAll(items)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object {

            val CREATOR: Parcelable.Creator<StandardResult<*>> = object : Parcelable.Creator<StandardResult<*>> {
                override fun createFromParcel(`in`: Parcel): StandardResult<*> {
                    return object : StandardResult<Parcelable>() {

                    }
                }

                override fun newArray(size: Int): Array<StandardResult<*>?> {
                    return arrayOfNulls(size)
                }
            }
        }


    }

}
