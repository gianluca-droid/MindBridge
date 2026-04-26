package com.mindbridge.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mindbridge.app.data.repository.MockRepository
import com.mindbridge.app.ui.auth.LoginScreen
import com.mindbridge.app.ui.auth.RegisterScreen
import com.mindbridge.app.ui.auth.AuthViewModel
import com.mindbridge.app.ui.chat.ChatScreen
import com.mindbridge.app.ui.patient.PatientDashboard
import com.mindbridge.app.ui.patient.MoodDiaryScreen
import com.mindbridge.app.ui.patient.ExercisesScreen
import com.mindbridge.app.ui.therapist.TherapistDashboard
import com.mindbridge.app.ui.cases.CasesScreen
import com.mindbridge.app.ui.cases.CaseDetailScreen
import com.mindbridge.app.ui.cases.NewCaseScreen
import com.mindbridge.app.ui.appointments.AppointmentsScreen
import com.mindbridge.app.ui.appointments.AppointmentForm

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Routes.Login.route,
    currentUserId: String,
    onLoginSuccess: (String) -> Unit
) {
    val currentUser = MockRepository.utenti.find { it.id == currentUserId }
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    val user = authViewModel.state.currentUser
                    if (user != null) {
                        onLoginSuccess(user.id)
                        if (user.ruolo == com.mindbridge.app.data.model.UserRole.TERAPEUTA) {
                            navController.navigate(Routes.TherapistDashboard.route) {
                                popUpTo(Routes.Login.route) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Routes.PatientDashboard.route) {
                                popUpTo(Routes.Login.route) { inclusive = true }
                            }
                        }
                    }
                }, onNavigateToRegister = {
                navController.navigate(Routes.Register.route)
            })
        }

        composable(Routes.Register.route) {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Routes.Login.route)
                }, onNavigateToLogin = {
                navController.navigate(Routes.Login.route)
            })
        }

        // --- SEZIONI TERAPEUTA ---
        composable(Routes.TherapistDashboard.route) {
            currentUser?.let { user ->
                TherapistDashboard(
                    user = user,
                    onNavigateToPatients = { navController.navigate(Routes.Cases.route) },
                    onNavigateToChat = { convId -> navController.navigate(Routes.Chat.createRoute(convId)) },
                    onNavigateToNotes = { caseId -> navController.navigate(Routes.CaseDetail.createRoute(caseId)) }
                )
            }
        }
        
        composable(Routes.Cases.route) {
            CasesScreen(
                therapistId = currentUserId,
                onCaseClick = { caseId -> navController.navigate(Routes.CaseDetail.createRoute(caseId)) },
                onNewCaseClick = { navController.navigate(Routes.CaseNew.route) }
            )
        }

        composable(Routes.CaseNew.route) {
            NewCaseScreen(
                therapistId = currentUserId,
                onDismiss = { navController.popBackStack() },
                onSave = { newCase ->
                    MockRepository.casi.add(newCase)
                    navController.popBackStack()
                }
            )
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
            currentUser?.let { user ->
                PatientDashboard(
                    user = user,
                    onNavigateToMood = { navController.navigate(Routes.MoodDiary.route) },
                    onNavigateToExercises = { navController.navigate(Routes.Exercises.route) },
                    onNavigateToAppointments = { navController.navigate(Routes.Appointments.route) },
                    onNavigateToChat = { convId -> navController.navigate(Routes.Chat.createRoute(convId)) }
                )
            }
        }

        composable(Routes.MoodDiary.route) {
            currentUser?.let { user ->
                MoodDiaryScreen(user = user)
            }
        }

        composable(Routes.Exercises.route) {
            currentUser?.let { user ->
                ExercisesScreen(user = user)
            }
        }
    }
}
