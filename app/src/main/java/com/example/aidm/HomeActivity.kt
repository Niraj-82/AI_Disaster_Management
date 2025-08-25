package com.example.aidm

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Campaign // For Announcements
import androidx.compose.material.icons.filled.Gite // For Shelters
import androidx.compose.material.icons.filled.Home // Example for Dashboard
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MedicalServices // For First Aid
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Gite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.ReportProblem
import androidx.compose.material3.*
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aidm.ui.theme.AIDMTheme

// Sealed class to represent different screens accessible from Home
sealed class HomeScreen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Dashboard : HomeScreen("dashboard", "Dashboard", Icons.Filled.Home, Icons.Outlined.Home)
    object MapView : HomeScreen("map", "Map", Icons.Filled.Map, Icons.Outlined.Map)
    object Report : HomeScreen("report", "Report Incident", Icons.Filled.ReportProblem, Icons.Outlined.ReportProblem)
    object Shelters : HomeScreen("shelters", "Shelters", Icons.Filled.Gite, Icons.Outlined.Gite)
    object FirstAid : HomeScreen("first_aid", "First Aid", Icons.Filled.MedicalServices, Icons.Outlined.MedicalServices)
    object Announcements : HomeScreen("announcements", "Announcements", Icons.Filled.Campaign, Icons.Outlined.Campaign)
    object Profile : HomeScreen("profile", "Profile", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)
    // Add other primary navigation destinations here
}

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AIDMTheme {
                MainHomeScreen()
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class) // For TopAppBar and NavigationBar
@androidx.compose.runtime.Composable
fun MainHomeScreen() {
    val context = LocalContext.current
    var currentScreen by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<HomeScreen>(HomeScreen.Dashboard) }

    val navigationItems = listOf(
        HomeScreen.Dashboard,
        HomeScreen.MapView,
        HomeScreen.Report,
        HomeScreen.Shelters,
        // Add more items to the bottom bar if they fit, or move some to a NavDrawer/menu
        HomeScreen.Profile
    )

    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { androidx.compose.material3.Text(currentScreen.title) },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer
                )
                // You can add navigationIcon for a drawer or actions items here
            )
        },
        bottomBar = {
            androidx.compose.material3.NavigationBar(
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
            ) {
                navigationItems.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            androidx.compose.material3.Icon(
                                imageVector = if (currentScreen.route == screen.route) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.title
                            )
                        },
                        label = { androidx.compose.material3.Text(screen.title) },
                        selected = currentScreen.route == screen.route,
                        onClick = {
                            if (currentScreen.route != screen.route) { // Prevent reloading same screen
                                currentScreen = screen // Update the content
                                // Actual navigation to other activities happens in HomeContent
                            }
                        },
                        colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                            selectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                            unselectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                            selectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                            unselectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer // Ripple color
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        HomeContent(
            modifier = Modifier.padding(innerPadding),
            currentScreen = currentScreen,
            onNavigateToActivity = { activityClass ->
                context.startActivity(Intent(context, activityClass))
            }
        )
    }
}

@androidx.compose.runtime.Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    currentScreen: HomeScreen,
    onNavigateToActivity: (Class<out ComponentActivity>) -> Unit
) {
    // This is where you decide what content to show based on `currentScreen`
    // For screens that are separate Activities, we trigger navigation.
    // For screens that are simple Composables, we can display them directly.

    androidx.compose.runtime.LaunchedEffect(currentScreen) { // React to changes in currentScreen
        when (currentScreen) {
            HomeScreen.MapView -> onNavigateToActivity(MapActivity::class.java)
            HomeScreen.Report -> onNavigateToActivity(ReportIncidentActivity::class.java)
            HomeScreen.Shelters -> onNavigateToActivity(ShelterListActivity::class.java)
            HomeScreen.FirstAid -> onNavigateToActivity(FirstAidActivity::class.java) // Assuming you have this
            HomeScreen.Announcements -> onNavigateToActivity(AnnouncementsActivity::class.java) // Assuming
            HomeScreen.Profile -> onNavigateToActivity(ProfileActivity::class.java) // Assuming
            // For Dashboard or other simple composable-only screens, they'd be handled below
            else -> { /* Current screen is handled by the Column below or is a no-op for activity nav */ }
        }
    }

    // Display content for screens that are NOT separate activities, or a placeholder
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        when (currentScreen) {
            HomeScreen.Dashboard -> {
                androidx.compose.material3.Text("Welcome to the Dashboard!", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
                androidx.compose.material3.Button(onClick = { onNavigateToActivity(RouteActivity::class.java) }) { // Example Button
                    androidx.compose.material3.Text("Show Example Route")
                }
                // Add more dashboard elements here
            }
            // Add cases for other screens if they are simple composables and not full Activities
            // e.g., if Profile was a simple Composable screen within HomeActivity:
            // HomeScreen.Profile -> ProfileContentComposable()
            else -> {
                // This state might be transient while an Activity is being launched.
                // Or if it's a screen handled by LaunchedEffect above, this is a fallback.
                androidx.compose.material3.Text(
                    "Selected: ${currentScreen.title}",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
                )
                androidx.compose.material3.Text(
                    "(Navigating to separate activity if applicable)",
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}


@Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun MainHomeScreenPreview() {
    AIDMTheme {
        MainHomeScreen()
    }
}

