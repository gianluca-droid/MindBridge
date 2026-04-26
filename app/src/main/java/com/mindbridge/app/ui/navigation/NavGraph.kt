package com.mindbridge.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mindbridge.app.data.repository.MockRepository
import com.mindbridge.app.ui.auth.LoginScreen
import com.mindbridge.app.ui.auth.RegisterScreen
import com.mindbridge.app.ui.chat.ChatScreen
import com.mindbridge.app.ui.patient.PatientDashboard
import com.mindbridge.app.ui.therapist.TherapistDashboard
import com.mindbridge.app.ui.cases.CasesScreen
import com.mindbridge.app.ui.cases.CaseDetailScreen
import com.mindbridge.app.ui.appointments.AppointmentsScreen
import com.mindbridge.app.ui.appointments.AppointmentForm

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Routes.Login.route,
    currentUserId: String,
    onLoginSuccess: (String) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.Login.route) {
            LoginScreen(onLoginSuccess = { userId ->
                onLoginSuccess(userId)
                val user = MockRepository.utenti.find { it.id == userId }
                if (user?.ruolo == com.mindbridge.app.data.model.UserRole.TERAPEUTA) {
                    navController.navigate(Routes.TherapistDashboard.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                } else {
                    navController.navigate(Routes.PatientDashboard.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                }
            }, onNavigateToRegister = {
                navController.navigate(Routes.Register.route)
            })
        }

        composable(Routes.Register.route) {
            RegisterScreen(onRegisterSuccess = {
                navController.navigate(Routes.Login.route)
            }, onNavigateToLogin = {
                navController.navigate(Routes.Login.route)
            })
        }

        // --- SEZIONI TERAPEUTA ---
        composable(Routes.TherapistDashboard.route) {
            TherapistDashboard(currentUserId)
        }
        
        composable(Routes.Cases.route) {
            CasesScreen(currentUserId) { caseId ->
                navController.navigate(Routes.CaseDetail.createRoute(caseId))
            }
        }
        
        composable(
            route = Routes.CaseDetail.route,
            arguments = listOf(navArgument("caseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val caseId = backStackEntry.arguments?.getString("caseId") ?: ""
            CaseDetailScreen(
                caseId = caseId,
                onBackClick = { navController.popBackStack() },
                onChatClick = { convId -> navController.navigate(Routes.Chat.createRoute(convId)) },
                onNewAppointmentClick = { id -> navController.navigate(Routes.AppointmentNew.createRoute(id)) }
            )
        }
        
        composable(Routes.Appointments.route) {
            AppointmentsScreen(currentUserId) {
                navController.navigate(Routes.AppointmentNew.createRoute())
            }
        }
        
        composable(
            route = Routes.AppointmentNew.route,
            arguments = listOf(navArgument("caseId") { 
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val caseId = backStackEntry.arguments?.getString("caseId")
            AppointmentForm(
                therapistId = currentUserId,
                initialCaseId = caseId,
                onDismiss = { navController.popBackStack() },
                onSave = { appt ->
                    MockRepository.addAppointment(appt)
                    navController.popBackStack()
                }
            )
        }

        // --- CHAT ---
        composable(
            route = Routes.Chat.route,
            arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val convId = backStackEntry.arguments?.getString("conversationId") ?: ""
            ChatScreen(currentUserId = currentUserId, conversationId = convId)
        }

        // --- PAZIENTE ---
        composable(Routes.PatientDashboard.route) {
            PatientDashboard(currentUserId)
        }
    }
}
