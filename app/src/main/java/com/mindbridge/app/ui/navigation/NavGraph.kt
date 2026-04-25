package com.mindbridge.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mindbridge.app.data.model.User
import com.mindbridge.app.ui.auth.AuthViewModel
import com.mindbridge.app.ui.auth.LoginScreen
import com.mindbridge.app.ui.auth.RegisterScreen
import com.mindbridge.app.ui.chat.ChatScreen
import com.mindbridge.app.ui.patient.*
import com.mindbridge.app.ui.therapist.*

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    startDestination: String = Routes.Login.route
) {
    val currentUser: User? = authViewModel.state.currentUser

    NavHost(navController = navController, startDestination = startDestination) {

        // ── Auth ──
        composable(Routes.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Routes.Register.route)
                },
                onLoginSuccess = {
                    val route = if (authViewModel.state.currentUser?.ruolo ==
                        com.mindbridge.app.data.model.UserRole.TERAPEUTA)
                        Routes.TherapistDashboard.route
                    else Routes.PatientDashboard.route
                    navController.navigate(route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Register.route) {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    val route = if (authViewModel.state.currentUser?.ruolo ==
                        com.mindbridge.app.data.model.UserRole.TERAPEUTA)
                        Routes.TherapistDashboard.route
                    else Routes.PatientDashboard.route
                    navController.navigate(route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Patient ──
        composable(Routes.PatientDashboard.route) {
            currentUser?.let { user ->
                PatientDashboard(
                    user = user,
                    onNavigateToMood = { navController.navigate(Routes.MoodDiary.route) },
                    onNavigateToExercises = { navController.navigate(Routes.Exercises.route) },
                    onNavigateToAppointments = { navController.navigate(Routes.BookAppointment.route) },
                    onNavigateToChat = { recipientId ->
                        navController.navigate(Routes.Chat.create(recipientId))
                    }
                )
            }
        }

        composable(Routes.MoodDiary.route) {
            currentUser?.let { MoodDiaryScreen(it) }
        }

        composable(Routes.Exercises.route) {
            currentUser?.let { ExercisesScreen(it) }
        }

        composable(Routes.BookAppointment.route) {
            currentUser?.let { BookAppointmentScreen(it) }
        }

        // ── Therapist ──
        composable(Routes.TherapistDashboard.route) {
            currentUser?.let { user ->
                TherapistDashboard(
                    user = user,
                    onNavigateToPatients = { navController.navigate(Routes.PatientList.route) },
                    onNavigateToChat = { patientId ->
                        navController.navigate(Routes.Chat.create(patientId))
                    },
                    onNavigateToNotes = { patientId ->
                        navController.navigate(Routes.SessionNotes.create(patientId))
                    }
                )
            }
        }

        composable(Routes.PatientList.route) {
            currentUser?.let { /* PatientListScreen placeholder */ }
        }

        composable(
            Routes.SessionNotes.route,
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: return@composable
            SessionNotesScreen(patientId)
        }

        composable(
            Routes.AssignExercise.route,
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: return@composable
            AssignExerciseScreen(patientId)
        }

        // ── Chat ──
        composable(
            Routes.Chat.route,
            arguments = listOf(navArgument("recipientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recipientId = backStackEntry.arguments?.getString("recipientId") ?: return@composable
            currentUser?.let { ChatScreen(it.id, recipientId) }
        }
    }
}
