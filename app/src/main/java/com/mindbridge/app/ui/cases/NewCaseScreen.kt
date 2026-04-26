package com.mindbridge.app.ui.cases

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mindbridge.app.data.model.*
import com.mindbridge.app.data.repository.MockRepository
import com.mindbridge.app.ui.theme.Violet500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCaseScreen(
    therapistId: String,
    onDismiss: () -> Unit,
    onSave: (CareCase) -> Unit
) {
    var titolo by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf(CaseType.INDIVIDUALE) }
    var nomePaziente by remember { mutableStateOf("") }
    var cognomePaziente by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuovo Caso Terapeutico") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null) }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Tipologia Percorso", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            
            // Selezione Tipo
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CaseType.values().forEach { type ->
                    FilterChip(
                        selected = tipo == type,
                        onClick = { tipo = type },
                        label = { Text(type.name, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            OutlinedTextField(
                value = titolo,
                onValueChange = { titolo = it },
                label = { Text("Titolo del Caso") },
                placeholder = { Text("es. Percorso Sofia Rossi") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Divider()
            Text("Paziente Principale / Referente", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = nomePaziente,
                onValueChange = { nomePaziente = it },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            OutlinedTextField(
                value = cognomePaziente,
                onValueChange = { cognomePaziente = it },
                label = { Text("Cognome") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(Modifier.weight(1f))
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    val personId = "p_${System.currentTimeMillis()}"
                    val newPerson = Person(
                        id = personId,
                        nome = nomePaziente,
                        cognome = cognomePaziente,
                        role = PersonRole.PAZIENTE
                    )
                    MockRepository.persone.add(newPerson)

                    val newCase = CareCase(
                        id = "c_${System.currentTimeMillis()}",
                        terapeutaId = therapistId,
                        titolo = titolo.ifBlank { "Percorso $nomePaziente $cognomePaziente" },
                        tipo = tipo,
                        primaryParticipantId = personId,
                        participantIds = listOf(personId)
                    )
                    onSave(newCase)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Violet500),
                enabled = nomePaziente.isNotBlank() && cognomePaziente.isNotBlank()
            ) {
                Icon(Icons.Default.Save, null)
                Spacer(Modifier.width(12.dp))
                Text("Crea Caso Terapeutico", fontWeight = FontWeight.Bold)
            }
        }
    }
}
