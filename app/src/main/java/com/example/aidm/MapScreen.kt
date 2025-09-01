package com.example.aidm

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

/**
 * A full-screen composable that displays a Google Map.
 *
 * @param initialUserLocation The initial geographical point to center the map on.
 * @param onMapReady A callback that is invoked when the map is fully loaded and ready to be interacted with.
 */
@Composable
fun MapScreen(
    initialUserLocation: LatLng,
    onMapReady: (GoogleMap) -> Unit // Callback to signal when the map is loaded
) {
    // A state object that can be used to control the camera position of the map
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialUserLocation, 14f) // Zoom level 14 is good for city views
    }

    // The main GoogleMap composable
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapLoaded = {
            // This block is called when the map tiles have finished loading.
            // It's a good place to do any initial setup.
        }
    ) {
        // You can add markers, polylines, etc., inside this block.

        // Add a marker for the user's initial location.
        Marker(
            state = MarkerState(position = initialUserLocation),
            title = "Your Location",
            snippet = "This is your starting point"
        )

        // TODO: Add more markers for incidents, shelters, etc., by fetching from a ViewModel.
    }

    // LaunchedEffect can be used to react to events.
    // While the onMapReady callback from the GoogleMap composable is often sufficient,
    // this shows another way to interact once the map is available.
    // For this use case, we'll keep it simple and rely on the UI.
    // The `onMapReady` parameter passed into this function is for the caller (AppNavigation)
    // if it needs to know the map is ready.
}
