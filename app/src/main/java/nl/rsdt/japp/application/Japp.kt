package nl.rsdt.japp.application

import android.content.Context
import android.content.res.Resources
import android.location.Location
import android.net.ConnectivityManager
import androidx.multidex.MultiDexApplication
import com.google.android.gms.maps.MapsInitializer
import com.google.gson.GsonBuilder
import nl.rsdt.japp.jotial.io.AppData
import nl.rsdt.japp.jotial.net.API
import nl.rsdt.japp.service.cloud.messaging.MessageManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.util.*

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-7-2016
 * Description...
 */
class Japp : MultiDexApplication() {

    private val messageManager = MessageManager()

    private var interceptor: Interceptor? = null

    override fun onCreate() {
        super.onCreate()
        instance = this



        MapsInitializer.initialize(this)

        AppData.initialize(this.filesDir)
    }

    companion object {

        var instance: Japp? = null
            private set
        private var _lastLocation :Location? = null
        private val _lastLocations : Queue<Location> = ArrayDeque()
        var lastLocation: Location?
            get() = _lastLocations.poll()?: _lastLocation
            set(value) {
                _lastLocations.offer(value)
                _lastLocation = value
            }

        val updateManager: MessageManager?
            get() = instance?.messageManager

        fun getInterceptor(): Interceptor? {
            return instance?.interceptor
        }

        fun setInterceptor(value: Interceptor?) {
            instance?.interceptor = value
        }

        fun <T> getApi(api: Class<T>): T {
            val client = OkHttpClient.Builder()
            val interceptor = instance?.interceptor
            if (interceptor != null) {
                client.addInterceptor(interceptor)
            }

            val retrofit = Retrofit.Builder()
                    .baseUrl(API.API_V2_ROOT)
                    .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
                    .client(client.build())
                    .build()
            return retrofit.create(api)
        }

        val appResources: Resources?
            get() = instance?.applicationContext?.resources

        fun hasInternetConnection(): Boolean {
            val cm = instance?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            val info = cm?.activeNetworkInfo
            return info != null && info.isConnectedOrConnecting
        }

        fun getString(string: Int): String {
            return appResources?.getString(string)?: "null"
        }
    }
}
