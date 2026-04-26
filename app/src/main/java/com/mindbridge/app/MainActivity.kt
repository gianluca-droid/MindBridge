package com.mindbridge.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mindbridge.app.data.model.UserRole
import com.mindbridge.app.data.repository.MockRepository
import com.mindbridge.app.ui.navigation.NavGraph
import com.mindbridge.app.ui.navigation.Routes
import com.mindbridge.app.ui.theme.MindBridgeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MindBridgeTheme {
                MindBridgeContent()
            }
        }
    }
}

@Composable
fun MindBridgeContent() {
    val navController = rememberNavController()
    var currentUserId by remember { mutableStateOf("") }
    val currentUser = remember(currentUserId) { MockRepository.utenti.find { it.id == currentUserId } }
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Schermate dove NON mostrare la bottom bar
    val hideBottomBar = currentRoute == Routes.Login.route || 
                        currentRoute == Routes.Register.route || 
                        currentRoute?.startsWith("chat/") == true ||
                        currentRoute?.startsWith("appointment_new") == true

    Scaffold(
        bottomBar = {
            if (!hideBottomBar && currentUser != null) {
                NavigationBar {
                    if (currentUser.ruolo == UserRole.TERAPEUTA) {
                        // Navigazione Terapeuta
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Home, "Home") },
                            label = { Text("Home") },
                            selected = currentRoute == Routes.TherapistDashboard.route,
                            onClick = { navController.navigate(Routes.TherapistDashboard.route) }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Groups, "Assistiti") },
                            label = { Text("Assistiti") },
                            selected = currentRoute == Routes.Cases.route || currentRoute?.startsWith("case_detail") == true,
                            onClick = { navController.navigate(Routes.Cases.route) }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.CalendarMonth, "Agenda") },
                            label = { Text("Agenda") },
                            selected = currentRoute == Routes.Appointments.route,
                            onClick = { navController.navigate(Routes.Appointments.route) }
                        )
                    } else {
                        // Navigazione Paziente
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Home, "Home") },
                            label = { Text("Home") },
                            selected = currentRoute == Routes.PatientDashboard.route,
                            onClick = { navController.navigate(Routes.PatientDashboard.route) }
                        )
                        // Altri item per il paziente...
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavGraph(
                navController = navController,
                currentUserId = currentUserId,
                onLoginSuccess = { id -> currentUserId = id }
            )
        }
    }
}
