package com.mindbridge.app.data.repository

import com.mindbridge.app.data.model.*
import androidx.compose.runtime.mutableStateListOf
import java.time.LocalDate
import java.time.LocalDateTime

object MockRepository {

    // ── PERSONE (Tutti i coinvolti) ──
    val persone = mutableStateListOf(
        Person("p1", "Sofia", "Rossi", "sofia.rossi@email.it", "3331234567", PersonRole.PAZIENTE),
        Person("p2", "Matteo", "Rossi", "", "", PersonRole.PAZIENTE),
        Person("p3", "Giulia", "Bianchi", "giulia.b@email.it", "3339876543", PersonRole.GENITORE),
        Person("p4", "Marco", "Rossi", "marco.r@email.it", "3334455667", PersonRole.GENITORE),
        Person("p5", "Marco", "Bianchi", "m.bianchi@email.it", "3331122334", PersonRole.PARTNER),
        Person("p6", "Laura", "Verdi", "l.verdi@email.it", "3335566778", PersonRole.PARTNER),
        Person("p7", "Elena", "Rossi", "elena.rossi@mindbridge.it", "06123456", PersonRole.CAREGIVER)
    )

    // ── UTENTI (Chi accede all'app) ──
    val utenti = mutableStateListOf(
        User("u_terapeuta", "Elena", "Rossi", "elena.rossi@mindbridge.it", UserRole.TERAPEUTA, personId = "p7"),
        User("u_sofia", "Sofia", "Rossi", "sofia.rossi@email.it", UserRole.PAZIENTE, personId = "p1", terapeutaId = "u_terapeuta"),
        User("u_giulia", "Giulia", "Bianchi", "giulia.b@email.it", UserRole.GENITORE, personId = "p3", terapeutaId = "u_terapeuta"),
        User("u_marco", "Marco", "Bianchi", "m.bianchi@email.it", UserRole.PAZIENTE, personId = "p5", terapeutaId = "u_terapeuta")
    )

    // ── CASI TERAPEUTICI (CareCase) ──
    val casi = mutableStateListOf(
        CareCase("c1", "u_terapeuta", "Percorso Individuale - Sofia Rossi", CaseType.INDIVIDUALE, "p1", listOf("p1")),
        CareCase("c2", "u_terapeuta", "Terapia Minore - Matteo Rossi", CaseType.MINORE, "p2", listOf("p2", "p3", "p4")),
        CareCase("c3", "u_terapeuta", "Terapia di Coppia - Bianchi/Verdi", CaseType.COPPIA, "p5", listOf("p5", "p6")),
        CareCase("c4", "u_terapeuta", "Sostegno Familiare - Famiglia Bianchi", CaseType.FAMIGLIA, "p5", listOf("p3", "p5", "p6"))
    )

    // ── CONVERSAZIONI ──
    val conversazioni = mutableStateListOf(
        Conversation("conv1", "c1", listOf("u_terapeuta", "u_sofia"), ConversationType.ONE_TO_ONE, "Chat con Sofia"),
        Conversation("conv2", "c2", listOf("u_terapeuta", "u_giulia"), ConversationType.ONE_TO_ONE, "Mamma di Matteo (Giulia)"),
        Conversation("conv3", "c2", listOf("u_terapeuta", "u_giulia", "u_marco_p"), ConversationType.CASE_GROUP, "Gruppo Genitori Matteo"),
        Conversation("conv4", "c3", listOf("u_terapeuta", "u_marco", "u_laura_p"), ConversationType.COUPLE, "Coppia Bianchi/Verdi")
    )

    // ── MESSAGGI ──
    val messaggi = mutableStateListOf(
        ChatMessage("m1", "conv1", "u_sofia", "Buongiorno, ho completato l'esercizio.", LocalDateTime.now().minusDays(1)),
        ChatMessage("m2", "conv1", "u_terapeuta", "Ottimo Sofia, ne parliamo domani.", LocalDateTime.now().minusHours(5)),
        ChatMessage("m3", "conv2", "u_giulia", "Dottoressa, Matteo oggi è molto agitato.", LocalDateTime.now().minusHours(2)),
        ChatMessage("m4", "conv4", "u_marco", "Abbiamo avuto una discussione intensa oggi.", LocalDateTime.now().minusMinutes(30))
    )

