package com.mindbridge.app.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.mindbridge.app.data.model.ChatMessage
import com.mindbridge.app.data.repository.MockRepository
import com.mindbridge.app.ui.theme.Teal600
import com.mindbridge.app.ui.theme.Teal200
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    currentUserId: String,
    recipientId: String
) {
    // Recupero informazioni destinatario per l'header
    val recipient = remember(recipientId) { MockRepository.utenti.find { it.id == recipientId } }
    
    val messages = remember { mutableStateListOf<ChatMessage>() }
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Caricamento messaggi iniziali
    LaunchedEffect(currentUserId, recipientId) {
        messages.clear()
        messages.addAll(MockRepository.getMessaggi(currentUserId, recipientId))
    }

    // Scroll automatico all'ultimo messaggio
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    fun sendMessage() {
        val text = inputText.trim()
        if (text.isBlank()) return

        val newMessage = ChatMessage(
            id = "c${System.currentTimeMillis()}",
            senderId = currentUserId,
            receiverId = recipientId,
            contenuto = text,
            timestamp = LocalDateTime.now()
        )

        // Aggiorno sia la UI che il Repository (Persistenza)
        messages.add(newMessage)
        MockRepository.messaggi.add(newMessage)
        inputText = ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Avatar circolare con iniziali
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = recipient?.let { "${it.nome.first()}${it.cognome.first()}" } ?: "?",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        
                        Spacer(Modifier.width(12.dp))
                        
                        Column {
                            Text(
                                text = recipient?.let { "${it.nome} ${it.cognome}" } ?: "Chat",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Online",
                                style = MaterialTheme.typography.labelSmall,
                                color = Teal600
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            ChatInputBar(
                inputText = inputText,
                onInputChange = { inputText = it },
                onSendClick = { sendMessage() },
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding()
            )
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = messages,
                key = { it.id }
            ) { message ->
                MessageBubble(
                    message = message,
                    isMine = message.senderId == currentUserId
                )
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: ChatMessage,
    isMine: Boolean
) {
    val timeFormatter = remember {
        DateTimeFormatter.ofPattern("HH:mm")
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMine) 16.dp else 2.dp,
                bottomEnd = if (isMine) 2.dp else 16.dp
            ),
            color = if (isMine) Teal600 else MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = message.contenuto,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isMine) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = message.timestamp.format(timeFormatter),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isMine) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
private fun ChatInputBar(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Scrivi un messaggio...") },
                maxLines = 4,
                shape = RoundedCornerShape(24.dp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Send
                )
            )

            Button(
                onClick = onSendClick,
                enabled = inputText.isNotBlank(),
                shape = CircleShape,
                modifier = Modifier.size(50.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Invia")
            }
        }
    }
}
