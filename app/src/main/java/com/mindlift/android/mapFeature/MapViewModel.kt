package com.mindlift.android.mapFeature

import android.content.ContentValues
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import com.mindlift.android.mapFeature.APIService.PlacesApiService
import com.mindlift.android.mapFeature.model.Place
import com.mindlift.android.mapFeature.model.PlacesResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(): ViewModel() {
    fun getDeviceLocation(fusedLocationProviderClient: FusedLocationProviderClient, onSuccess: (Location?) -> Unit) {
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    onSuccess(task.result)
                } else {
                    onSuccess(null)
                }
            }
        } catch (e: SecurityException) {
            onSuccess(null)
        }
    }

    object RetrofitClient {
        private val retrofit = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        val service: PlacesApiService = retrofit.create(PlacesApiService::class.java)
    }


    private val _places = MutableStateFlow<List<Place>>(emptyList())
    val places: StateFlow<List<Place>> = _places.asStateFlow()


    fun searchNearbyGyms(location: LatLng, radius: Int = 100000) {
        val apiKey = "Google_API_KEY"
//        Log.e("LocationService", "lat, long: ${location.latitude},${location.longitude}")
        RetrofitClient.service.searchNearbyPlaces("${location.latitude},${location.longitude}", radius, "gym", apiKey)
            .enqueue(object : Callback<PlacesResponse> {
                override fun onResponse(call: Call<PlacesResponse>, response: Response<PlacesResponse>) {
                    if (response.isSuccessful) {
                        val results = response.body()?.results ?: emptyList()
                        results.forEach { place ->
                            Log.d("SearchNearbyGyms", "Found gym: ${place.name} at lat: ${place.geometry.location.lat}, lng: ${place.geometry.location.lng}")
                        }
                        _places.value = results.map{ place ->
//                            Place(place.lat, place.lng, place.name)
                            Place(place.id, place.name, place.geometry)
                        }
                    } else {
                        Log.e(ContentValues.TAG, "API Error: Response Code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<PlacesResponse>, t: Throwable) {
                    Log.e(ContentValues.TAG, "API Error: ${t.message}")
                }

            })
    }
    fun submitReviewForPlace(place: Place, reviewText: String) {
        Log.d("MapViewModel", "Submitting review for ${place.name}: $reviewText")
        // sendReviewToServer(place.id, reviewText)
        // _reviews.value = _reviews.value + Review(place.id, reviewText)
    }






}