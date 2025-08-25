package com.example.aidm

import androidx.activity.ComponentActivity
import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun RouteScreen() {
    var from by remember { mutableStateOf("19.0760,72.8777") } // Mumbai
    var to by remember { mutableStateOf("19.2183,72.9781") }   // Thane
    var draw by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        Column(Modifier.padding(12.dp)) {
            OutlinedTextField(from, { from = it }, label = { Text("From (lat,lng)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(to, { to = it }, label = { Text("To (lat,lng)") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Button(onClick = { draw = true }, modifier = Modifier.fillMaxWidth()) { Text("Find Safe Route (Mock)") }
        }
        val start = from.split(",").mapNotNull { it.trim().toDoubleOrNull() }
        val end = to.split(",").mapNotNull { it.trim().toDoubleOrNull() }
        val s = if (start.size == 2) LatLng(start[0], start[1]) else LatLng(19.076, 72.8777)
        val e = if (end.size == 2) LatLng(end[0], end[1]) else LatLng(19.2183, 72.9781)
        val camState = rememberCameraPositionState { position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(s, 10f) }

        GoogleMap(Modifier.weight(1f), cameraPositionState = camState) {
            Marker(state = MarkerState(s), title = "Start")
            Marker(state = MarkerState(e), title = "Destination")
            if (draw) {
                // Straight line mock "safe route"
                Polyline(points = listOf(s, e))
            }
        }
    }
}
