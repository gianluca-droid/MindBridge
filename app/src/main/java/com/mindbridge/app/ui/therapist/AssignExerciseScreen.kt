package com.mindbridge.app.ui.therapist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mindbridge.app.data.model.Exercise
import com.mindbridge.app.data.model.ExerciseStatus
import com.mindbridge.app.data.repository.MockRepository
import com.mindbridge.app.ui.theme.*
import java.time.LocalDate

@Composable
fun AssignExerciseScreen(patientId: String) {
    val paziente = MockRepository.utenti.find { it.id == patientId }
    var titolo by remember { mutableStateOf("") }
    var descrizione by remember { mutableStateOf("") }
    var istruzioni by remember { mutableStateOf("") }
    var assigned by remember { mutableStateOf(false) }

    // Template suggeriti
    val templates = listOf(
        Triple("Respirazione 4-7-8", "Tecnica di rilassamento respiratorio",
            "Inspira per 4s, trattieni per 7s, espira per 8s. Ripeti 4 volte."),
        Triple("Journaling Serale", "Scrivi i pensieri della giornata",
            "Ogni sera, dedica 10 minuti a scrivere liberamente."),
        Triple("Grounding 5-4-3-2-1", "Tecnica di radicamento sensoriale",
            "Identifica 5 cose che vedi, 4 che tocchi, 3 che senti, 2 che annusi, 1 che gusti.")
    )

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Violet500, Violet600)))
                    .padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                Column {
                    Text("Assegna Esercizio", color = Color.White,
                        style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold)
                    if (paziente != null) {
                        Text("Per ${paziente.nome} ${paziente.cognome}",
                            color = Violet300, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }

        // Template rapidi
        item {
            Text("Template Rapidi",
                modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp),
                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                templates.forEach { (t, d, i) ->
                    SuggestionChip(
                        onClick = { titolo = t; descrizione = d; istruzioni = i },
                        label = { Text(t, maxLines = 1, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
        }

        // Form
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    OutlinedTextField(
                        value = titolo, onValueChange = { titolo = it },
                        label = { Text("Titolo esercizio") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp), singleLine = true
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = descrizione, onValueChange = { descrizione = it },
                        label = { Text("Descrizione") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp), minLines = 2
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = istruzioni, onValueChange = { istruzioni = it },
                        label = { Text("Istruzioni dettagliate") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp), minLines = 3
                    )

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            MockRepository.esercizi.add(
                                Exercise(
                                    id = "e${System.currentTimeMillis()}", terapeutaId = "t1",
                                    pazienteId = patientId, titolo = titolo,
                                    descrizione = descrizione, istruzioni = istruzioni,
                                    stato = ExerciseStatus.DA_FARE,
                                    dataAssegnazione = LocalDate.now(),
                                    dataScadenza = LocalDate.now().plusDays(7)
                                )
                            )
                            assigned = true
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Violet500),
                        enabled = titolo.isNotBlank() && descrizione.isNotBlank() && !assigned
                    ) {
                        Icon(Icons.Filled.Send, null, Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Assegna Esercizio", fontWeight = FontWeight.SemiBold)
                    }

                    if (assigned) {
                        Spacer(Modifier.height(8.dp))
                        Surface(color = Success.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)) {
                            Text("✅ Esercizio assegnato a ${paziente?.nome}!",
                                modifier = Modifier.padding(12.dp), color = Success)
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(100.dp)) }
    }
}
