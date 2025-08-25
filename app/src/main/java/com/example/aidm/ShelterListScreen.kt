package com.example.aidm

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn // Example icon
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.isEmpty
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.error
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.weight
import kotlinx.coroutines.delay // For simulating network delay

// Placeholder data class for Shelter Summary
data class ShelterSummary(
    val id: String,
    val name: String,
    val shortAddress: String,
    val currentOccupancy: Int,
    val capacity: Int
)

// Dummy function to simulate fetching a list of shelters
suspend fun fetchShelterSummaries(): List<ShelterSummary> {
    delay(1500) // Simulate network delay
    return listOf(
        ShelterSummary("shelter123", "Hope Community Shelter", "123 Main St", 75, 100),
        ShelterSummary("shelter456", "Safe Haven Center", "456 Oak Ave", 30, 50),
        ShelterSummary("shelter789", "New Beginnings Home", "789 Pine Ln", 90, 120),
        ShelterSummary("shelterABC", "Downtown Relief Point", "101 State St", 15, 20)
    )
}

@Composable
fun ShelterListScreen() {
    var shelters by remember { mutableStateOf<List<ShelterSummary>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        isLoading = true
        error = null
        try {
            shelters = fetchShelterSummaries()
        } catch (e: Exception) {
            error = "Failed to load shelters: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp) // Add padding at the top if there's no AppBar
    ) {
        Text(
            text = "Available Shelters",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(16.dp))

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            shelters.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No shelters available at the moment.", modifier = Modifier.padding(16.dp))
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(shelters, key = { it.id }) { shelter ->
                        ShelterListItem(shelter = shelter) {
                            // Navigate to ShelterDetailActivity
                            val intent = Intent(context, ShelterDetailActivity::class.java).apply {
                                putExtra(ShelterDetailActivity.EXTRA_SHELTER_ID, shelter.id)
                            }
                            context.startActivity(intent)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShelterListItem(shelter: ShelterSummary, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn, // Example icon
                contentDescription = "Shelter Location",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 16.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(shelter.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(shelter.shortAddress, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.width(16.dp))
            Text(
                "${shelter.currentOccupancy}/${shelter.capacity}",
                style = MaterialTheme.typography.bodyLarge,
                color = if (shelter.currentOccupancy.toDouble() / shelter.capacity < 0.8) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}
