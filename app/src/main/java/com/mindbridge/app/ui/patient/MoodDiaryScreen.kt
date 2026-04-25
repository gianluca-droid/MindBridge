package com.mindbridge.app.ui.patient

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mindbridge.app.data.model.MoodEntry
import com.mindbridge.app.data.model.User
import com.mindbridge.app.data.repository.MockRepository
import com.mindbridge.app.ui.components.GradientHeader
import com.mindbridge.app.ui.components.MoodSelector
import com.mindbridge.app.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun MoodDiaryScreen(user: User) {
    var selectedMood by remember { mutableStateOf(3) }
    var notes by remember { mutableStateOf("") }
    var showSaved by remember { mutableStateOf(false) }
    val moodEntries = MockRepository.getMoodByPaziente(user.id).sortedByDescending { it.data }
    val formatter = DateTimeFormatter.ofPattern("EEEE d MMM", Locale.ITALIAN)
    val moodEmoji = listOf("", "😢", "😔", "😐", "😊", "😄")
    val moodColors = listOf(Color.Transparent, MoodTerrible, MoodBad, MoodNeutral, MoodGood, MoodExcellent)

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { GradientHeader("Diario dell'Umore", "Come ti senti oggi?") }

        // Mood input card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .offset(y = (-16).dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    MoodSelector(selectedMood, { selectedMood = it })

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Come è andata la giornata?") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        minLines = 3
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            MockRepository.moodEntries.add(
                                MoodEntry(
                                    id = "m${System.currentTimeMillis()}",
                                    pazienteId = user.id,
                                    data = LocalDate.now(),
                                    livello = selectedMood,
                                    emozioni = listOf(moodEmoji[selectedMood]),
                                    note = notes
                                )
                            )
                            showSaved = true
                            notes = ""
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Teal600)
                    ) {
                        Icon(Icons.Filled.Save, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Salva", fontWeight = FontWeight.SemiBold)
                    }

                    if (showSaved) {
                        Spacer(Modifier.height(8.dp))
                        Surface(color = Success.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)) {
                            Text("✅ Umore registrato!", modifier = Modifier.padding(12.dp),
                                color = Success, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        // Storico
        item {
            Text(
                "Storico", modifier = Modifier.padding(start = 20.dp, top = 8.dp, bottom = 8.dp),
                style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold
            )
        }

        // Mood bar chart (simple)
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Ultima settimana", style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        moodEntries.take(7).reversed().forEach { entry ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(moodEmoji[entry.livello])
                                Spacer(Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(28.dp)
                                        .height((entry.livello * 18).dp)
                                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(
                                                    moodColors[entry.livello],
                                                    moodColors[entry.livello].copy(alpha = 0.5f)
                                                )
                                            )
                                        )
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    entry.data.format(DateTimeFormatter.ofPattern("dd/MM")),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Lista entries
        items(moodEntries.take(10)) { entry ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = moodColors[entry.livello].copy(alpha = 0.15f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(moodEmoji[entry.livello], style = MaterialTheme.typography.titleLarge)
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            entry.data.format(formatter).replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium
                        )
                        if (entry.note.isNotBlank()) {
                            Text(entry.note, style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                        }
                    }
                    Text("${entry.livello}/5", fontWeight = FontWeight.Bold,
                        color = moodColors[entry.livello])
                }
            }
        }

        item { Spacer(Modifier.height(100.dp)) }
    }
}
