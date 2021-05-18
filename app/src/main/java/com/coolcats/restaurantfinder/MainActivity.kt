package com.coolcats.restaurantfinder

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.coolcats.restaurantfinder.util.State
import com.coolcats.restaurantfinder.viewmodel.PlacesViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var locationManager: LocationManager
    private val viewModel: PlacesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        viewModel.liveData.observe(this, {
            it.forEach { place ->
                Log.d("TAG_X", "${place.name}")
            }
        })

        viewModel.statusData.observe(this, {
            when(it){
                State.LOADING -> status_progressbar.visibility = View.VISIBLE
                State.ERROR -> displayError()
                else -> status_progressbar.visibility = View.GONE
            }
        })
    }

    private fun displayError() {
        status_progressbar.visibility = View.GONE
        Snackbar.make(view_main, "An error occurred!", Snackbar.LENGTH_SHORT).show()
    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000L,
                5f,
                myLocationListener
        )
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    private val myLocationListener = MyLocationListener(
        object: MyLocationListener.LocationDelegate{
            override fun provideLocation(location: Location) {
                makeApiCall(location)
            }
        }
    )

    private fun makeApiCall(location: Location) {
        viewModel.getPlacesNearMe(location)
    }

    @SuppressLint("MissingPermission")
    override fun onStop() {
        super.onStop()
        locationManager.removeUpdates(myLocationListener)
    }
}