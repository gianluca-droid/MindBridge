package com.mindbridge.app.ui.navigation

/** Definizione delle rotte di navigazione */
sealed class Routes(val route: String) {
    object Login : Routes("login")
    object Register : Routes("register")
    
    // Dashboard principale per ruolo
    object PatientDashboard : Routes("patient_dashboard")
    object TherapistDashboard : Routes("therapist_dashboard")
    
    // Nuove sezioni verticali
    object Cases : Routes("cases")
    object CaseNew : Routes("case_new")
    object CaseDetail : Routes("case_detail/{caseId}") {
        fun createRoute(caseId: String) = "case_detail/$caseId"
    }
    
    object Appointments : Routes("appointments")
    object AppointmentNew : Routes("appointment_new?caseId={caseId}") {
        fun createRoute(caseId: String? = null) = if (caseId != null) "appointment_new?caseId=$caseId" else "appointment_new"
    }
    
    // Chat aggiornata
    object Chat : Routes("chat/{conversationId}") {
        fun createRoute(convId: String) = "chat/$convId"
    }

    object MoodDiary : Routes("mood_diary")
    object Exercises : Routes("exercises")
}
