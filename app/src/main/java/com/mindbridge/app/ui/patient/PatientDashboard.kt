package com.mindbridge.app.ui.patient

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
import androidx.compose.ui.unit.sp
import com.mindbridge.app.data.model.*
import com.mindbridge.app.data.repository.MockRepository
import com.mindbridge.app.ui.components.*
import com.mindbridge.app.ui.theme.*
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun PatientDashboard(
    user: User,
    onNavigateToMood: () -> Unit,
    onNavigateToExercises: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToChat: (String) -> Unit
) {
    val moods = MockRepository.getMoodByPaziente(user.id)
    val appointments = MockRepository.appuntamenti
        .filter { it.participantIds.contains(user.personId) }
        .filter { it.status == AppointmentStatus.CONFERMATO }
        .sortedBy { it.dataOra }
    val exercises = MockRepository.getEserciziByPaziente(user.id)
    val todayMood = moods.lastOrNull()
    val pendingExercises = exercises.count { it.stato != ExerciseStatus.COMPLETATO }
    val formatter = DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.ITALIAN)

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        // Header con gradiente
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Teal600, Teal700)))
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                Column {
                    Text("Buongiorno,", style = MaterialTheme.typography.bodyLarge, color = Teal200)
                    Text(
                        "${user.nome} 👋",
                        style = MaterialTheme.typography.displayMedium,
                        color = Color.White, fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        java.time.LocalDate.now().format(formatter)
                            .replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Teal200
                    )
                }
            }
        }

        // Statistiche rapide
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-16).dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    Icons.Outlined.Mood, "Umore oggi",
                    todayMood?.let { "${it.livello}/5" } ?: "—",
                    Teal500, Modifier.weight(1f)
                )
                StatCard(
                    Icons.Outlined.Assignment, "Da fare",
                    "$pendingExercises",
                    Violet500, Modifier.weight(1f)
                )
                StatCard(
                    Icons.Outlined.CalendarMonth, "Sedute",
                    "${appointments.size}",
                    Info, Modifier.weight(1f)
                )
            }
        }

        // Quick actions
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionButton(
                    "😊 Registra\nUmore", Teal100, Teal700, Modifier.weight(1f), onNavigateToMood
                )
                QuickActionButton(
                    "📋 I miei\nEsercizi", Violet300.copy(alpha=0.3f), Violet600,
                    Modifier.weight(1f), onNavigateToExercises
                )
                QuickActionButton(
                    "📅 Prenota\nSeduta", Color(0xFFDCFCE7), Color(0xFF16A34A),
                    Modifier.weight(1f), onNavigateToAppointments
                )
            }
        }

        // Prossimo appuntamento
        if (appointments.isNotEmpty()) {
            item {
                SectionHeader("Prossimo Appuntamento")
                AppointmentCard(
                    appointments.first(),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // Esercizi in corso
        val activeExercises = exercises.filter { it.stato != ExerciseStatus.COMPLETATO }
        if (activeExercises.isNotEmpty()) {
            item { SectionHeader("Esercizi da Completare") }
            items(activeExercises) { exercise ->
                ExerciseCard(
                    exercise,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }

        // Chat con terapeuta
        if (user.terapeutaId != null) {
            val conversation = MockRepository.conversazioni.find { 
                it.participantIds.contains(user.id) && it.participantIds.contains(user.terapeutaId) 
            }
            item {
                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { conversation?.let { onNavigateToChat(it.id) } },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Teal50)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Teal600),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Chat, null, tint = Color.White)
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Scrivi al terapeuta", fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.titleMedium)
                            Text("Hai bisogno di parlare?",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(Icons.Filled.ArrowForwardIos, null,
                            modifier = Modifier.size(16.dp),
                            tint = Teal600)
                    }
                }
            }
        }

        item { Spacer(Modifier.height(100.dp)) }
    }
}

@Composable
private fun QuickActionButton(
    label: String, bgColor: Color, textColor: Color,
    modifier: Modifier, onClick: () -> Unit
) {
    Surface(
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(20.dp),
        color = bgColor,
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(8.dp)) {
            Text(label, color = textColor, style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 18.sp)
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        title,
        modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 12.dp),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
}
