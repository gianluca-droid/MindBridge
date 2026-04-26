package com.mindbridge.app.data.model

import java.time.LocalDate
import java.time.LocalDateTime

enum class UserRole {
    PAZIENTE,
    TERAPEUTA,
    GENITORE,
    TUTORE,
    FAMILIARE
}

enum class PersonRole {
    PAZIENTE,
    GENITORE,
    TUTORE,
    PARTNER,
    FAMILIARE,
    CAREGIVER
}

enum class CaseType {
    INDIVIDUALE,
    MINORE,
    COPPIA,
    FAMIGLIA
}

enum class ConversationType {
    ONE_TO_ONE,
    CASE_GROUP,
    FAMILY,
    COUPLE
}

enum class AppointmentStatus {
    CONFERMATO,
    RICHIESTA_MODIFICA,
    ANNULLATO,
    COMPLETATO
}

enum class ExerciseStatus {
    DA_FARE,
    IN_CORSO,
    COMPLETATO
}

data class User(
    val id: String = "",
    val nome: String = "",
    val cognome: String = "",
    val email: String = "",
    val ruolo: UserRole = UserRole.PAZIENTE,
    val terapeutaId: String? = null,
    val personId: String? = null
)

data class Person(
    val id: String = "",
    val nome: String = "",
    val cognome: String = "",
    val email: String = "",
    val telefono: String = "",
    val role: PersonRole = PersonRole.PAZIENTE
)

data class CareCase(
    val id: String = "",
    val terapeutaId: String = "",
    val titolo: String = "",
    val tipo: CaseType = CaseType.INDIVIDUALE,
    val mainPatientId: String? = null,
    val participantIds: List<String> = emptyList(),
    val note: String = ""
)

data class Conversation(
    val id: String = "",
    val caseId: String? = null,
    val participantIds: List<String> = emptyList(),
    val type: ConversationType = ConversationType.ONE_TO_ONE,
    val title: String = ""
)

data class ChatMessage(
    val id: String = "",
    val conversationId: String = "",
    val senderId: String = "",
    val contenuto: String = "",
    val timestamp: LocalDateTime = LocalDateTime.now()
)

data class Appointment(
    val id: String = "",
    val caseId: String = "",
    val terapeutaId: String = "",
    val titolo: String = "",
    val participantIds: List<String> = emptyList(),
    val dataOra: LocalDateTime = LocalDateTime.now(),
    val durataMinuti: Int = 50,
    val note: String = "",
    val status: AppointmentStatus = AppointmentStatus.CONFERMATO
)

// Vecchi modelli mantenuti per compatibilità temporanea se necessario
data class MoodEntry(
    val id: String = "",
    val pazienteId: String = "",
    val data: LocalDate = LocalDate.now(),
    val livello: Int = 3,
    val emozioni: List<String> = emptyList(),
    val note: String = "",
    val timestamp: LocalDateTime = LocalDateTime.now()
)

data class Exercise(
    val id: String = "",
    val terapeutaId: String = "",
    val pazienteId: String = "",
    val titolo: String = "",
    val descrizione: String = "",
    val istruzioni: String = "",
    val stato: ExerciseStatus = ExerciseStatus.DA_FARE,
    val dataAssegnazione: LocalDate = LocalDate.now(),
    val dataScadenza: LocalDate? = null,
    val feedbackPaziente: String? = null
)
