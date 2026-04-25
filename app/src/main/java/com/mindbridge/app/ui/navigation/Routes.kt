package com.mindbridge.app.ui.navigation

/** Definizione delle rotte di navigazione */
sealed class Routes(val route: String) {
    object Login : Routes("login")
    object Register : Routes("register")
    object PatientDashboard : Routes("patient/dashboard")
    object MoodDiary : Routes("patient/mood")
    object Exercises : Routes("patient/exercises")
    object BookAppointment : Routes("patient/appointments")
    object TherapistDashboard : Routes("therapist/dashboard")
    object PatientList : Routes("therapist/patients")
    object SessionNotes : Routes("therapist/notes/{patientId}") {
        fun create(patientId: String) = "therapist/notes/$patientId"
    }
    object AssignExercise : Routes("therapist/assign/{patientId}") {
        fun create(patientId: String) = "therapist/assign/$patientId"
    }
    object Chat : Routes("chat/{recipientId}") {
        fun create(recipientId: String) = "chat/$recipientId"
    }
}
