package com.example.aidm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel // For potentially sharing ViewModel

@Composable
fun AnnouncementsScreen(
    // Use the same shared ViewModel instance
    sharedViewModel: SharedIncidentViewModel = viewModel()
) {
    // val repo = remember { FakeRepo() } // Use the one from ViewModel
    val repo = sharedViewModel.getRepository()

    // Option A: Collect the raw incidents and format them here
    // val incidents by repo.getIncidentsFlow().collectAsState()

    // Option B: Collect pre-formatted announcements (if getAnnouncements() is a Flow)
    // For simplicity with the current FakeRepo.getAnnouncements() being a suspend fun:
    var announcements by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) { // Re-launch if repo instance changes, though ViewModel should keep it stable
        isLoading = true
        announcements = repo.getAnnouncements() // Call the suspend fun
        isLoading = false
    }

    // If you want it to auto-update when new incidents are added via FakeRepo's StateFlow:
    val incidentsFromFlow by repo.getIncidentsFlow().collectAsState()
    LaunchedEffect(incidentsFromFlow) { // Re-fetch announcements when incidents change
        isLoading = true
        announcements = repo.getAnnouncements() // Re-derive announcements
        isLoading = false
    }


    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Latest Incident Reports", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (announcements.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No incident reports yet.")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(announcements) { announcement ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = announcement,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

