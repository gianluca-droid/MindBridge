package com.mindbridge.app.ui.patient

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mindbridge.app.data.model.User
import com.mindbridge.app.ui.components.GradientHeader
import com.mindbridge.app.ui.theme.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun BookAppointmentScreen(user: User) {
    val today = LocalDate.now()
    var selectedDate by remember { mutableStateOf(today.plusDays(1)) }
    var selectedSlot by remember { mutableStateOf("") }
    var booked by remember { mutableStateOf(false) }
    val formatter = DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.ITALIAN)

    // Slot disponibili mock
    val slots = listOf("09:00", "10:00", "11:00", "14:00", "15:00", "16:00", "17:00")

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { GradientHeader("Prenota Seduta", "Scegli data e orario") }

        // Date selector (prossimi 7 giorni)
        item {
            Text(
                "Seleziona una data",
                modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp),
                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (1..5).forEach { dayOffset ->
                    val date = today.plusDays(dayOffset.toLong())
                    val isSelected = date == selectedDate
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) Teal600 else MaterialTheme.colorScheme.surfaceVariant,
                        onClick = { selectedDate = date; selectedSlot = "" }
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                date.format(DateTimeFormatter.ofPattern("EEE", Locale.ITALIAN))
                                    .replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "${date.dayOfMonth}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Data selezionata
        item {
            Text(
                selectedDate.format(formatter).replaceFirstChar { it.uppercase() },
                modifier = Modifier.padding(start = 20.dp, top = 20.dp),
                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold
            )
        }

        // Slot orari
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                slots.chunked(3).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { slot ->
                            val isSelected = slot == selectedSlot
                            Surface(
                                modifier = Modifier.weight(1f).padding(vertical = 4.dp),
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) Teal600 else MaterialTheme.colorScheme.surfaceVariant,
                                onClick = { selectedSlot = slot }
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 14.dp)
                                ) {
                                    Text(
                                        slot,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                        // Fill remaining space if row < 3
                        repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }

        // Book button
        item {
            Button(
                onClick = { booked = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Teal600),
                enabled = selectedSlot.isNotBlank() && !booked
            ) {
                Text("Conferma Prenotazione", fontWeight = FontWeight.SemiBold)
            }
        }

        if (booked) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Success.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.CheckCircle, null, tint = Success, modifier = Modifier.size(32.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Prenotazione confermata! ✅",
                                fontWeight = FontWeight.Bold, color = Success)
                            Text(
                                "${selectedDate.format(formatter)} alle $selectedSlot",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(100.dp)) }
    }
}
