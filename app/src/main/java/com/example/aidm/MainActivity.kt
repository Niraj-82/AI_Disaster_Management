package com.example.aidm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// --- Theme ---
// FIX 2: Corrected the import path for the theme to the standard location.
import com.example.aidm.AIDMTheme

// --- Screen Imports ---
import com.example.aidm.SplashScreen
import com.example.aidm.LoginScreen
import com.example.aidm.HomeScreen
import com.example.aidm.MapScreen
import com.example.aidm.RouteScreen
import com.example.aidm.ReportIncidentScreen
import com.example.aidm.ShelterListScreen
import com.example.aidm.ShelterDetailScreen
import com.example.aidm.FirstAidScreen
import com.example.aidm.AnnouncementsScreen
import com.example.aidm.ProfileScreen
import com.example.aidm.SettingsScreen

// Import LatLng to use for the map's initial location
import com.google.android.gms.maps.model.LatLng

// FIX 1: This Routes object should be the ONLY one in your entire project.
// Delete any other declarations of "object Routes".
object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val HOME = "home"
    const val MAP = "map"
    const val ROUTE = "route"
    const val REPORT_INCIDENT = "report_incident"
    const val SHELTERS = "shelters"
    const val SHELTER_DETAIL = "shelter/{shelterId}" // Argument name is 'shelterId'
    fun shelterDetail(shelterId: String) = "shelter/$shelterId" // Helper for cleaner navigation calls
    const val FIRST_AID = "first_aid"
    const val ANNOUNCEMENTS = "announcements"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
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

    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onDone = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.HOME) {
            HomeScreen(
                onOpenMap = { navController.navigate(Routes.MAP) },
                onOpenRoute = { navController.navigate(Routes.ROUTE) },
                onReport = { navController.navigate(Routes.REPORT_INCIDENT) },
                onShelters = { navController.navigate(Routes.SHELTERS) },
                onFirstAid = { navController.navigate(Routes.FIRST_AID) },
                onAnnouncements = { navController.navigate(Routes.ANNOUNCEMENTS) },
                onProfile = { navController.navigate(Routes.PROFILE) },
                onSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }
        composable(Routes.MAP) {
            // FIX 3: Add the required onMapReady parameter
            MapScreen(
                initialUserLocation = LatLng(40.7128, -74.0060), // Example: New York City
                onMapReady = {}
            )
        }
        composable(Routes.ROUTE) {
            // Provide example parameters for origin and destination.
            RouteScreen(originName = "Your Location", destinationName = "Example Destination")
        }
        composable(Routes.REPORT_INCIDENT) { ReportIncidentScreen() }
        composable(Routes.SHELTERS) {
            ShelterListScreen(
                onOpenShelter = { shelterId ->
                    navController.navigate(Routes.shelterDetail(shelterId))
                }
            )
        }
        composable(Routes.SHELTER_DETAIL) { backStackEntry ->
            val shelterId = backStackEntry.arguments?.getString("shelterId")
            if (shelterId != null) {
                // Use the correct parameter name 'id' for ShelterDetailScreen.
                ShelterDetailScreen(id = shelterId)
            } else {
                navController.popBackStack()
            }
        }
        composable(Routes.FIRST_AID) { FirstAidScreen() }
        composable(Routes.ANNOUNCEMENTS) { AnnouncementsScreen() }
        composable(Routes.PROFILE) { ProfileScreen() }
        composable(Routes.SETTINGS) { SettingsScreen() }
    }
}
