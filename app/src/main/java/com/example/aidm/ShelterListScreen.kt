package com.example.aidm
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
import androidx.compose.material.icons.filled.LocationOn
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
// REMOVED: LocalContext is no longer needed for starting an Activity.
// import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

val repo = FakeRepo()

suspend fun fetchShelterSummaries(): List<Shelter> {
    delay(1500)
    return repo.getShelterList()
}

@Composable
fun ShelterListScreen(
    // <<< FIX 1: Add the onOpenShelter parameter to the function signature.
    // It's a function that takes a shelter ID (String) and does something (Unit).
    onOpenShelter: (shelterId: String) -> Unit
) {
    var shelters by remember { mutableStateOf<List<Shelter>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    // REMOVED: context is no longer needed
    // val context = LocalContext.current

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
            .padding(top = 16.dp)
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
                        // Pass the shelter and the onOpenShelter lambda down to the item.
                        ShelterListItem(
                            shelter = shelter,
                            // <<< FIX 2: Call the onOpenShelter lambda when the item is clicked.
                            onClick = { onOpenShelter(shelter.id) }
                        )
                    }
                }
            }
        }
    }
}

// NOTE: The ShelterListItem's 'onClick' signature remains the same, which is fine.
// It just needs to be called correctly from the LazyColumn above.
@Composable
fun ShelterListItem(shelter: Shelter, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick), // This onClick now triggers navigation
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = "Shelter Location",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 16.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(shelter.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(shelter.address, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.width(16.dp))
            Text(
                "${shelter.available}/${shelter.capacity}",
                style = MaterialTheme.typography.bodyLarge,
                color = if (shelter.available.toDouble() / shelter.capacity < 0.8) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}
