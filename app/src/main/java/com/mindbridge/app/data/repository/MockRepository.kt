package com.mindbridge.app.data.repository

import com.mindbridge.app.data.model.*
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Repository mock con dati di esempio in italiano.
 * Sarà sostituito da Firebase in futuro.
 */
object MockRepository {

    // ── Utenti ──
    private val terapeuta = User(
        id = "t1", nome = "Elena", cognome = "Rossi",
        email = "elena.rossi@mindbridge.it",
        ruolo = UserRole.TERAPEUTA,
        numeroAlbo = "PSI-RM-12345",
        bio = "Psicoterapeuta cognitivo-comportamentale con 15 anni di esperienza."
    )

    private val paziente = User(
        id = "p1", nome = "Sofia", cognome = "Conti",
        email = "sofia.conti@email.it",
        ruolo = UserRole.PAZIENTE,
        terapeutaId = "t1"
    )

    val utenti = listOf(terapeuta, paziente,
        User(id = "p2", nome = "Luca", cognome = "Moretti", email = "luca.m@email.it",
            ruolo = UserRole.PAZIENTE, terapeutaId = "t1"),
        User(id = "p3", nome = "Giulia", cognome = "Ferrara", email = "giulia.f@email.it",
            ruolo = UserRole.PAZIENTE, terapeutaId = "t1")
    )

    // ── Umore ──
    val moodEntries = mutableListOf(
        MoodEntry("m1", "p1", LocalDate.now().minusDays(6), 2, listOf("😔","😰"), "Giornata difficile"),
        MoodEntry("m2", "p1", LocalDate.now().minusDays(5), 3, listOf("😐"), "Nella norma"),
        MoodEntry("m3", "p1", LocalDate.now().minusDays(4), 3, listOf("😐","🤔"), "Un po' pensierosa"),
        MoodEntry("m4", "p1", LocalDate.now().minusDays(3), 4, listOf("😊"), "Buona passeggiata"),
        MoodEntry("m5", "p1", LocalDate.now().minusDays(2), 4, listOf("😊","💪"), "Mi sento meglio"),
        MoodEntry("m6", "p1", LocalDate.now().minusDays(1), 5, listOf("😄","🌟"), "Ottima giornata!"),
        MoodEntry("m7", "p1", LocalDate.now(), 4, listOf("😊"), "Serena")
    )

    // ── Appuntamenti ──
    val appuntamenti = mutableListOf(
        Appointment("a1", "p1", "t1",
            LocalDateTime.now().plusDays(2).withHour(10).withMinute(0),
            50, AppointmentStatus.CONFERMATO, nomePaziente = "Sofia Conti", nomeTerapeuta = "Dr.ssa Elena Rossi"),
        Appointment("a2", "p2", "t1",
            LocalDateTime.now().plusDays(1).withHour(14).withMinute(0),
            50, AppointmentStatus.CONFERMATO, nomePaziente = "Luca Moretti", nomeTerapeuta = "Dr.ssa Elena Rossi"),
        Appointment("a3", "p3", "t1",
            LocalDateTime.now().withHour(16).withMinute(0),
            50, AppointmentStatus.CONFERMATO, nomePaziente = "Giulia Ferrara", nomeTerapeuta = "Dr.ssa Elena Rossi"),
        Appointment("a4", "p1", "t1",
            LocalDateTime.now().minusDays(7).withHour(10).withMinute(0),
            50, AppointmentStatus.COMPLETATO, nomePaziente = "Sofia Conti", nomeTerapeuta = "Dr.ssa Elena Rossi")
    )

    // ── Esercizi ──
    val esercizi = mutableListOf(
        Exercise("e1", "t1", "p1", "Respirazione Diaframmatica",
            "Pratica la respirazione profonda per 10 minuti al giorno.",
            "1. Siediti comodamente\n2. Inspira dal naso per 4 secondi\n3. Trattieni per 4 secondi\n4. Espira dalla bocca per 6 secondi\n5. Ripeti 10 volte",
            ExerciseStatus.IN_CORSO, LocalDate.now().minusDays(3), LocalDate.now().plusDays(4)),
        Exercise("e2", "t1", "p1", "Diario della Gratitudine",
            "Scrivi 3 cose per cui sei grata ogni sera prima di dormire.",
            "Ogni sera, prenditi 5 minuti per riflettere sulla giornata e annotare almeno 3 momenti positivi.",
            ExerciseStatus.DA_FARE, LocalDate.now().minusDays(1), LocalDate.now().plusDays(6)),
        Exercise("e3", "t1", "p1", "Camminata Consapevole",
            "Fai una passeggiata di 20 minuti concentrandoti sulle sensazioni.",
            "Cammina lentamente, nota i suoni, gli odori, le sensazioni sul corpo. Senza telefono.",
            ExerciseStatus.COMPLETATO, LocalDate.now().minusDays(7), LocalDate.now().minusDays(1),
            feedbackPaziente = "Mi ha fatto sentire molto più rilassata!")
    )

    // ── Note di sessione ──
    val noteSessione = mutableListOf(
        SessionNote("n1", "t1", "p1", LocalDate.now().minusDays(7),
            "Sofia mostra progressi significativi nella gestione dell'ansia.",
            "Ridurre episodi di ansia sociale",
            "Buona capacità di applicare le tecniche di respirazione",
            "Introdurre esposizione graduale a situazioni sociali")
    )

    // ── Chat ──
    val messaggi = mutableListOf(
        ChatMessage("c1", "p1", "t1", "Buongiorno dottoressa, volevo dirle che l'esercizio di respirazione sta funzionando!",
            LocalDateTime.now().minusHours(5), true),
        ChatMessage("c2", "t1", "p1", "Ottimo Sofia! Sono contenta dei tuoi progressi. Continua così 😊",
            LocalDateTime.now().minusHours(4), true),
        ChatMessage("c3", "p1", "t1", "Grazie! Ho anche iniziato il diario della gratitudine.",
            LocalDateTime.now().minusHours(3), true),
        ChatMessage("c4", "t1", "p1", "Perfetto. Ne parliamo alla prossima seduta. A presto!",
            LocalDateTime.now().minusHours(2), false)
    )

    // ── Helper functions ──
    fun getUserByEmail(email: String) = utenti.find { it.email == email }
    fun getPazientiByTerapeuta(terapeutaId: String) = utenti.filter {
        it.ruolo == UserRole.PAZIENTE && it.terapeutaId == terapeutaId
    }
    fun getMoodByPaziente(pazienteId: String) = moodEntries.filter { it.pazienteId == pazienteId }
    fun getAppuntamentiByUser(userId: String) = appuntamenti.filter {
        it.pazienteId == userId || it.terapeutaId == userId
    }
    fun getEserciziByPaziente(pazienteId: String) = esercizi.filter { it.pazienteId == pazienteId }
    fun getMessaggi(userId1: String, userId2: String) = messaggi.filter {
        (it.senderId == userId1 && it.receiverId == userId2) ||
        (it.senderId == userId2 && it.receiverId == userId1)
    }.sortedBy { it.timestamp }
}
