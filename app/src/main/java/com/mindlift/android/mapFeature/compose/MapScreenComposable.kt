package com.mindlift.android.mapFeature.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mindlift.android.mapFeature.MapViewModel
import com.mindlift.android.mapFeature.model.Place
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import com.mindlift.android.mapFeature.APIService.FirestoreService
import com.mindlift.android.mapFeature.Database.Review


@Composable
fun MapScreenComposable() {
    val viewModel: MapViewModel = viewModel()
    val places by viewModel.places.collectAsState()
    val context = LocalContext.current
    val fusedLocationProviderClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    var showDialog by remember { mutableStateOf(false) }
    var selectedPlace by remember { mutableStateOf<Place?>(null) }


    LaunchedEffect(true) {
        viewModel.getDeviceLocation(fusedLocationProviderClient) { location ->
            location?.let {
                viewModel.searchNearbyGyms(LatLng(location.latitude, location.longitude))
            }
        }
    }

    GoogleMapComposable(places = places,
        onPlaceSelected = { place ->
            selectedPlace = place
            showDialog = true
        },
        selectedPlace = selectedPlace
    )

    if (showDialog && selectedPlace != null) {
        ReviewDialog(
            place = selectedPlace,
            onDismiss = {
                showDialog = false
                selectedPlace = null
            },
            //submit comments
            onSubmit = { reviewText ->
                viewModel.submitReviewForPlace(selectedPlace!!, reviewText)

                showDialog = false
                selectedPlace = null
            }
        )
    }

//    LaunchedEffect(true) { // Unit ensures it runs only once after composition
//        FirestoreService.fetchHighestRatedPlace(context)
//    }

   
}

@Composable
fun GoogleMapComposable(
    places: List<Place>,
    onPlaceSelected: (Place) -> Unit,
    selectedPlace: Place?) {

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(45.4162875935326, -75.6721819609751), 10f)
    }


    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        places.forEach { place ->
            Marker(
                state = MarkerState(position = LatLng(place.geometry.location.lat, place.geometry.location.lng)),
                title = place.name,
                snippet = "Snippet here",
                onClick = {
                    onPlaceSelected(place)
                    true // the click event has been processed
                }
            )
        }
    }

    LaunchedEffect(places) {
        if (places.isNotEmpty()) {
            val boundsBuilder = LatLngBounds.builder()
            places.forEach { place ->
                boundsBuilder.include(LatLng(place.geometry.location.lat, place.geometry.location.lng))
            }
            val bounds = boundsBuilder.build()
            cameraPositionState.move(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        }
    }
}

@Composable
fun ReviewDialog( place: Place?,
                  onDismiss: () -> Unit,
                  onSubmit: (String) -> Unit
                  ) {
    var reviewText by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }

    if (place != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = "Leave a Review")
            },
            text = {
                Column {
                    Text("How was your experience at ${place.name}?")
                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = { newText ->
                            reviewText = newText },
                        label = { Text("Your review") }
                    )
                    OutlinedTextField(
                        value = rating,
                        onValueChange = { newRating ->
                            rating = newRating },
                        label = { Text("Your Rating") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSubmit(reviewText)
                        val newReview = Review(placeId = place.id, place = place.name, reviewText = reviewText, rating = rating.toInt())
                        FirestoreService.submitReview(
                            newReview,
                            onSuccess = {
                                reviewText = ""
                                onDismiss()
                            },
                            onFailure = { exception ->
                                // Handle the error
                            }
                        )
                        reviewText = ""
                        onDismiss()
                    }
                ) {
                    Text("Submit")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}




