package com.example.fiapp.network

import com.example.walmarthackathon.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.security.NoSuchAlgorithmException
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext

object NetworkServiceGenerator {

    public var instance: NetworkServiceGenerator? = null
    private var httpBuilder: OkHttpClient.Builder? = null
    private var builder: Retrofit.Builder? = null
    private var logging: HttpLoggingInterceptor? = null

    init {
        httpBuilder = OkHttpClient.Builder()
        logging = HttpLoggingInterceptor()

        enableLog()
    }

    private fun enableLog() {
        if (!BuildConfig.DEBUG) {
            logging?.level = HttpLoggingInterceptor.Level.NONE
        } else {
            logging?.level = HttpLoggingInterceptor.Level.HEADERS
            logging?.level = HttpLoggingInterceptor.Level.BODY
        }
    }

    public fun <T> createService(serviceClass: Class<T>, baseUrl: String): T {

        try {
            SSLContext.getInstance("TLSv1.2")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        httpBuilder?.writeTimeout(120000, TimeUnit.MILLISECONDS)
        httpBuilder?.readTimeout(120000, TimeUnit.MILLISECONDS)
        httpBuilder?.connectTimeout(120000, TimeUnit.MILLISECONDS)
        httpBuilder?.retryOnConnectionFailure(true)

        if (!httpBuilder?.interceptors()!!.isEmpty()) {
            httpBuilder?.interceptors()!!.clear()
        }

        httpBuilder?.addInterceptor(JSONHeaderInterceptor())
        httpBuilder?.addNetworkInterceptor(logging!!)

        var client: OkHttpClient = httpBuilder!!.build();

        builder = Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(JacksonConverterFactory.create())

        var retrofit: Retrofit = builder?.client(client)!!.build()

        return retrofit.create(serviceClass)
    }
}