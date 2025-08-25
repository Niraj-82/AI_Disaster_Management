package com.example.aidm

import androidx.activity.ComponentActivity
import android.os.Bundle
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.aidm.app.data.FakeRepo
import com.aidm.app.ui.components.Loading
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ReportIncidentScreen() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { FakeRepo() }

    var type by remember { mutableStateOf("Flood") }
    var desc by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf<Double?>(null) }
    var lng by remember { mutableStateOf<Double?>(null) }
    var loading by remember { mutableStateOf(false) }
    val fine = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(Unit) { if (!fine.status.isGranted) fine.launchPermissionRequest() }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Report Incident", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(type, { type = it }, label = { Text("Type") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(desc, { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Row {
            OutlinedButton(onClick = {
                if (fine.status.isGranted) {
                    @SuppressLint("MissingPermission")
                    val client = LocationServices.getFusedLocationProviderClient(ctx)
                    client.lastLocation.addOnSuccessListener { loc ->
                        lat = loc?.latitude; lng = loc?.longitude
                    }
                }
            }) { Text("Use Current Location") }
            Spacer(Modifier.width(8.dp))
            Text("Lat: ${lat ?: "-"}  Lng: ${lng ?: "-"}")
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = {
            loading = true
            scope.launch {
                val res = repo.submitIncident(
                    type, desc,
                    lat ?: 19.0760, lng ?: 72.8777
                )
                loading = false
                desc = ""
            }
        }, modifier = Modifier.fillMaxWidth()) { Text("Submit") }

        if (loading) Loading()
    }
}
