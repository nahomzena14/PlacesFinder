package com.coolcats.restaurantfinder.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.viewModels
import com.coolcats.restaurantfinder.R
import com.coolcats.restaurantfinder.util.AppState
import com.coolcats.restaurantfinder.util.MyLocationListener
import com.coolcats.restaurantfinder.view.adapter.LocationAdapter
import com.coolcats.restaurantfinder.viewmodel.PlacesViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var locationManager: LocationManager
    private val viewModel: PlacesViewModel by viewModels()
    private var myLocation: Location? = null
    private lateinit var spinner:Spinner
    private lateinit var currentTypeSelected:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //spinner
        val types:List<String> = listOf("restaurant","cafe","supermarket","bakery")
        currentTypeSelected =types[0]
        val spinnerAdapter = ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,types)
        spinner = spinner_
        spinner.adapter = spinnerAdapter

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        //observe state
        viewModel.statusData.observe(this,{
            statusControl(it)
        })

        val adapter = LocationAdapter()
        recyclerview.adapter = adapter
        //observe list of places
        viewModel.liveData.observe(this, {
            adapter.updateList(it)
        })
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //get type selected from spinner and make a new api call
                myLocation?.let { makeApiCall(it,types[position]).also {
                    currentTypeSelected = types[position]
                } }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //do nothing
            }

        }
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
        object: MyLocationListener.LocationDelegate {
            override fun provideLocation(location: Location) {
                makeApiCall(location,currentTypeSelected)
            }
        }
    )

    private fun makeApiCall(location: Location,type:String) {
        Geocoder(this, Locale.getDefault()).getFromLocation(location.latitude, location.longitude, 1)
            .also { it[0].getAddressLine(0).let { it ->
                currentlocation_textView.text= it
            }}
        myLocation?.set(location)
        viewModel.getPlacesNearMe(location,currentTypeSelected)
    }

    @SuppressLint("MissingPermission")
    override fun onStop() {
        super.onStop()
        locationManager.removeUpdates(myLocationListener)
    }

    private fun statusControl(status: AppState?) {
        when (status) {
            AppState.LOADING -> status_progressbar.visibility = View.VISIBLE
            AppState.SUCCESS -> status_progressbar.visibility = View.GONE
            else -> {
                status_progressbar.visibility = View.GONE
                Snackbar.make(view_main, "An error occurred!", Snackbar.LENGTH_SHORT).show()
            }
        }

    }
}