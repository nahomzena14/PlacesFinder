package com.coolcats.restaurantfinder.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coolcats.restaurantfinder.network.GooglePlaceRetrofit
import com.coolcats.restaurantfinder.model.Result
import com.coolcats.restaurantfinder.util.FunctionUtility.Companion.toFormattedString
import com.coolcats.restaurantfinder.util.AppState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PlacesViewModel: ViewModel() {

    private var netJob: Job? = null

    private val retrofit = GooglePlaceRetrofit()

    val liveData: MutableLiveData<List<Result>> = MutableLiveData()

    val statusData = MutableLiveData<AppState>()

    fun getPlacesNearMe(location: Location,currentTypeSelected:String){

        statusData.value = AppState.LOADING

        netJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = retrofit.makeApiCallAsync(
                    currentTypeSelected,
                    "",
                    location.toFormattedString(),
                    10000
                ).await()

                liveData.postValue(result.results)
                statusData.postValue(AppState.SUCCESS)

            } catch (e: Exception) {
                //At this point an error occurred
                statusData.postValue(AppState.ERROR)
            }

        }
    }

    override fun onCleared() {
        netJob?.cancel()
        super.onCleared()
    }
}