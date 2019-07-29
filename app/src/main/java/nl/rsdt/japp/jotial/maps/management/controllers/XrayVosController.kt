package nl.rsdt.japp.jotial.maps.management.controllers


/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
class XrayVosController : VosController() {

    override val team: String
        get() = "x"

    override val id: String
        get() = CONTROLLER_ID

    override val storageId: String
        get() = STORAGE_ID

    override val bundleId: String
        get() = BUNDLE_ID

    companion object {

        val CONTROLLER_ID = "XrayVosController"

        val STORAGE_ID = "STORAGE_VOS_X"

        val BUNDLE_ID = "VOS_X"

        val REQUEST_ID = "REQUEST_VOS_X"
    }

}

