package com.example.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.HamroViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    viewModel: HamroViewModel,
    onBack: () -> Unit
) {
    val notifications by viewModel.userNotifications.collectAsStateWithLifecycle()
    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (currentLang == "ne") "सूचनाहरू (Notifications)" else "Notifications", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (notifications.isNotEmpty()) {
                        TextButton(onClick = { viewModel.markAllNotificationsRead() }) {
                            Text(if (currentLang == "ne") "सबै पढिसकें" else "Mark all read", fontSize = 12.sp)
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .testTag("empty_notifications"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.NotificationsNone,
                        contentDescription = null,
                        modifier = Modifier.size(54.dp),
                        tint = SlateTextMedium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (currentLang == "ne") "हाल कुनै नयाँ सूचना छैन।" else "No notifications yet.",
                        color = SlateTextMedium,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .testTag("notifications_list")
            ) {
                items(notifications, key = { it.id }) { notif ->
                    val sdf = remember { SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()) }
                    val timeStr = sdf.format(Date(notif.timestamp))

                    Surface(
                        color = if (notif.isRead) MaterialTheme.colorScheme.surface else HamroCrimson.copy(alpha = 0.05f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.markNotificationAsRead(notif.id) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            val icon = when (notif.type) {
                                "Order" -> Icons.Filled.ShoppingBag
                                "Booking" -> Icons.Filled.CalendarMonth
                                "Rental" -> Icons.Filled.HomeWork
                                "Message" -> Icons.Filled.Chat
                                else -> Icons.Filled.Notifications
                            }
                            val iconColor = when (notif.type) {
                                "Order" -> NepalGreen
                                "Booking" -> HamroAmber
                                "Rental" -> HamroNavy
                                else -> HamroCrimson
                            }

                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = iconColor,
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(top = 2.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = notif.title,
                                        fontWeight = if (notif.isRead) FontWeight.SemiBold else FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = timeStr,
                                        fontSize = 10.sp,
                                        color = SlateTextMedium
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = notif.message,
                                    fontSize = 12.sp,
                                    color = SlateTextDark,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                    HorizontalDivider(color = BorderLight.copy(alpha = 0.5f))
                }
            }
        }
    }
}
