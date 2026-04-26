package com.mindbridge.app.ui.therapist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mindbridge.app.data.model.*
import com.mindbridge.app.data.repository.MockRepository
import com.mindbridge.app.ui.components.*
import com.mindbridge.app.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TherapistDashboard(
    user: User,
    onNavigateToPatients: () -> Unit,
    onNavigateToChat: (String) -> Unit,
    onNavigateToNotes: (String) -> Unit
) {
    val casi = MockRepository.getCasesForTherapist(user.id)
    val tuttiAppuntamenti = MockRepository.getAppointmentsForTherapist(user.id)
    val appuntamentiOggi = tuttiAppuntamenti.filter { it.dataOra.toLocalDate() == LocalDate.now() }
    val prossimiAppuntamenti = tuttiAppuntamenti.filter { 
        it.dataOra.toLocalDate() > LocalDate.now() && it.status == AppointmentStatus.CONFERMATO 
    }
    val conversazioni = MockRepository.conversazioni.filter { it.participantIds.contains(user.id) }
    
    val formatter = DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.ITALIAN)

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Violet500, Violet600)))
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                Column {
                    Text("Bentornata,", style = MaterialTheme.typography.bodyLarge,
                        color = Violet300)
                    Text(
                        "Dr.ssa ${user.cognome} 👩‍⚕️",
                        style = MaterialTheme.typography.displayMedium,
                        color = Color.White, fontWeight = FontWeight.Bold
                    )
                    Text(
                        LocalDate.now().format(formatter).replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyMedium, color = Violet300
                    )
                }
            }
        }

        // Stats
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-16).dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(Icons.Outlined.People, "Casi",
                    "${casi.size}", Violet500, Modifier.weight(1f))
                StatCard(Icons.Outlined.Today, "Oggi",
                    "${appuntamentiOggi.size}", Teal500, Modifier.weight(1f))
                StatCard(Icons.Outlined.CalendarMonth, "Agenda",
                    "${prossimiAppuntamenti.size}", Info, Modifier.weight(1f))
            }
        }

        // Appuntamenti di oggi
        item {
            Text(
                "Appuntamenti di Oggi",
                modifier = Modifier.padding(start = 20.dp, top = 8.dp, bottom = 12.dp),
                style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold
            )
        }

        if (appuntamentiOggi.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("Nessun appuntamento in programma per oggi", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(appuntamentiOggi) { app ->
                AppointmentCard(
                    appointment = app, isTherapist = true,
                    onClick = { onNavigateToNotes(app.caseId) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }

        // Prossime Chat (Novità)
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 20.dp, top = 24.dp, bottom = 12.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Chat Recenti", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
        }

        items(conversazioni.take(3)) { conv ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clickable { onNavigateToChat(conv.id) },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Teal50)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Teal600),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Chat, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(conv.title, fontWeight = FontWeight.SemiBold)
                        Text("Ultimo messaggio...", style = MaterialTheme.typography.bodySmall, color = Teal800)
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = Teal600)
                }
            }
        }

        // Prossimi appuntamenti (Ridotto)
        if (prossimiAppuntamenti.isNotEmpty()) {
            item {
                Text(
                    "Prossimi giorni",
                    modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 12.dp),
                    style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold
                )
            }
            items(prossimiAppuntamenti.take(3)) { app ->
                AppointmentCard(
                    app, isTherapist = true,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }

        item { Spacer(Modifier.height(100.dp)) }
    }
}
