package com.example.ui.screens.chat

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.MessageEntity
import com.example.ui.HamroViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    viewModel: HamroViewModel,
    onOpenConversation: (receiverId: Long, receiverName: String, itemId: Long, itemTitle: String) -> Unit
) {
    val currentUserId by viewModel.currentUserId.collectAsStateWithLifecycle()
    val allMessages by viewModel.userMessages.collectAsStateWithLifecycle()
    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()

    // Group messages by conversationId
    val conversations = remember(allMessages, currentUserId) {
        allMessages.groupBy { it.conversationId }.values.mapNotNull { msgs ->
            val last = msgs.maxByOrNull { it.timestamp } ?: return@mapNotNull null
            val otherUserId = if (last.senderId == currentUserId) last.receiverId else last.senderId
            val otherUserName = if (last.senderId == currentUserId) last.receiverName else last.senderName
            Triple(last, otherUserId, otherUserName)
        }.sortedByDescending { it.first.timestamp }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (currentLang == "ne") "च्याट तथा सन्देशहरू (Messages)" else "Messages", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        if (conversations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .testTag("empty_chat_list"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.ChatBubbleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = SlateTextMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (currentLang == "ne") "हालसम्म कुनै कुराकानी भएको छैन।" else "No messages yet.",
                        fontSize = 15.sp,
                        color = SlateTextMedium
                    )
                    Text(
                        text = "कुनै पनि सामान वा सेवामा 'च्याट' थिचेर कुराकानी सुरु गर्नुहोस्।",
                        fontSize = 12.sp,
                        color = SlateTextMedium
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .testTag("chat_list_column")
            ) {
                items(conversations, key = { it.first.conversationId }) { (lastMsg, otherId, otherName) ->
                    val sdf = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
                    val timeStr = sdf.format(Date(lastMsg.timestamp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onOpenConversation(otherId, otherName, lastMsg.itemId, lastMsg.itemTitle)
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .testTag("conv_item_${lastMsg.conversationId}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(HamroNavy.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = otherName.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = HamroNavy,
                                fontSize = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = otherName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = timeStr,
                                    fontSize = 11.sp,
                                    color = SlateTextMedium
                                )
                            }

                            if (lastMsg.itemTitle.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = HamroCrimson.copy(alpha = 0.08f),
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Re: ${lastMsg.itemTitle}",
                                        fontSize = 10.sp,
                                        color = HamroCrimson,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Text(
                                text = lastMsg.text,
                                fontSize = 13.sp,
                                color = SlateTextDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    HorizontalDivider(color = BorderLight.copy(alpha = 0.6f))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatConversationScreen(
    receiverId: Long,
    receiverName: String,
    itemId: Long,
    itemTitle: String,
    viewModel: HamroViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val currentUserId by viewModel.currentUserId.collectAsStateWithLifecycle()
    val allMessages by viewModel.userMessages.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val otherUser = allUsers.find { it.id == receiverId }

    val convId = remember(currentUserId, receiverId) {
        if (currentUserId < receiverId) "${currentUserId}_$receiverId" else "${receiverId}_$currentUserId"
    }

    val conversationMessages = remember(allMessages, convId) {
        allMessages.filter { it.conversationId == convId }.sortedBy { it.timestamp }
    }

    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val quickChips = listOf(
        "के यो अझै उपलब्ध छ?",
        "मूल्य केही मिल्न सक्छ?",
        "कहिले भेट्न सकिन्छ?",
        "म हेर्न आउन चाहन्छु।"
    )

    LaunchedEffect(conversationMessages.size) {
        if (conversationMessages.isNotEmpty()) {
            listState.animateScrollToItem(conversationMessages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(receiverName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        if (itemTitle.isNotBlank()) {
                            Text(
                                text = "Item: $itemTitle",
                                fontSize = 11.sp,
                                color = SlateTextMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    otherUser?.phone?.let { phone ->
                        IconButton(onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                            context.startActivity(intent)
                        }) {
                            Icon(Icons.Filled.Call, contentDescription = "Call", tint = HamroCrimson)
                        }
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .imePadding()
            ) {
                // Quick Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickChips.forEach { chip ->
                        SuggestionChip(
                            onClick = {
                                viewModel.sendMessage(
                                    receiverId = receiverId,
                                    receiverName = receiverName,
                                    text = chip,
                                    itemId = itemId,
                                    itemTitle = itemTitle
                                )
                            },
                            label = { Text(chip, fontSize = 11.sp) }
                        )
                    }
                }

                // Input row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("सन्देश लेख्नुहोस् (Message)...") },
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                viewModel.sendMessage(
                                    receiverId = receiverId,
                                    receiverName = receiverName,
                                    text = messageText.trim(),
                                    itemId = itemId,
                                    itemTitle = itemTitle
                                )
                                messageText = ""
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(HamroCrimson)
                            .testTag("chat_send_button")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .testTag("chat_messages_column")
        ) {
            items(conversationMessages, key = { it.id }) { msg ->
                val isMe = msg.senderId == currentUserId
                val sdf = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
                val timeStr = sdf.format(Date(msg.timestamp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 14.dp,
                            topEnd = 14.dp,
                            bottomStart = if (isMe) 14.dp else 2.dp,
                            bottomEnd = if (isMe) 2.dp else 14.dp
                        ),
                        color = if (isMe) HamroCrimson else MaterialTheme.colorScheme.surfaceVariant,
                        tonalElevation = 1.dp,
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = msg.text,
                                color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = timeStr,
                                color = if (isMe) Color.White.copy(alpha = 0.7f) else SlateTextMedium,
                                fontSize = 9.sp,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }
    }
}
