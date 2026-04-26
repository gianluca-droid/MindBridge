package com.mindbridge.app.ui.cases

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mindbridge.app.data.model.CareCase
import com.mindbridge.app.data.model.CaseType
import com.mindbridge.app.data.repository.MockRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CasesScreen(
    therapistId: String,
    onCaseClick: (String) -> Unit
) {
    val cases = remember { MockRepository.getCasesForTherapist(therapistId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Assistiti", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { /* Filtra */ }) {
                        Icon(Icons.Default.FilterList, "Filtra")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Nuovo Caso */ }) {
                Icon(Icons.Default.Add, "Nuovo Caso")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cases) { careCase ->
                CaseItem(careCase = careCase, onClick = { onCaseClick(careCase.id) })
            }
        }
    }
}

@Composable
fun CaseItem(careCase: CareCase, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icona basata sul tipo di caso
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(getCaseColor(careCase.tipo).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCaseIcon(careCase.tipo),
                    contentDescription = null,
                    tint = getCaseColor(careCase.tipo)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = careCase.titolo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SuggestionChip(
                        onClick = { },
                        label = { Text(careCase.tipo.name, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.height(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "${careCase.participantIds.size} persone",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun getCaseIcon(type: CaseType): ImageVector = when (type) {
    CaseType.INDIVIDUALE -> Icons.Default.Person
    CaseType.MINORE -> Icons.Default.ChildCare
    CaseType.COPPIA -> Icons.Default.People
    CaseType.FAMIGLIA -> Icons.Default.Groups
}

fun getCaseColor(type: CaseType): Color = when (type) {
    CaseType.INDIVIDUALE -> Color(0xFF0D9488) // Teal
    CaseType.MINORE -> Color(0xFFF59E0B)      // Amber
    CaseType.COPPIA -> Color(0xFF8B5CF6)      // Violet
    CaseType.FAMIGLIA -> Color(0xFFEF4444)    // Red
}
