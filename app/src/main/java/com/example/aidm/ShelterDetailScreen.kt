package com.example.aidm

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.aidm.app.data.FakeRepo
import com.aidm.app.model.Shelter
import com.aidm.app.ui.components.Loading
import kotlinx.coroutines.launch
import androidx.activity.ComponentActivity
import android.os.Bundle

@Composable
fun ShelterDetailScreen(id: String) {
    val repo = remember { FakeRepo() }
    var shelter by remember { mutableStateOf<Shelter?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(id) {
        scope.launch { shelter = repo.getShelter(id) }
    }

    val s = shelter
    if (s == null) { Loading(); return }
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
