package com.example.walmarthackathon.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.example.fiapp.model.ApodNasaReponse
import com.example.walmarthackathon.model.SharedPreferenceModel
import com.example.walmarthackathon.repository.NasaRepository
import kotlinx.coroutines.*
import okhttp3.internal.lockAndWaitNanos
import okhttp3.internal.wait

class NasaDataViewModel : BaseViewModel() {
    var apodNasaReponse = MutableLiveData<ApodNasaReponse>()
    var encodedString = MutableLiveData<String>()
    var noDetailString = MutableLiveData<Boolean>().apply { value = false }
    var sharedPreferencesModel = MutableLiveData<SharedPreferenceModel>()

    fun fetchApodNasa() {
        dataLoading.value = true
        NasaRepository.getInstance().apodNasaApi { isSuccess, response ->
            if (isSuccess) {
                apodNasaReponse.value = response
                empty.value = false
            } else {
                empty.value = true
                dataLoading.value = false
            }
        }
    }

    fun loadImage(apodNasaReponse: ApodNasaReponse?) {
        NasaRepository.getInstance()
            .getByteArrayFromImageURL(apodNasaReponse?.url) { isSuccess, encoded ->

                dataLoading.postValue(false)
                if (isSuccess) {
                    encodedString.postValue(encoded)
                    empty.postValue(false)
                } else {
                    empty.postValue(true)
                }
            }

    }

    fun loadFromSharedPreference(sharedPreferences: SharedPreferences?) {
        NasaRepository.getInstance()
            .loadDataSharedPreference(sharedPreferences) { isSuccess, response ->
                if (isSuccess) {
                    noDetailString.value = false
                    sharedPreferencesModel.value = response
                } else {
                    noDetailString.value = true
                }
            }
    }
}