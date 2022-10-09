package nl.rsdt.japp.jotial.maps.clustering.osm

//import com.crashlytics.android.Crashlytics
import nl.rsdt.japp.jotial.data.structures.area348.ScoutingGroepInfo
import nl.rsdt.japp.jotial.maps.clustering.ClusterManagerInterface
import nl.rsdt.japp.jotial.maps.wrapper.osm.OsmJotiMap
import org.acra.ktx.sendWithAcra
import java.util.*

/**
 * Created by mattijn on 10/08/17.
 */

class OsmScoutingGroepClusterManager(jotiMap: OsmJotiMap) : ClusterManagerInterface {

    private val infos: MutableCollection<ScoutingGroepInfo>
    private val markers: OsmMarkerContainer

    override val items: List<ScoutingGroepInfo>
        get() = infos.toList()

    init {
        infos = ArrayList()
        markers = OsmMarkerContainer(jotiMap)
    }

    override fun addItems(buffer: ArrayList<ScoutingGroepInfo>) {
        try {
            val infosToAdd = mutableListOf<ScoutingGroepInfo>()
            for (info in buffer) {
                infosToAdd.add(info)
                markers.add(info)
            }
            infos.addAll(infosToAdd)
            markers.showMarkers()
        } catch(e: ClassCastException){
            System.err.println(e)
            e.sendWithAcra()
            //Crashlytics.log(e.toString())
        }
    }

    override fun cluster() {

    }

    override fun clearItems() {
        infos.clear()
        markers.clear()
    }
}
