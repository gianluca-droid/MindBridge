package com.mindbridge.app.ui.cases

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mindbridge.app.data.model.*
import com.mindbridge.app.data.repository.MockRepository
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseDetailScreen(
    caseId: String,
    onBackClick: () -> Unit,
    onChatClick: (String) -> Unit,
    onNewAppointmentClick: (String) -> Unit
) {
    val careCase = remember(caseId) { MockRepository.getCaseById(caseId) }
    val participants = remember(caseId) { MockRepository.getPersonsForCase(caseId) }
    val conversations = remember(caseId) { MockRepository.getConversationsForCase(caseId) }
    val appointments = remember(caseId) { MockRepository.getAppointmentsForCase(caseId) }
    
    var selectedPersonForEdit by remember { mutableStateOf<Person?>(null) }
    var showNewChatDialog by remember { mutableStateOf(false) }

    if (careCase == null) return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dettaglio Caso") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header Info
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = getCaseColor(careCase.tipo).copy(alpha = 0.05f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(careCase.titolo, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        SuggestionChip(onClick = {}, label = { Text(careCase.tipo.name) })
                    }
                }
            }

            // Partecipanti
            item {
                Text("Anagrafiche Partecipanti", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            items(participants) { person ->
                ParticipantItem(person, onClick = { selectedPersonForEdit = person })
            }

            // Chat Disponibili
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Conversazioni", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    TextButton(onClick = { showNewChatDialog = true }) {
                        Icon(Icons.Default.AddComment, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Nuova Chat")
                    }
                }
            }
            items(conversations) { conv ->
                OutlinedCard(
                    onClick = { onChatClick(conv.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Chat, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(12.dp))
                        Text(conv.title, style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.weight(1f))
                        Icon(Icons.Default.ChevronRight, null)
                    }
                }
            }

            // Appuntamenti
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Appuntamenti", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    TextButton(onClick = { onNewAppointmentClick(caseId) }) {
                        Icon(Icons.Default.Add, null)
                        Text("Nuovo")
                    }
                }
            }
            items(appointments) { appt ->
                AppointmentItem(appt)
            }
        }
    }

    // Dialog Modifica Anagrafica
    selectedPersonForEdit?.let { person ->
        PersonEditDialog(
            person = person,
            onDismiss = { selectedPersonForEdit = null },
            onSave = { updated ->
                // Mock update logic
                val index = MockRepository.persone.indexOfFirst { it.id == updated.id }
                if (index != -1) MockRepository.persone[index] = updated
                selectedPersonForEdit = null
            }
        )
    }

    // Dialog Nuova Chat
    if (showNewChatDialog) {
        NewChatDialog(
            caseId = caseId,
            onDismiss = { showNewChatDialog = false },
            onSave = { title ->
                val newConv = Conversation(
                    id = "conv${System.currentTimeMillis()}",
                    caseId = caseId,
                    title = title,
                    type = ConversationType.CASE_GROUP,
                    participantIds = careCase.participantIds + careCase.terapeutaId
                )
                MockRepository.conversazioni.add(newConv)
                showNewChatDialog = false
            }
        )
    }
}

@Composable
fun PersonEditDialog(person: Person, onDismiss: () -> Unit, onSave: (Person) -> Unit) {
    var telefono by remember { mutableStateOf(person.telefono) }
    var email by remember { mutableStateOf(person.email) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Anagrafica: ${person.nome}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Telefono") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { onSave(person.copy(telefono = telefono, email = email)) }) { Text("Salva") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annulla") }
        }
    )
}

@Composable
fun NewChatDialog(caseId: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var title by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuova Conversazione") },
        text = {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Titolo Chat (es. Solo Genitori)") }, modifier = Modifier.fillMaxWidth())
        },
        confirmButton = {
            Button(onClick = { onSave(title) }, enabled = title.isNotBlank()) { Text("Crea") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annulla") }
        }
    )
}

@Composable
fun ParticipantItem(person: Person, onClick: () -> Unit) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(person.nome.take(1), style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("${person.nome} ${person.cognome}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(person.role.name, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (person.telefono.isNotBlank()) {
                Icon(Icons.Default.Phone, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun AppointmentItem(appt: Appointment) {
    val formatter = DateTimeFormatter.ofPattern("dd MMM, HH:mm")
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Event, null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(appt.titolo, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(appt.dataOra.format(formatter), style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.weight(1f))
            Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) { Text(appt.status.name) }
        }
    }
}
