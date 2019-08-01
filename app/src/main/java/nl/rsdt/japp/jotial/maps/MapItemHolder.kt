package nl.rsdt.japp.jotial.maps


import nl.rsdt.japp.jotial.maps.wrapper.ICircle
import nl.rsdt.japp.jotial.maps.wrapper.IMarker
import nl.rsdt.japp.jotial.maps.wrapper.IPolygon
import nl.rsdt.japp.jotial.maps.wrapper.IPolyline
import java.util.*


/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 24-7-2016
 * Description...
 */
interface MapItemHolder {

    val markers: List<IMarker>

    val polylines: List<IPolyline>

    val polygons: List<IPolygon>

    val circles: List<ICircle>

}
