package com.example.aidm

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aidm.app.data.FakeRepo // Assuming this path is correct for your FakeRepo
import com.aidm.app.ui.components.Loading // Assuming this path is correct for your Loading component

import kotlinx.coroutines.launch

@Composable
fun FirstAidScreen() { // This remains your Composable function
    val repo = remember { FakeRepo() }
    var data by remember { mutableStateOf<Map<String, List<String>>?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            data = repo.getFirstAidTopics()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("First Aid Guide", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))

        val topics = data
        if (topics == null) {
            Loading()
            return // Prevent further execution if topics are null
        }

        topics.forEach { (title, steps) ->
            Text("• $title", style = MaterialTheme.typography.titleMedium)
            steps.forEach { step ->
                Text("   - $step")
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
