package nl.rsdt.japp.jotial.maps.management

import nl.rsdt.japp.service.cloud.data.UpdateInfo
import retrofit2.Call

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
interface MapItemUpdatable<I> {

    fun update(mode: String): Call<I>?

    fun onUpdateInvoked()

    fun onUpdateMessage(info: UpdateInfo)

    companion object {

        val MODE_ALL = "ALL"

        val MODE_LATEST = "LATEST"
    }
}
