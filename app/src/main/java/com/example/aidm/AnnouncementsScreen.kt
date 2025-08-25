package com.example.aidm

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.aidm.app.data.FakeRepo
import com.aidm.app.ui.components.Loading
import kotlinx.coroutines.launch

@Composable
fun AnnouncementsScreen() {
    val repo = remember { FakeRepo() }
    var msgs by remember { mutableStateOf<List<String>?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { scope.launch { msgs = repo.getAnnouncements() } }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Official Announcements", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        val list = msgs
        if (list == null) Loading() else list.forEach {
            Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                Text(it, Modifier.padding(12.dp))
            }
        }
    }
}
