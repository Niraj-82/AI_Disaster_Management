package com.example.aidm

import androidx.activity.ComponentActivity
import android.os.Bundle
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen() {
    val fineLoc = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
    LaunchedEffect(Unit) { if (!fineLoc.status.isGranted) fineLoc.launchPermissionRequest() }

    val mumbai = LatLng(19.0760, 72.8777)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(mumbai, 10f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = fineLoc.status.isGranted),
        uiSettings = MapUiSettings(zoomControlsEnabled = true)
    ) {
        // Mock hazards
        Marker(state = MarkerState(LatLng(19.09, 72.88)), title = "Flooded Area")
        Marker(state = MarkerState(LatLng(19.2, 72.98)), title = "Road Blocked")
        // Mock safe zones
        Marker(state = MarkerState(LatLng(19.12, 72.86)), title = "Shelter: City Hall")
    }
}
