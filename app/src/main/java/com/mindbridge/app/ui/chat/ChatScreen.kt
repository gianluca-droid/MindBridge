package com.mindbridge.app.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mindbridge.app.data.model.ChatMessage
import com.mindbridge.app.data.repository.MockRepository
import com.mindbridge.app.ui.components.ChatBubble
import com.mindbridge.app.ui.theme.*
import java.time.LocalDateTime

@Composable
fun ChatScreen(currentUserId: String, recipientId: String) {
    val recipient = MockRepository.utenti.find { it.id == recipientId }
    val messages = remember { mutableStateListOf<ChatMessage>() }
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        messages.addAll(MockRepository.getMessaggi(currentUserId, recipientId))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Chat header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Teal600,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        recipient?.let { "${it.nome.first()}${it.cognome.first()}" } ?: "?",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        recipient?.let { "${it.nome} ${it.cognome}" } ?: "Chat",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Online",
                        color = Teal200,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Messages
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            state = listState,
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages) { message ->
                ChatBubble(
                    message = message,
                    isMine = message.senderId == currentUserId,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }

        // Input
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Scrivi un messaggio...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = false,
                    maxLines = 3
                )
                Spacer(Modifier.width(8.dp))
                FilledIconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            val newMsg = ChatMessage(
                                id = "c${System.currentTimeMillis()}",
                                senderId = currentUserId,
                                receiverId = recipientId,
                                contenuto = inputText,
                                timestamp = LocalDateTime.now()
                            )
                            messages.add(newMsg)
                            MockRepository.messaggi.add(newMsg)
                            inputText = ""
                        }
                    },
                    modifier = Modifier.size(48.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Teal600
                    )
                ) {
                    Icon(Icons.Filled.Send, "Invia", tint = Color.White)
                }
            }
        }
    }
}
