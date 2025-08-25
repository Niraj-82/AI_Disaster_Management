package com.example.aidm

import androidx.activity.ComponentActivity // Or import android.app.Activity, etc.
import android.os.Bundle
import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.navigation.compose.rememberNavController
import com.aidm.app.ui.theme.AIDMTheme
import com.aidm.app.nav.Routes
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aidm.app.ui.screens.*
import com.aidm.app.ui.screens.shelter.ShelterDetailScreen
import com.aidm.app.ui.screens.shelter.ShelterListScreen
import com.google.accompanist.permissions.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AIDMTheme {
                val nav = rememberNavController()

                NavHost(navController = nav, startDestination = Routes.Splash) {
                    composable(Routes.Splash) { SplashScreen(onDone = { nav.navigate(Routes.Login) { popUpTo(Routes.Splash) { inclusive = true } } }) }
                    composable(Routes.Login)  { LoginScreen(onLogin = { nav.navigate(Routes.Home) { popUpTo(Routes.Login) { inclusive = true } } }) }
                    composable(Routes.Home)   { HomeScreen(
                        onOpenMap = { nav.navigate(Routes.Map) },
                        onOpenRoute = { nav.navigate(Routes.Route) },
                        onReport = { nav.navigate(Routes.Report) },
                        onShelters = { nav.navigate(Routes.Shelters) },
                        onFirstAid = { nav.navigate(Routes.FirstAid) },
                        onAnnouncements = { nav.navigate(Routes.Announcements) },
                        onProfile = { nav.navigate(Routes.Profile) },
                        onSettings = { nav.navigate(Routes.Settings) }
                    ) }
                    composable(Routes.Map) { MapScreen() }
                    composable(Routes.Route) { RouteScreen() }
                    composable(Routes.Report) { ReportIncidentScreen() }
                    composable(Routes.Shelters) { ShelterListScreen(onOpen = { id -> nav.navigate("shelter/$id") }) }
                    composable(Routes.ShelterDetail) { backStack ->
                        val id = backStack.arguments?.getString("id") ?: ""
                        ShelterDetailScreen(id)
                    }
                    composable(Routes.FirstAid) { FirstAidScreen() }
                    composable(Routes.Announcements) { AnnouncementsScreen() }
                    composable(Routes.Profile) { ProfileScreen() }
                    composable(Routes.Settings) { SettingsScreen() }
                }
            }
        }
    }
}
