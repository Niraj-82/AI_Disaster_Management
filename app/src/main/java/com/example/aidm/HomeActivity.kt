package com.example.aidm

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Campaign // Standard icon
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MedicalServices // Standard icon for First Aid
import androidx.compose.material.icons.filled.NightShelter // Good icon for Shelters
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.NightShelter
import androidx.compose.material.icons.outlined.ReportProblem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aidm.AIDMTheme // Assuming this is your theme's location

// Sealed class to represent different screens/destinations accessible from Home
sealed class HomeScreen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val targetActivity: Class<out ComponentActivity>? = null // Optional: For navigation to a separate Activity
) {
    object Dashboard : HomeScreen("dashboard", "Dashboard", Icons.Filled.Home, Icons.Outlined.Home)
    object MapView : HomeScreen("map", "Map", Icons.Filled.Map, Icons.Outlined.Map, MapActivity::class.java)
    object Report : HomeScreen("report", "Report Incident", Icons.Filled.ReportProblem, Icons.Outlined.ReportProblem, ReportIncidentActivity::class.java)
    object Shelters : HomeScreen("shelters", "Shelters", Icons.Filled.NightShelter, Icons.Outlined.NightShelter, ShelterListActivity::class.java)
    object FirstAid : HomeScreen("first_aid", "First Aid", Icons.Filled.MedicalServices, Icons.Outlined.MedicalServices, FirstAidActivity::class.java)
    object Announcements : HomeScreen("announcements", "Announcements", Icons.Filled.Campaign, Icons.Outlined.Campaign, AnnouncementsActivity::class.java)
    object Profile : HomeScreen("profile", "Profile", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle, ProfileActivity::class.java)
}

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AIDMTheme {
                MainAppScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen() {
    val context = LocalContext.current
    // Default to Dashboard screen
    var currentScreen by remember { mutableStateOf<HomeScreen>(HomeScreen.Dashboard) }

    // Define which items appear in the bottom navigation bar
    // Consider screen real estate; too many items can clutter the bottom bar.
    // Others might go into a navigation drawer or a "More" menu.
    val bottomNavigationItems = listOf(
        HomeScreen.Dashboard,
        HomeScreen.MapView,
        HomeScreen.Report,
        HomeScreen.Announcements, // Example: Moved Announcements to bottom bar
        HomeScreen.Profile
    )

    // All possible destinations (could be more than what's on the bottom bar)
    // This isn't strictly necessary if all nav is activity-based or simple content swap
    val allDestinations = listOf(
        HomeScreen.Dashboard,
        HomeScreen.MapView,
        HomeScreen.Report,
        HomeScreen.Shelters,
        HomeScreen.FirstAid,
        HomeScreen.Announcements,
        HomeScreen.Profile
    )


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentScreen.title) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
                // You could add a navigationIcon for a NavDrawer here if needed
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface, // Or surfaceVariant, primaryContainer etc.
                contentColor = MaterialTheme.colorScheme.onSurface // Or onPrimaryContainer
            ) {
                bottomNavigationItems.forEach { screen ->
                    val isSelected = currentScreen.route == screen.route
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.title
                            )
                        },
                        label = { Text(screen.title) },
                        selected = isSelected,
                        onClick = {
                            if (!isSelected) { // Prevent re-selecting the same screen
                                currentScreen = screen // Update the current screen state
                                // Actual navigation to other activities will be handled by HomeContent's LaunchedEffect
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer // Ripple/indicator color
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

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    currentScreen: HomeScreen,
    onNavigateToActivity: (Class<out ComponentActivity>) -> Unit
) {
    // This LaunchedEffect handles navigation to separate Activities
    // when currentScreen changes to one that has a targetActivity.
    LaunchedEffect(currentScreen) {
        currentScreen.targetActivity?.let { activityClass ->
            onNavigateToActivity(activityClass)
            // Note: If you navigate to another activity, the HomeActivity might go to the background.
            // The content below (Column) might briefly show or not at all if the new activity starts quickly.
            // If you want HomeActivity to stay and swap content *within* itself using Jetpack Navigation Compose,
            // the structure would be different (using NavHost).
        }
    }

    // This Column displays content for screens that are handled *within* HomeActivity
    // (i.e., those without a targetActivity, like the Dashboard in this example).
    // Or it can serve as a placeholder while an activity is being launched.
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (currentScreen) {
            HomeScreen.Dashboard -> {
                // Content for the Dashboard screen
                Text("Welcome to the Dashboard!", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                // Example: Add buttons to navigate to other less-frequent screens
                // that are not on the bottom bar but are separate activities.
                Button(onClick = { onNavigateToActivity(HomeScreen.FirstAid.targetActivity!!) }) {
                    Text("Go to First Aid Guide")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { onNavigateToActivity(HomeScreen.Shelters.targetActivity!!) }) {
                    Text("Find Shelters")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { onNavigateToActivity(RouteActivity::class.java) }) { // Assuming RouteActivity exists
                    Text("Show Example Route")
                }
                // Add more Dashboard specific UI elements here
            }
            // If any other screens were to be displayed directly within HomeActivity
            // without launching a new Activity, their content would go here.
            // For example, if Profile was a simple Composable:
            // HomeScreen.Profile -> ProfileScreenComposable()
            else -> {
                // This content is shown if the currentScreen is one that launches a new Activity.
                // It can act as a temporary placeholder.
                Text(
                    "Loading ${currentScreen.title}...",
                    style = MaterialTheme.typography.bodyLarge
                )
                if (currentScreen.targetActivity != null) {
                    Text(
                        "(Navigating to separate screen)",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainAppScreenPreview() {
    AIDMTheme {
        MainAppScreen()
    }
}

// Dummy Activity classes for preview and structure - replace with your actual activities
class MapActivity : ComponentActivity() { /* ... */ }
class ReportIncidentActivity : ComponentActivity() { /* ... */ }
class ProfileActivity : ComponentActivity() { /* ... */ }
class RouteActivity : ComponentActivity() { /* ... */ }
