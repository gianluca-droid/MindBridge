package com.mindbridge.app.ui.therapist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mindbridge.app.data.model.SessionNote
import com.mindbridge.app.data.repository.MockRepository
import com.mindbridge.app.ui.theme.*
import java.time.LocalDate

@Composable
fun SessionNotesScreen(patientId: String) {
    val paziente = MockRepository.utenti.find { it.id == patientId }
    val noteEsistenti = MockRepository.noteSessione.filter { it.pazienteId == patientId }
    var contenuto by remember { mutableStateOf("") }
    var obiettivi by remember { mutableStateOf("") }
    var progressi by remember { mutableStateOf("") }
    var prossimiPassi by remember { mutableStateOf("") }
    var saved by remember { mutableStateOf(false) }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Violet500, Violet600)))
                    .padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                Column {
                    Text("Note di Sessione", color = Color.White,
                        style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold)
                    if (paziente != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(28.dp).clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${paziente.nome.first()}", color = Color.White,
                                    style = MaterialTheme.typography.labelMedium)
                            }
                            Spacer(Modifier.width(8.dp))
                            Text("${paziente.nome} ${paziente.cognome}",
                                color = Violet300, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }

        // Nuova nota
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Nuova Nota", style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))

                    NoteField("Contenuto della sessione", contenuto, { contenuto = it }, 4)
                    Spacer(Modifier.height(12.dp))
                    NoteField("Obiettivi terapeutici", obiettivi, { obiettivi = it }, 2)
                    Spacer(Modifier.height(12.dp))
                    NoteField("Progressi osservati", progressi, { progressi = it }, 2)
                    Spacer(Modifier.height(12.dp))
                    NoteField("Prossimi passi", prossimiPassi, { prossimiPassi = it }, 2)

                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            MockRepository.noteSessione.add(
                                SessionNote(
                                    id = "n${System.currentTimeMillis()}", terapeutaId = "t1",
                                    pazienteId = patientId, dataSessione = LocalDate.now(),
                                    contenuto = contenuto, obiettivi = obiettivi,
                                    progressi = progressi, prossimiPassi = prossimiPassi
                                )
                            )
                            saved = true
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Violet500),
                        enabled = contenuto.isNotBlank()
                    ) {
                        Icon(Icons.Filled.Save, null, Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Salva Nota", fontWeight = FontWeight.SemiBold)
                    }

                    if (saved) {
                        Spacer(Modifier.height(8.dp))
                        Surface(color = Success.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)) {
                            Text("✅ Nota salvata!", modifier = Modifier.padding(12.dp),
                                color = Success)
                        }
                    }
                }
            }
        }

        // Note precedenti
        if (noteEsistenti.isNotEmpty()) {
            item {
                Text("Note Precedenti",
                    modifier = Modifier.padding(start = 20.dp, top = 8.dp, bottom = 8.dp),
                    style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            noteEsistenti.forEach { nota ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("📅 ${nota.dataSessione}", fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(8.dp))
                            Text(nota.contenuto, style = MaterialTheme.typography.bodyMedium)
                            if (nota.progressi.isNotBlank()) {
                                Spacer(Modifier.height(6.dp))
                                Text("📈 ${nota.progressi}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Success)
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(100.dp)) }
    }
}

@Composable
private fun NoteField(label: String, value: String, onValueChange: (String) -> Unit, lines: Int) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        minLines = lines
    )
}
