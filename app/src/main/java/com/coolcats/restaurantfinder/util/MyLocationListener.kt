package com.coolcats.restaurantfinder.util

import android.location.Location
import android.location.LocationListener

class MyLocationListener(private val locationDelegate: LocationDelegate): LocationListener {

    interface LocationDelegate {
        fun provideLocation(location: Location)
    }
    override fun onLocationChanged(location: Location) {
        locationDelegate.provideLocation(location)
    }
}