package com.example.aidm // Or your actual UI package

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Assuming FakeRepo is in the same package or imported correctly
// import com.example.aidm.data.FakeRepo // If FakeRepo is in a 'data' sub-package

@Composable
fun FirstAidScreen(
    // You might pass FakeRepo or a ViewModel that uses FakeRepo
    // For simplicity, instantiating it here or using a shared ViewModel instance
    // For a shared instance across screens, a ViewModel is preferred:
    // viewModel: FirstAidViewModel = viewModel()
) {
    // If using a ViewModel:
    // val repo = viewModel.getRepository()

    // For direct use (or if ViewModel provides FakeRepo directly):
    val repo = remember { FakeRepo() } // Or get from a shared ViewModel

    var firstAidTopics by remember { mutableStateOf<Map<String, List<String>>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Use LaunchedEffect to fetch data when the screen is first composed
    LaunchedEffect(key1 = Unit) { // key1 = Unit ensures it runs once on composition
        isLoading = true
        errorMessage = null
        try {
            firstAidTopics = repo.getFirstAidTopics()
        } catch (e: Exception) {
            // In a real app, handle exceptions more gracefully (e.g., logging)
            errorMessage = "Failed to load first aid topics: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "First Aid Guide",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when {
            isLoading -> {
                // Show a loading indicator
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                // Show an error message
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            firstAidTopics.isNullOrEmpty() -> {
                // Show a message if no topics are available
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No first aid topics available at the moment.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            else -> {
                // Display the list of topics and their steps
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp) // Add padding at the bottom of the list
                ) {
                    items(firstAidTopics!!.toList()) { (topicTitle, steps) ->
                        FirstAidTopicCard(title = topicTitle, steps = steps)
                    }
                }
            }
        }
    }
}

@Composable
fun FirstAidTopicCard(title: String, steps: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            steps.forEach { step ->
                Text(
                    text = step, // Assuming steps are already numbered or formatted
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
