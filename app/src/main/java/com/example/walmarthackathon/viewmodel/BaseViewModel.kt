package com.example.walmarthackathon.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {

    var empty = MutableLiveData<Boolean>().apply { value = false }

    var dataLoading = MutableLiveData<Boolean>().apply { value = false }

}