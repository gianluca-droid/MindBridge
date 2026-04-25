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
    val pazienti = MockRepository.getPazientiByTerapeuta(user.id)
    val appuntamentiOggi = MockRepository.getAppuntamentiByUser(user.id)
        .filter { it.dataOra.toLocalDate() == LocalDate.now() }
    val tuttiAppuntamenti = MockRepository.getAppuntamentiByUser(user.id)
        .filter { it.stato == AppointmentStatus.CONFERMATO }
        .sortedBy { it.dataOra }
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
                StatCard(Icons.Outlined.People, "Pazienti",
                    "${pazienti.size}", Violet500, Modifier.weight(1f))
                StatCard(Icons.Outlined.Today, "Oggi",
                    "${appuntamentiOggi.size}", Teal500, Modifier.weight(1f))
                StatCard(Icons.Outlined.CalendarMonth, "Prossime",
                    "${tuttiAppuntamenti.size}", Info, Modifier.weight(1f))
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
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📅", style = MaterialTheme.typography.displayLarge)
                            Spacer(Modifier.height(8.dp))
                            Text("Nessun appuntamento oggi",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        } else {
            items(appuntamentiOggi) { app ->
                AppointmentCard(
                    appointment = app, isTherapist = true,
                    onClick = { onNavigateToNotes(app.pazienteId) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }

        // Prossimi appuntamenti
        if (tuttiAppuntamenti.isNotEmpty()) {
            item {
                Text(
                    "Prossimi Appuntamenti",
                    modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 12.dp),
                    style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold
                )
            }
            items(tuttiAppuntamenti.take(5)) { app ->
                AppointmentCard(
                    app, isTherapist = true,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }

        // Accesso rapido pazienti
        item {
            Text(
                "I tuoi Pazienti",
                modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 12.dp),
                style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold
            )
        }

        items(pazienti) { paziente ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clickable { onNavigateToChat(paziente.id) },
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(48.dp).clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Teal400, Teal600))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "${paziente.nome.first()}${paziente.cognome.first()}",
                            color = Color.White, fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("${paziente.nome} ${paziente.cognome}",
                            fontWeight = FontWeight.SemiBold)
                        Text(paziente.email, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row {
                        IconButton(onClick = { onNavigateToChat(paziente.id) }) {
                            Icon(Icons.Outlined.Chat, "Chat", tint = Teal600)
                        }
                        IconButton(onClick = { onNavigateToNotes(paziente.id) }) {
                            Icon(Icons.Outlined.NoteAlt, "Note", tint = Violet500)
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(100.dp)) }
    }
}
