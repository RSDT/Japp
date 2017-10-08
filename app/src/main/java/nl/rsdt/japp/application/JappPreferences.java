package nl.rsdt.japp.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.Set;

import nl.rsdt.japp.jotial.maps.MapStyle;


/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 13-7-2016
 * Class for release_preferences
 */
public class JappPreferences {

    public static final String FIRST_RUN = "pref_first_run";

    public static final String ACCOUNT_ID = "pref_account_id";

    public static final String ACCOUNT_USERNAME = "pref_account_username";

    public static final String ACCOUNT_NAME = "pref_account_name";

    public static final String ACCOUNT_SURNAME = "pref_account_surname";

    public static final String ACCOUNT_EMAIL = "pref_account_email";

    public static final String ACCOUNT_MEMBER_SINCE = "pref_account_since";

    public static final String ACCOUNT_LAST_VISIT = "pref_account_last";

    public static final String ACCOUNT_ACTIVE = "pref_account_active";

    public static final String ACCOUNT_AVATAR = "pref_account_avatar";

    public static final String ACCOUNT_RANK = "pref_account_rank";

    public static final String ACCOUNT_ICON = "pref_account_icon";

    public static final String ACCOUNT_KEY = "pref_developer_account_key";

    public static final String UPDATES_AUTO = "pref_updates_auto";

    public static final String MAP_TYPE = "pref_map_type";

    public static final String MAP_STYLE = "pref_map_style";

    public static final String MAP_HUNT_NAME = "pref_map_hunt_name";

    public static final String MAP_CONTROLS = "pref_map_controls";

    public static final String FOLLOW_ZOOM = "pref_follow_zoom";

    public static final String FOLLOW_AOA = "pref_follow_aoa";

    public static final String LOCATION_UPDATE_INTERVAL = "pref_advanced_location_update_interval";

    public static final String HUNTER_UPDATE_INTERVAL = "pref_advanced_hunter_update_interval";

    public static final String DEBUG_VERSION_NAME = "pref_developer_debug_version_name";

    public static final String FCM_TOKEN = "pref_fcm_token";

    public static final String DEBUG_FRESH_START = "pref_developer_debug_fresh_start";

    public static final String UPDATE_LOCATION = "pref_updates_location";

    public static final String AUTO_ENLARGMENT = "pref_advanced_auto_enlargement";

    public static final String AUTO_ENLARGMENT_INTERVAL = "pref_advanced_auto_enlargement_interval";

    public static final String WALK_SPEED = "pref_advanced_auto_enlargement_walking_speed";

    public static final String AREAS_EDGES = "pref_advanced_areas_edges";

    public static final String AREAS_EDGES_WIDTH = "pref_advanced_areas_edges_width";

    public static final String AREAS_COLOR = "pref_advanced_areas_color";

    public static final String AREAS_COLOR_ALPHA = "pref_advanced_areas_color_alpha";

    public static final String USE_OSM = "pref_advanced_osm";

    public static final String SHACO_ENABLED = "pref_developer_random_shaco";

    public static final String NAVIGATION_APP = "pref_navigation_app";

    public static final String HUNTER_ALL = "pref_developer_hunter_all";

    public static final String IS_NAVIGATION_PHONE = "pref_navigation_phone";

    public static final String LOAD_OLD_DATA = "pref_developer_load_old_data";

    public static final String OSM_MAP_TYPE = "pref_map_osm_source";


    /**
     * Gets the visible release_preferences of Japp.
     * */
    public static SharedPreferences getVisiblePreferences() {
        return PreferenceManager.getDefaultSharedPreferences(Japp.getInstance());
    }

    /**
     * Gets the non-visible release_preferences of Japp.
     * */
    public static SharedPreferences getAppPreferences() {
        return Japp.getInstance().getSharedPreferences("nl.rsdt.japp", Context.MODE_PRIVATE);
    }

    /**
     * Gets the value indicating if this is the first run.
     * */
    public static boolean isFirstRun() {
        return getAppPreferences().getBoolean(FIRST_RUN, true);
    }

    /**
     * Sets the value determining if this is the first run.
     * */
    public static void setFirstRun(boolean value) { getAppPreferences().edit().putBoolean(FIRST_RUN, value).apply(); }

    /**
     * Gets the username of the active account.
     * */
    public static String getAccountUsername() {
        return getVisiblePreferences().getString(ACCOUNT_USERNAME, "unknown");
    }

    /**
     * Gets the rank of the active account.
     * */
    public static String getAccountRank() {
        return getVisiblePreferences().getString(ACCOUNT_RANK, "Guest");
    }

    public static int getAccountId(){
        return getAppPreferences().getInt(ACCOUNT_ID, -1);
    }

    /**
     * Gets the icon that the user has selected to be displayed to the others.
     * */
    public static int getAccountIcon() {
        return Integer.valueOf(getVisiblePreferences().getString(ACCOUNT_ICON, "0"));
    }

    /**
     * Gets the API-key associated with the active account.
     * */
    public static String getAccountKey() {
        return getVisiblePreferences().getString(ACCOUNT_KEY, "");
    }

    /**
     * Gets the filename of the avatar on the area348 server of the active account.
     * */
    public static String getAccountAvatarName() {
        return getAppPreferences().getString(ACCOUNT_AVATAR, "");
    }

    /**
     * Gets the value indicating if auto update is enabled.
     * */
    public static boolean isAutoUpdateEnabled() {
        return getVisiblePreferences().getBoolean(UPDATES_AUTO, true);
    }

    /**
     * Sets the value indicating if auto update is enabled.
     * */
    public static void setAutoUpdateEnabled(boolean value) {
        getVisiblePreferences().edit().putBoolean(UPDATES_AUTO, value).apply();
    }

