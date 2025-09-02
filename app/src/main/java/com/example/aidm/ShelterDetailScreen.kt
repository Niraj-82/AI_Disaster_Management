package com.example.aidm

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ShelterDetailScreen(id: String, viewModel: ShelterViewModel = viewModel()) {
    val shelter by viewModel.shelter

    LaunchedEffect(id) {
        viewModel.loadShelter(id)
    }

    val s = shelter
    if (s == null) {
        Loading()
        return
    }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(s.name, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(s.address)
        Text("Capacity: ${s.available}/${s.capacity}")
        Spacer(Modifier.height(12.dp))
        Button(onClick = { /* TODO: reserve/request help */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Request Help / Reserve")
        }
    }
}
