package com.example.aidm

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aidm.ui.theme.AIDMTheme;

class TopBarDemoActivity : ComponentActivity() { // Renamed for clarity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AIDMTheme {
                TopBarDemoScreen(
                    onNavigateUp = { finish() } // Simple back navigation
                )
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@androidx.compose.runtime.Composable
fun TopBarDemoScreen(onNavigateUp: () -> Unit) {
    val context = LocalContext.current
    var showOptionsMenu by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    // For scroll behaviors (e.g., enterAlways, exitUntilCollapsed)
    // val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    // For a pinned TopAppBar (no scroll behavior)
    val scrollBehavior = androidx.compose.material3.TopAppBarDefaults.pinnedScrollBehavior(androidx.compose.material3.rememberTopAppBarState())


    androidx.compose.material3.Scaffold(
        // Apply the scrollBehavior to the scaffold's modifier to connect scrolling
        // with the TopAppBar.
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { androidx.compose.material3.Text("App Bar Demo") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onNavigateUp) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                actions = {
                    androidx.compose.material3.IconButton(onClick = {
                        Toast.makeText(context, "Search clicked", Toast.LENGTH_SHORT).show()
                    }) {
                        androidx.compose.material3.Icon(Icons.Filled.Search, contentDescription = "Search")
                    }
                    androidx.compose.material3.IconButton(onClick = {
                        Toast.makeText(context, "Favorite clicked", Toast.LENGTH_SHORT).show()
                    }) {
                        androidx.compose.material3.Icon(Icons.Filled.Favorite, contentDescription = "Favorite")
                    }
                    Box { // Needed for proper positioning of DropdownMenu
                        androidx.compose.material3.IconButton(onClick = { showOptionsMenu = true }) {
                            androidx.compose.material3.Icon(Icons.Filled.MoreVert, contentDescription = "More options")
                        }
                        androidx.compose.material3.DropdownMenu(
                            expanded = showOptionsMenu,
                            onDismissRequest = { showOptionsMenu = false }
                        ) {
                            androidx.compose.material3.DropdownMenuItem(
                                text = { androidx.compose.material3.Text("Settings") },
                                onClick = {
                                    Toast.makeText(context, "Settings selected", Toast.LENGTH_SHORT).show()
                                    showOptionsMenu = false
                                }
                            )
                            androidx.compose.material3.DropdownMenuItem(
                                text = { androidx.compose.material3.Text("Help") },
                                onClick = {
                                    Toast.makeText(context, "Help selected", Toast.LENGTH_SHORT).show()
                                    showOptionsMenu = false
                                }
                            )
                        }
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                    titleContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                ),
                scrollBehavior = scrollBehavior // Assign the scroll behavior
            )
        }
    ) { innerPadding ->
        // Content that can scroll, demonstrating the scroll behavior
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), // Essential: apply innerPadding
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
        ) {
            items(50) { index ->
                androidx.compose.material3.Text("Item #$index", modifier = Modifier.padding(vertical = 8.dp))
                androidx.compose.material3.Divider()
            }
        }
    }
}

@Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun TopBarDemoScreenPreview() {
    AIDMTheme {
        TopBarDemoScreen(onNavigateUp = {})
    }
}
