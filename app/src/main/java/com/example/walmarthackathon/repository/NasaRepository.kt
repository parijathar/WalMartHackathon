package com.example.walmarthackathon.repository

import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import com.example.fiapp.model.ApodNasaReponse
import com.example.fiapp.network.NetworkApi
import com.example.fiapp.network.NetworkServiceGenerator
import com.example.walmarthackathon.BuildConfig
import com.example.walmarthackathon.model.SharedPreferenceModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL
import java.net.URLConnection

class NasaRepository {

    fun apodNasaApi(onResult: (isSuccess: Boolean, response: ApodNasaReponse?) -> Unit) {
        val networkApi: NetworkApi = NetworkServiceGenerator.createService(
            NetworkApi::class.java, BuildConfig.API_BASE_PATH
        )
        val call: Call<ApodNasaReponse?>? = networkApi.getApodNASA(BuildConfig.API_KEY)
        call?.enqueue(object : Callback<ApodNasaReponse?> {
            override fun onFailure(call: Call<ApodNasaReponse?>, t: Throwable) {
                onResult(false, null)
            }

            override fun onResponse(
                call: Call<ApodNasaReponse?>,
                response: Response<ApodNasaReponse?>
            ) {
                if (response != null && response.isSuccessful)
                    onResult(true, response.body()!!)
                else
                    onResult(false, null)
            }
        })
    }

    fun getByteArrayFromImageURL(
        url: String?,
        onResult: (isSuccess: Boolean, encoded: String?) -> Unit
    ) {
        try {
            val imageUrl = URL(url)
            val ucon: URLConnection = imageUrl.openConnection()
            val `is`: InputStream = ucon.getInputStream()
            val baos = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var read = 0
            while (`is`.read(buffer, 0, buffer.size).also { read = it } != -1) {
                baos.write(buffer, 0, read)
            }
            baos.flush()
            onResult(true, Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT))
        } catch (e: Exception) {
            onResult(false, null)
        }
    }

    fun loadDataSharedPreference(
        sharedPreferences: SharedPreferences?,
        onResult: (isSuccess: Boolean, response: SharedPreferenceModel?) -> Unit
    ) {
        var pureBase64Encoded: String? = sharedPreferences?.getString("image", "image")
        if (pureBase64Encoded!!.equals("image")) {
            onResult(false, null)
        } else {
            var sharedPreferenceModel: SharedPreferenceModel? = SharedPreferenceModel(
                sharedPreferences?.getString("title", "title"),
                sharedPreferences?.getString("description", "description"),
                pureBase64Encoded
            )
            onResult(true, sharedPreferenceModel)
        }
    }

    companion object {
        private var INSTANCE: NasaRepository? = null
        fun getInstance() = INSTANCE
            ?: NasaRepository().also {
                INSTANCE = it
            }
    }
}