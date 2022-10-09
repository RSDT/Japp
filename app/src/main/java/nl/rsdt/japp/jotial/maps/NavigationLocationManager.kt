package nl.rsdt.japp.jotial.maps

import android.util.Log
import nl.rsdt.japp.application.Japp
import nl.rsdt.japp.application.JappPreferences
import nl.rsdt.japp.jotial.data.nav.Location
import nl.rsdt.japp.jotial.data.structures.area348.AutoInzittendeInfo
import nl.rsdt.japp.jotial.net.apis.AutoApi
import nl.rsdt.japp.service.AutoSocketHandler
import org.acra.ktx.sendWithAcra
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by mattijn on 30/09/17.
 */

class NavigationLocationManager {
    private var callback: OnNewLocation? = null
    private var oldLoc: Location? = null
    private val mSocketHandler: AutoSocketHandler.NavToHandler
    init {
        // args[0] is the data from the server
// Change "as Int" according to the data type
// Example "as String" or write nothing
// Logging the data is optional
        mSocketHandler = AutoSocketHandler.NavToHandler {
            update(it)
        }
        AutoSocketHandler.registerNavToHandler(mSocketHandler)
    }

    private fun update(location: Location) {
        val id = JappPreferences.accountId
        if (id >= 0) {
            val api = Japp.getApi(AutoApi::class.java)
            api.getInfoById(JappPreferences.accountKey, id).enqueue(object : Callback<AutoInzittendeInfo> {
                override fun onResponse(call: Call<AutoInzittendeInfo>, response: Response<AutoInzittendeInfo>) {
                    if (response.code() == 200) {
                        val info = response.body()
                        if (oldLoc != null && oldLoc != location || oldLoc == null) {
                            oldLoc = location
                            callback?.onNewLocation(location)

                        }
                    } else if (response.code() == 404) {
                        callback?.onNotInCar()
                    }
                }

                override fun onFailure(call: Call<AutoInzittendeInfo>, t: Throwable) {
                    t.sendWithAcra()
                    Log.e(TAG, t.toString())
                }
            })
        }
    }

    fun setCallback(callback: OnNewLocation) {
        this.callback = callback
    }

    interface OnNewLocation {
        fun onNewLocation(location: Location)
        fun onNotInCar()
    }

    companion object {
        val FDB_NAME = "autos"
        val TAG = "NavigationLocationManager"
    }
}
