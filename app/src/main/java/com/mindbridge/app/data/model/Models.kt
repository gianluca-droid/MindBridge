package com.mindbridge.app.data.model

import java.time.LocalDate
import java.time.LocalDateTime

/** Ruolo dell'utente nell'app */
enum class UserRole { PAZIENTE, TERAPEUTA }

/** Stato di un appuntamento */
enum class AppointmentStatus { CONFERMATO, IN_ATTESA, ANNULLATO, COMPLETATO }

/** Stato di un esercizio terapeutico */
enum class ExerciseStatus { DA_FARE, IN_CORSO, COMPLETATO }

/** Profilo utente */
data class User(
    val id: String = "",
    val nome: String = "",
    val cognome: String = "",
    val email: String = "",
    val ruolo: UserRole = UserRole.PAZIENTE,
    val fotoUrl: String? = null,
    val terapeutaId: String? = null,   // solo per pazienti
    val numeroAlbo: String? = null,     // solo per terapeuti
    val bio: String? = null
)

/** Voce del diario dell'umore (1-5) */
data class MoodEntry(
    val id: String = "",
    val pazienteId: String = "",
    val data: LocalDate = LocalDate.now(),
    val livello: Int = 3,               // 1=pessimo, 5=ottimo
    val emozioni: List<String> = emptyList(),
    val note: String = "",
    val timestamp: LocalDateTime = LocalDateTime.now()
)

/** Appuntamento tra paziente e terapeuta */
data class Appointment(
    val id: String = "",
    val pazienteId: String = "",
    val terapeutaId: String = "",
    val dataOra: LocalDateTime = LocalDateTime.now(),
    val durata: Int = 50,               // minuti
    val stato: AppointmentStatus = AppointmentStatus.IN_ATTESA,
    val note: String = "",
    val nomePaziente: String = "",
    val nomeTerapeuta: String = ""
)

/** Esercizio terapeutico assegnato dal terapeuta */
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

/** Nota di sessione (visibile solo al terapeuta) */
data class SessionNote(
    val id: String = "",
    val terapeutaId: String = "",
    val pazienteId: String = "",
    val dataSessione: LocalDate = LocalDate.now(),
    val contenuto: String = "",
    val obiettivi: String = "",
    val progressi: String = "",
    val prossimiPassi: String = ""
)

/** Messaggio della chat */
data class ChatMessage(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val contenuto: String = "",
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val letto: Boolean = false
)
