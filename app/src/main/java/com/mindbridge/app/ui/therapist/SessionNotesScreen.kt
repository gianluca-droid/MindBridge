package com.mindbridge.app.ui.therapist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionNotesScreen(
    caseId: String,
    therapistId: String,
    onBackClick: () -> Unit
) {
    val careCase = remember(caseId) { MockRepository.getCaseById(caseId) }
    val noteEsistenti = remember(caseId) { MockRepository.getNotesForCase(caseId) }
    
    var contenuto by remember { mutableStateOf("") }
    var progressi by remember { mutableStateOf("") }
    var compiti by remember { mutableStateOf("") }
    var saved by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Note di Sessione") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            // Header Info Caso
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(Violet500, Violet600)))
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                ) {
                    Column {
                        Text(careCase?.titolo ?: "Caso", color = Color.White,
                            style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(careCase?.tipo?.name ?: "", color = Violet300, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // Form Nuova Nota
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Nuova Nota", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(16.dp))

                        NoteField("Contenuto della sessione", contenuto, { contenuto = it }, 4)
                        Spacer(Modifier.height(12.dp))
                        NoteField("Progressi e Osservazioni", progressi, { progressi = it }, 2)
                        Spacer(Modifier.height(12.dp))
                        NoteField("Compiti assegnati", compiti, { compiti = it }, 2)

                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = {
                                val nuovaNota = SessionNote(
                                    id = "n${System.currentTimeMillis()}",
                                    caseId = caseId,
                                    terapeutaId = therapistId,
                                    dataSessione = LocalDateTime.now(),
                                    contenuto = contenuto,
                                    progressi = progressi,
                                    compiti = compiti
                                )
                                MockRepository.addNote(nuovaNota)
                                saved = true
                                contenuto = ""
                                progressi = ""
                                compiti = ""
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
                                Text("✅ Nota salvata!", modifier = Modifier.padding(12.dp), color = Success)
                            }
                        }
                    }
                }
            }

            // Lista Note Precedenti
            if (noteEsistenti.isNotEmpty()) {
                item {
                    Text("Note Precedenti",
                        modifier = Modifier.padding(start = 20.dp, top = 8.dp, bottom = 8.dp),
                        style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                items(noteEsistenti) { nota ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "📅 ${nota.dataSessione.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"))}",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(nota.contenuto, style = MaterialTheme.typography.bodyLarge)
                            if (nota.progressi.isNotBlank()) {
                                Spacer(Modifier.height(8.dp))
                                Text("📈 Progressi: ${nota.progressi}", style = MaterialTheme.typography.bodySmall, color = Teal700)
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(100.dp)) }
        }
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
