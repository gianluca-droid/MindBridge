package com.mindbridge.app.ui.cases

import androidx.compose.foundation.background
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
                SectionHeader("Partecipanti")
            }
            items(participants) { person ->
                ParticipantItem(person)
            }

            // Chat Disponibili
            item {
                SectionHeader("Conversazioni")
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
                    SectionHeader("Appuntamenti")
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
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun ParticipantItem(person: Person) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(person.nome.take(1), style = MaterialTheme.typography.labelLarge)
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text("${person.nome} ${person.cognome}", style = MaterialTheme.typography.bodyLarge)
            Text(person.role.name, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun AppointmentItem(appt: Appointment) {
    val formatter = DateTimeFormatter.ofPattern("dd MMM, HH:mm")
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
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
            Badge { Text(appt.status.name) }
        }
    }
}