    // ── APPUNTAMENTI ──
    val appuntamenti = mutableStateListOf(
        Appointment("a1", "c1", "u_terapeuta", "Seduta Individuale", listOf("p1"), LocalDateTime.now().plusDays(1).withHour(10).withMinute(0)),
        Appointment("a2", "c2", "u_terapeuta", "Incontro con Genitori", listOf("p3", "p4"), LocalDateTime.now().withHour(15).withMinute(0)),
        Appointment("a3", "c3", "u_terapeuta", "Terapia di Coppia", listOf("p5", "p6"), LocalDateTime.now().plusDays(2).withHour(17).withMinute(30)),
        Appointment("a4", "c4", "u_terapeuta", "Seduta Familiare", listOf("p3", "p5", "p6"), LocalDateTime.now().plusDays(5).withHour(11).withMinute(0))
    )

    val noteSessione = mutableStateListOf(
        SessionNote("sn1", "c1", "u_terapeuta", LocalDateTime.now().minusDays(7), "Prima seduta conoscitiva.", "Approccio", "Buoni", "Approfondire", "Riflettere sulle emozioni"),
        SessionNote("sn2", "c2", "u_terapeuta", LocalDateTime.now().minusDays(3), "Osservazione del gioco del minore.", "", "Stabile", "", "")
    )

    val moodEntries = mutableStateListOf(
        MoodEntry("me1", "u_sofia", LocalDate.now().minusDays(1), 4, listOf("Calma"), "Mi sento bene"),
        MoodEntry("me2", "u_sofia", LocalDate.now(), 5, listOf("Felicità"), "Giornata ottima")
    )

    val esercizi = mutableStateListOf(
        Exercise("e1", "u_terapeuta", "u_sofia", "Respirazione Diaframmatica", "Esegui 5 minuti al mattino", "Segui il ritmo", ExerciseStatus.DA_FARE)
    )

    // ── HELPER FUNCTIONS ──
    fun getCasesForTherapist(therapistId: String) = casi.filter { it.terapeutaId == therapistId }
    
    fun getCaseById(caseId: String) = casi.find { it.id == caseId }
    
    fun getPersonsForCase(caseId: String): List<Person> {
        val case = getCaseById(caseId) ?: return emptyList()
        return persone.filter { it.id in case.participantIds }
    }
    
    fun getConversationsForCase(caseId: String) = conversazioni.filter { it.caseId == caseId }
    
    fun getMessagesForConversation(conversationId: String) = messaggi
        .filter { it.conversationId == conversationId }
        .sortedBy { it.timestamp }
    
    fun addMessage(message: ChatMessage) {
        messaggi.add(message)
    }
    
    fun getAppointmentsForTherapist(therapistId: String) = appuntamenti
        .filter { it.terapeutaId == therapistId }
        .sortedBy { it.dataOra }
    
    fun getAppointmentsForCase(caseId: String) = appuntamenti
        .filter { it.caseId == caseId }
        .sortedBy { it.dataOra }
        
    fun addAppointment(appointment: Appointment) {
        appuntamenti.add(appointment)
    }

    fun updateAppointment(appointment: Appointment) {
        val index = appuntamenti.indexOfFirst { it.id == appointment.id }
        if (index != -1) appuntamenti[index] = appointment
    }
    
    fun getPersonById(personId: String) = persone.find { it.id == personId }
    
    fun getConversationById(convId: String) = conversazioni.find { it.id == convId }

    fun getUserByEmail(email: String) = utenti.find { it.email == email }

    fun getNotesForCase(caseId: String) = noteSessione
        .filter { it.caseId == caseId }
        .sortedByDescending { it.dataSessione }

    fun addNote(note: SessionNote) {
        noteSessione.add(note)
    }

    fun getMoodByPaziente(pazienteId: String) = moodEntries.filter { it.pazienteId == pazienteId }
    
    fun getEserciziByPaziente(pazienteId: String) = esercizi.filter { it.pazienteId == pazienteId }

    fun getPazientiByTerapeuta(terapeutaId: String) = utenti.filter { it.terapeutaId == terapeutaId }
}
