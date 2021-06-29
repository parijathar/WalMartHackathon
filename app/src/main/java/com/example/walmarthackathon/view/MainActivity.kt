package com.example.walmarthackathon.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.walmarthackathon.R
import com.example.walmarthackathon.databinding.ActivityMainBinding
import com.example.walmarthackathon.util.Utility
import com.example.walmarthackathon.viewmodel.NasaDataViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    var sharedPreferences: SharedPreferences? = null
    private lateinit var viewModel: NasaDataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        sharedPreferences = this.getSharedPreferences(getString(R.string.sharedPreference), Context.MODE_PRIVATE)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(application)
        ).get(NasaDataViewModel::class.java)

        observeData()

        if (Utility.isInternetAvailable(this)) {
            viewModel.fetchApodNasa()
        } else {
            observeSharedPreferenceLoading()
            viewModel.loadFromSharedPreference(sharedPreferences)
        }
    }

    fun observeData() {
        viewModel.dataLoading.observe(this, Observer {
            if (it == true) {
                binding?.loader!!.visibility = View.VISIBLE
            } else {
                binding?.loader!!.visibility = View.GONE
            }
        })

        viewModel.apodNasaReponse.observe(this, Observer {
            if (it?.media_type!!.equals(getString(R.string.image))) {
                observeImageLoading()
                GlobalScope.async(Dispatchers.IO) {
                    viewModel.loadImage(it)
                }
            } else {
                binding?.noDetailsText!!.text = getString(R.string.media_not_image)
                binding?.noDetailsText!!.visibility = View.VISIBLE
            }
        })

        viewModel.empty.observe(this, Observer {
            if (it == true) {
                Toast.makeText(this, getString(R.string.error_occured), Toast.LENGTH_SHORT).show()
                binding?.noDetailsText!!.visibility = View.VISIBLE
                binding?.noDetailsText!!.text = getString(R.string.error_occured)
            } else {
                binding?.noDetailsText!!.visibility = View.GONE
            }
        })

    }

    fun observeImageLoading() {
        viewModel.encodedString.observe(this, Observer {
            Utility.saveToSharedPreference(
                sharedPreferences,
                it, viewModel.apodNasaReponse.value?.title,
                viewModel.apodNasaReponse.value?.explanation
            )
            var pureBase64Encoded =
                it?.substring(it?.indexOf(",")!!.plus(1))
            val decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT)
            binding?.cardview!!.visibility = View.VISIBLE
            Glide.with(baseContext).load(decodedBytes).fitCenter().into(binding!!.imageView)
            displayData(
                viewModel.apodNasaReponse.value?.title,
                viewModel.apodNasaReponse.value?.explanation
            )
        })
    }

    fun observeSharedPreferenceLoading() {
        viewModel.noDetailString.observe(this, Observer {
            if (it == true) {
                binding?.noDetailsText!!.visibility = View.VISIBLE
                binding?.noDetailsText!!.text = getString(R.string.no_data_available)
            } else {
                binding?.noDetailsText!!.visibility = View.GONE
            }
        })

        viewModel.sharedPreferencesModel.observe(this, Observer {
            var pureBase64Encoded = it?.encodedString;
            pureBase64Encoded = pureBase64Encoded?.substring(pureBase64Encoded?.indexOf(",")!!.plus(1))
            val decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT)
            binding?.cardview!!.visibility = View.VISIBLE
            Glide.with(baseContext).load(decodedBytes).fitCenter().into(binding!!.imageView)
            displayData(
                it?.title,
                it?.description
            )
        })
    }

    fun displayData(title: String?, description: String?) {
        binding?.titleText!!.text = title
        binding?.descriptionText!!.text = description
    }

}