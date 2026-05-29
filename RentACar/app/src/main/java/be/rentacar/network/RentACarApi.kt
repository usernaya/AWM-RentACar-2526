package be.rentacar.network

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

// url van mijn backend op Combell — laatste / is belangrijk
private const val BASE_URL =
    "http://mohamedaminehssinoui-odiseebe.webhosting.be/api/"

// singleton zodat ik overal dezelfde Retrofit gebruik
object RentACarApi {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    // 30s timeout, Combell kan traag opstarten
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(
            json.asConverterFactory("application/json".toMediaType())
        )
        .client(okHttpClient)
        .baseUrl(BASE_URL)
        .build()

    val retroFitService: RentACarApiService by lazy {
        retrofit.create(RentACarApiService::class.java)
    }
}