    public static int getMapType() {
        return Integer.valueOf(getVisiblePreferences().getString(MAP_TYPE, String.valueOf(GoogleMap.MAP_TYPE_NORMAL)));
    }

    public static void setMapType(int type) {
        getVisiblePreferences().edit().putString(MAP_TYPE, String.valueOf(type)).apply();
    }

    public static int getMapStyle() {
        return Integer.valueOf(getVisiblePreferences().getString(MAP_STYLE, String.valueOf(MapStyle.DAY)));
    }

    public static String getHuntname() {
        return getVisiblePreferences().getString(MAP_HUNT_NAME, "");
    }

    public static Set<String> getMapControls() {
        return getVisiblePreferences().getStringSet(MAP_CONTROLS, null);
    }

    public static float getFollowZoom() {
        return getAppPreferences().getFloat(FOLLOW_ZOOM, 20f);
    }

    public static void setFollowZoom(float zoom) {
        getAppPreferences().edit().putFloat(FOLLOW_ZOOM, zoom).apply();
    }

    public static float getFollowAngleOfAttack() {
        return getAppPreferences().getFloat(FOLLOW_AOA, 45f);
    }

    public static void setFollowAoa(float aoa) {
        getAppPreferences().edit().putFloat(FOLLOW_AOA, aoa).apply();
    }

    public static float getLocationUpdateIntervalInMinutes() {
        return Float.valueOf(getVisiblePreferences().getString(LOCATION_UPDATE_INTERVAL, "1.0"));
    }

    public static float getLocationUpdateIntervalInMs() {
        return Float.valueOf(getVisiblePreferences().getString(LOCATION_UPDATE_INTERVAL, "1.0")) * 60 * 1000;
    }

    public static float getHunterUpdateIntervalInMs() {
        return Float.valueOf(getVisiblePreferences().getString(HUNTER_UPDATE_INTERVAL, "1.0")) * 60 * 1000;
    }

    public static String getFcmToken() {
        return getAppPreferences().getString(FCM_TOKEN, "");
    }

    public static void setFcmToken(String token) {
        getAppPreferences().edit().putString(FCM_TOKEN, token).apply();
    }

    public static boolean isFreshStart() {
        return getVisiblePreferences().getBoolean(DEBUG_FRESH_START, false);
    }

    public static boolean isUpdatingLocationToServer() {
        return getVisiblePreferences().getBoolean(UPDATE_LOCATION, true);
    }

    public static boolean isAutoEnlargementEnabled() {
        return getVisiblePreferences().getBoolean(AUTO_ENLARGMENT, true);
    }

    public static float getAutoEnlargementInterval() {
        return Float.valueOf(getVisiblePreferences().getString(AUTO_ENLARGMENT_INTERVAL, "0.1f"));
    }

    public static float getAutoEnlargementIntervalInMs() {
        return Float.valueOf(getVisiblePreferences().getString(AUTO_ENLARGMENT_INTERVAL, "0.2f")) * 60 * 1000;
    }

    public static float getWalkSpeed() {
        return Float.parseFloat(getVisiblePreferences().getString(WALK_SPEED, "6.0f"));
    }

    public static boolean getAreasEdgesEnabled() {
        return getVisiblePreferences().getBoolean(AREAS_EDGES, true);
    }

    public static int getAreasEdgesWidth() {
        return Integer.valueOf(getVisiblePreferences().getString(AREAS_EDGES_WIDTH, "5"));
    }

    public static boolean getAreasColorEnabled() {
        return getVisiblePreferences().getBoolean(AREAS_COLOR, true);
    }

    public static int getAreasColorAlpha() {
        return Integer.valueOf(getVisiblePreferences().getString(AREAS_COLOR_ALPHA, "25"));
    }

    public static boolean useOSM() {
        return getVisiblePreferences().getBoolean(USE_OSM, true);
    }

    public static boolean shacoEnabled() {
        return getVisiblePreferences().getBoolean(SHACO_ENABLED, false);
    }

    public static NavigationApp navigationApp(){
        return NavigationApp.fromString(getVisiblePreferences().getString(NAVIGATION_APP, "Google Maps"));
    }

    public static void clear() {
        getVisiblePreferences().edit().clear().apply();
        getAppPreferences().edit().clear().apply();
    }

    public static boolean getGetAllHunters() {
        return getVisiblePreferences().getBoolean(HUNTER_ALL, false);
    }

    public static boolean loadOldData() {
        return getVisiblePreferences().getBoolean(LOAD_OLD_DATA, true);
    }
    public static OsmMapType getOsmMapSource(){
        String value = getVisiblePreferences().getString(OSM_MAP_TYPE, "Default");
        if ("0".equals(value)){
            return OsmMapType.Default;
        }
        return OsmMapType.valueOf(value);
    }
    public static boolean isNavigationPhone(){
        return getVisiblePreferences().getBoolean(IS_NAVIGATION_PHONE, false);
    }

    public enum OsmMapType{
        Default,
        OpenTopo,
        Mapnik,
        HikeBike,
        Public_Transport,
        Base_NL,
        Fiets_NL,
        Road_NL,
        OpenSeaMap,
        CloudMade_Small,
        CloudMade_Normal,
        USGS_Topo,
        USGS_Sat,
        ChartBundle_ENRH,
        ChartBundle_ENRL,
        ChartBundle_WAC
    }
    public enum NavigationApp{
        GoogleMaps, Waze;
        public static NavigationApp fromString(String appName){
            if (appName.equals("Waze")){
                return Waze;
            }
            else if (appName.equals("Google Maps")){
                return GoogleMaps;
            }
            else{
                return GoogleMaps; //default google maps
            }
        }
    }

}
