package com.mindbridge.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mindbridge.app.data.model.UserRole
import com.mindbridge.app.ui.auth.AuthViewModel
import com.mindbridge.app.ui.navigation.NavGraph
import com.mindbridge.app.ui.navigation.Routes
import com.mindbridge.app.ui.theme.MindBridgeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MindBridgeTheme {
                MindBridgeApp()
            }
        }
    }
}

data class BottomNavItem(
    val label: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun MindBridgeApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val currentUser = authViewModel.state.currentUser
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Bottom nav items in base al ruolo
    val patientItems = listOf(
        BottomNavItem("Home", Routes.PatientDashboard.route, Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem("Umore", Routes.MoodDiary.route, Icons.Filled.Mood, Icons.Outlined.Mood),
        BottomNavItem("Esercizi", Routes.Exercises.route, Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter),
        BottomNavItem("Prenota", Routes.BookAppointment.route, Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth)
    )

    val therapistItems = listOf(
        BottomNavItem("Home", Routes.TherapistDashboard.route, Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem("Pazienti", Routes.PatientList.route, Icons.Filled.People, Icons.Outlined.People)
    )

    val isLoggedIn = authViewModel.state.isLoggedIn
    val isAuthRoute = currentRoute in listOf(Routes.Login.route, Routes.Register.route)
    val showBottomBar = isLoggedIn && !isAuthRoute &&
        currentRoute?.startsWith("chat/") != true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                val items = if (currentUser?.ruolo == UserRole.TERAPEUTA) therapistItems else patientItems
                NavigationBar {
                    items.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(items.first().route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavGraph(
                navController = navController,
                authViewModel = authViewModel
            )
        }
    }
}
