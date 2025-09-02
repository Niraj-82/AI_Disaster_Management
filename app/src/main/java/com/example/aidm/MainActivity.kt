
package com.example.aidm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.NightShelter
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.NightShelter
import androidx.compose.material.icons.outlined.ReportProblem
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay


// --- Routes and Navigation ---

// Routes for the entire app
object AppRoutes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val MAIN = "main" // A nested graph for the main app content
}

// Routes for the screens inside the main app (with bottom navigation)
sealed class MainScreenRoutes(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Dashboard : MainScreenRoutes("dashboard", "Dashboard", Icons.Filled.Home, Icons.Outlined.Home)
    object Map : MainScreenRoutes("map", "Map", Icons.Filled.Map, Icons.Outlined.Map)
    object Report : MainScreenRoutes("report", "Report Incident", Icons.Filled.ReportProblem, Icons.Outlined.ReportProblem)
    object Announcements : MainScreenRoutes("announcements", "Announcements", Icons.Filled.Campaign, Icons.Outlined.Campaign)
    object Profile : MainScreenRoutes("profile", "Profile", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)
    // Non-bottom bar screens
    object Shelters : MainScreenRoutes("shelters", "Shelters", Icons.Filled.NightShelter, Icons.Outlined.NightShelter)
    object ShelterDetail : MainScreenRoutes("shelter_detail/{shelterId}", "Shelter Detail", Icons.Filled.NightShelter, Icons.Outlined.NightShelter) {
        fun createRoute(shelterId: String) = "shelter_detail/$shelterId"
    }
    object FirstAid : MainScreenRoutes("first_aid", "First Aid", Icons.Filled.MedicalServices, Icons.Outlined.MedicalServices)
    object Settings : MainScreenRoutes("settings", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
    object Route : MainScreenRoutes("route", "Route", Icons.Filled.Route, Icons.Outlined.Route)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AIDMTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppRoutes.SPLASH) {
        composable(AppRoutes.SPLASH) {
            SplashScreen(onDone = {
                navController.navigate(AppRoutes.LOGIN) { popUpTo(AppRoutes.SPLASH) { inclusive = true } }
            })
        }
        composable(AppRoutes.LOGIN) {
            LoginScreen(
                viewModel = viewModel(),
                onLoginSuccess = { navController.navigate(AppRoutes.MAIN) { popUpTo(AppRoutes.LOGIN) { inclusive = true } } },
                onSignupClicked = { navController.navigate(AppRoutes.SIGNUP) }
            )
        }
        composable(AppRoutes.SIGNUP) {
            SignupScreen(
                viewModel = viewModel(),
                onSignupSuccess = { navController.navigate(AppRoutes.MAIN) { popUpTo(AppRoutes.LOGIN) { inclusive = true } } },
                onBackToLogin = { navController.popBackStack() }
            )
        }
        composable(AppRoutes.MAIN) {
            MainScreen()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val bottomNavItems = listOf(
        MainScreenRoutes.Dashboard,
        MainScreenRoutes.Map,
        MainScreenRoutes.Report,
        MainScreenRoutes.Announcements,
        MainScreenRoutes.Profile
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(if (isSelected) screen.selectedIcon else screen.unselectedIcon, contentDescription = screen.title) },
                        label = { Text(screen.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        MainAppNavHost(navController = navController, innerPadding = innerPadding)
    }
}

@Composable
fun MainAppNavHost(navController: NavHostController, innerPadding: PaddingValues) {
    NavHost(navController, startDestination = MainScreenRoutes.Dashboard.route, Modifier.padding(innerPadding)) {
        composable(MainScreenRoutes.Dashboard.route) {
            DashboardScreen(
                onNavigateToShelters = { navController.navigate(MainScreenRoutes.Shelters.route) },
                onNavigateToFirstAid = { navController.navigate(MainScreenRoutes.FirstAid.route) },
                onNavigateToRoute = { navController.navigate(MainScreenRoutes.Route.route) }
            )
        }
        composable(MainScreenRoutes.Map.route) {
             MapScreen(
                initialUserLocation = LatLng(40.7128, -74.0060), // Example location
                onMapReady = {}
            )
        }
        composable(MainScreenRoutes.Report.route) {
             val sharedViewModel: SharedIncidentViewModel = viewModel()
            ReportIncidentScreen(sharedViewModel = sharedViewModel)
        }
        composable(MainScreenRoutes.Announcements.route) { AnnouncementsScreen() }
        composable(MainScreenRoutes.Profile.route) { ProfileScreen(onNavigateToSettings = { navController.navigate(MainScreenRoutes.Settings.route) } ) }
        composable(MainScreenRoutes.Settings.route) { SettingsScreen() }
        composable(MainScreenRoutes.Shelters.route) {
            ShelterListScreen(onOpenShelter = { shelterId ->
                    navController.navigate(MainScreenRoutes.ShelterDetail.createRoute(shelterId))
                }
            )
        }
        composable(MainScreenRoutes.ShelterDetail.route) { backStackEntry ->
            val shelterId = backStackEntry.arguments?.getString("shelterId")
            if (shelterId != null) {
                ShelterDetailScreen(id = shelterId, viewModel = viewModel())
            } else {
                navController.popBackStack()
            }
        }
        composable(MainScreenRoutes.FirstAid.route) { FirstAidScreen(viewModel = viewModel()) }
        composable(MainScreenRoutes.Route.route) { RouteScreen(originName = "Your Location", destinationName = "Example Destination") }
    }
}


// --- Placeholder Screens & Re-usable Components ---

@Composable
fun SplashScreen(onDone: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1000)
        onDone()
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("AIDM • Smart Disaster Response")
    }
}

@Composable
fun DashboardScreen(
    onNavigateToShelters: () -> Unit,
    onNavigateToFirstAid: () -> Unit,
    onNavigateToRoute: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to the Dashboard!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateToFirstAid) { Text("Go to First Aid Guide") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onNavigateToShelters) { Text("Find Shelters") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onNavigateToRoute) { Text("Show Example Route") }
    }
}

@Composable
fun ProfileScreen(onNavigateToSettings: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Profile Screen")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateToSettings) {
            Text("Go to Settings")
        }
    }
}

@Composable
fun AnnouncementsScreen() { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Announcements Screen") } }

@Composable
fun SettingsScreen() { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Settings Screen") } }

