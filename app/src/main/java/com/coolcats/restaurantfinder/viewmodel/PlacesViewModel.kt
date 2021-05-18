package com.coolcats.restaurantfinder.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coolcats.restaurantfinder.GooglePlaceRetrofit
import com.coolcats.restaurantfinder.model.PlacesResponse
import com.coolcats.restaurantfinder.model.Result
import com.coolcats.restaurantfinder.util.FunctionUtility.Companion.toFormattedString
import com.coolcats.restaurantfinder.util.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlacesViewModel: ViewModel() {

    private var netJob: Job? = null

    private val retrofit = GooglePlaceRetrofit()

    val liveData: MutableLiveData<List<Result>> = MutableLiveData()

    val statusData = MutableLiveData<State>()

    fun getPlacesNearMe(location: Location){

        statusData.value = State.LOADING

        netJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = retrofit.makeApiCallAsync(
                    "restaurant",
                    "chicken",
                    location.toFormattedString(),
                    10000
                ).await()

                liveData.postValue(result.results)
                statusData.postValue(State.SUCCESS)

            } catch (e: Exception) {
                //At this point an error occurred
                Log.d("TAG_X", e.toString())
                statusData.postValue(State.ERROR)
            }

        }

       /* retrofit.makeApiCall(
            "restaurant",
            "chicken",
            location.toFormattedString(),
            10000
        ).enqueue(object: Callback<PlacesResponse> {
            override fun onResponse(
                call: Call<PlacesResponse>,
                response: Response<PlacesResponse>
            ) {
                response.body()?.let {
                    liveData.value = it.results
                    Log.d("TAG_X", "${it.results.size}, Success -> ${call.request().url()}")
                } ?: {
                    Log.d("TAG_X", "At this point...")
                    //At this point the body is null
                }()
            }
            override fun onFailure(call: Call<PlacesResponse>, t: Throwable) {
                //Handle the error
                Log.d("TAG_X", "At this point..${t.localizedMessage}")
            }
        })*/

    }

    override fun onCleared() {
        netJob?.cancel()
        super.onCleared()
    }
}