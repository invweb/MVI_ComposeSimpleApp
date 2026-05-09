package com.example.myapplication.ui.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.logik.ChatIntent
import com.example.myapplication.model.ChatState
import com.example.myapplication.model.Message
import com.example.myapplication.logik.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: ChatViewModel) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.processIntent(ChatIntent.LoadData)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Top app bar")
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            MessageList(viewModel = viewModel, state = state)
        }
    }
}

@Composable
fun MessageList(state: ChatState, viewModel: ChatViewModel) {
    when (state) {

        ChatState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is ChatState.Success -> {
            val listState = rememberLazyListState()
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .weight(8f) // <-- The key point: we set the weight of 8 parts
                        .fillMaxWidth()
                        .background(Color(0xFFADD8E6)) // Light blue background
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        items(state.messages) { message ->
                            MessageItem(message = message)
                        }
                        items(state.messages) { message ->
                            MessagesCardItem(message = message) // Passing the data to the card display function
                            LaunchedEffect(Unit) {
                                if (state.messages.isNotEmpty()) {

                                    val lastIndex = state.messages.size - 1
                                    listState.animateScrollToItem(
                                        index = lastIndex,
                                    )
                                    Log.d("log", "$lastIndex")
                                }
                            }
                        }

                    }
                }

                Box(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxWidth()
                        .background(Color(0xFF90EE90)) // Light green background
                ) {
                    MessageInput(onSend = { text ->
                        viewModel.processIntent(ChatIntent.SendMessage(text))
                    })
                }

            }
        }

        is ChatState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.message)
            }
        }
    }
}

@Composable
fun MessagesCardItem(message: Message) {
    message.text
    message.isSentByMe
    message.id
}


@Composable
fun MessageItem(message: Message) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = if (message.isSentByMe) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Text(
            text = message.text,
            modifier = Modifier
                .background(
                    if (message.isSentByMe) Color.Blue else Color.Gray,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp),
            color = Color.White
        )
    }
}

@Composable
fun MessageInput(onSend: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .weight(1f),
                placeholder = { Text("Enter a message") }
            )
            Button(onClick = {
                if (text.isNotBlank()) {
                    onSend(text)
                    text = ""
                }
            }) {
                Text("Send")
            }
        }
    }
}