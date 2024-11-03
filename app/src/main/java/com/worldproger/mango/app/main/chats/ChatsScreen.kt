package com.worldproger.mango.app.main.chats

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.worldproger.mango.R
import com.worldproger.mango.domain.model.UserModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreen(
    onNavigateToChat: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: ChatsViewModel = koinViewModel()
) {
    val user by viewModel.currentUser.collectAsState()
    val chats by viewModel.chats.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.updateData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.chats_title)) },
                actions = {
                    UserAvatar(
                        user = user,
                        onClick = onNavigateToProfile
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToChat("new") }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.create_chat)
                )
            }
        }
    ) { innerPadding ->
        if (chats.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(R.string.no_chats_available))
            }
        } else {
            LazyColumn(
                contentPadding = innerPadding,
                modifier = Modifier.fillMaxSize()
            ) {
                items(chats) { chat ->
                    ChatItem(chat = chat, onClick = { onNavigateToChat(chat.id) })
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun UserAvatar(
    user: UserModel?,
    onClick: () -> Unit
) {
    val modifier = Modifier
        .padding(end = 8.dp)
        .size(40.dp)
        .clip(CircleShape)
        .clickable { onClick() }

    if (user != null) {
        val avatarUrl = user.avatars?.miniAvatar
        if (avatarUrl != null) {
            Image(
                painter = rememberAsyncImagePainter(avatarUrl),
                contentDescription = stringResource(R.string.user_avatar_description),
                modifier = modifier,
                contentScale = ContentScale.Crop
            )
        } else {
            val initials = user.name.take(2).uppercase()
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier.background(MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = initials,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    } else {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = stringResource(R.string.profile_description),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun ChatItem(
    chat: Chat,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(onClick = onClick),
        headlineContent = { Text(chat.title) },
        supportingContent = { Text(chat.lastMessage) }
    )
}