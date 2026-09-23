package com.example.ui.screens.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.HamroViewModel
import com.example.ui.theme.*
import com.example.ui.util.HamroStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersBookingsScreen(
    viewModel: HamroViewModel,
    onBack: () -> Unit
) {
    val orders by viewModel.userOrders.collectAsStateWithLifecycle()
    val sellerOrders by viewModel.userSellerOrders.collectAsStateWithLifecycle()
    val bookings by viewModel.userBookings.collectAsStateWithLifecycle()
    val providerBookings by viewModel.providerBookings.collectAsStateWithLifecycle()
    val rentalRequests by viewModel.userRentalRequests.collectAsStateWithLifecycle()
    val ownerRentalRequests by viewModel.ownerRentalRequests.collectAsStateWithLifecycle()
    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(0) } // 0: Orders, 1: Bookings, 2: Rental Requests

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (currentLang == "ne") "अर्डर तथा बुकिङहरू (Orders & Bookings)" else "Orders & Bookings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("orders_bookings_screen")
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = HamroCrimson
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("अर्डरहरू (${orders.size + sellerOrders.size})", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("सेवा बुकिङ (${bookings.size + providerBookings.size})", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("भाडा अनुरोध (${rentalRequests.size + ownerRentalRequests.size})", fontSize = 12.sp) }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        if (orders.isEmpty() && sellerOrders.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("हाल कुनै अर्डर छैन।", color = SlateTextMedium)
                                }
                            }
                        }

                        if (orders.isNotEmpty()) {
                            item {
                                Text("मेरो खरिद अर्डरहरू (My Purchases)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            items(orders, key = { "buy_${it.id}" }) { order ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(order.productTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            StatusChip(order.status)
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("विक्रेता: ${order.sellerName}", fontSize = 12.sp, color = SlateTextMedium)
                                        Text("माध्यम: ${order.paymentMethod}", fontSize = 12.sp, color = SlateTextMedium)
                                        Text("डेलिभरी ठेगाना: ${order.deliveryAddress}", fontSize = 12.sp, color = SlateTextMedium)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            "जम्मा: ${HamroStrings.formatNpr(order.totalPriceNpr, currentLang)} (परिमाण: ${order.quantity})",
                                            fontWeight = FontWeight.Bold,
                                            color = HamroCrimson,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }

                        if (sellerOrders.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("मेरो बिक्री अर्डरहरू (Sales Orders to Fulfill)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            items(sellerOrders, key = { "sell_${it.id}" }) { order ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(order.productTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            StatusChip(order.status)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("ग्राहक: ${order.buyerName} (${order.buyerPhone})", fontSize = 12.sp)
                                        Text("डेलिभरी: ${order.deliveryAddress}", fontSize = 12.sp)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = {
                                                    viewModel.updateOrderStatus(order.id, "Delivered", order.buyerId, order.productTitle)
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = NepalGreen),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                modifier = Modifier.height(32.dp)
                                            ) {
                                                Text("डेलिभर भयो (Delivered)", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        val allB = bookings + providerBookings
                        if (allB.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                                    Text("कुनै सेवा बुकिङ छैन।", color = SlateTextMedium)
                                }
                            }
                        }
                        items(allB, key = { "book_${it.id}" }) { booking ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(booking.serviceName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        StatusChip(booking.status)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("प्रदायक: ${booking.providerName} | ग्राहक: ${booking.customerName}", fontSize = 12.sp, color = SlateTextMedium)
                                    Text("समय: ${booking.bookingDate} at ${booking.bookingTime}", fontSize = 12.sp, color = SlateTextMedium)
                                    Text("ठेगाना: ${booking.address}", fontSize = 12.sp, color = SlateTextMedium)
                                    Text("विवरण: ${booking.workDescription}", fontSize = 12.sp, color = SlateTextDark)

                                    if (booking.status == "Pending") {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = {
                                                    viewModel.updateBookingStatus(booking.id, "Accepted", booking.customerId, booking.serviceName)
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = NepalGreen),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                                modifier = Modifier.height(32.dp)
                                            ) {
                                                Text("स्वीकार (Accept)", fontSize = 11.sp)
                                            }
                                            OutlinedButton(
                                                onClick = {
                                                    viewModel.updateBookingStatus(booking.id, "Completed", booking.customerId, booking.serviceName)
                                                },
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                                modifier = Modifier.height(32.dp)
                                            ) {
                                                Text("सम्पन्न (Complete)", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        val allR = rentalRequests + ownerRentalRequests
                        if (allR.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                                    Text("कुनै भाडा अनुरोध छैन।", color = SlateTextMedium)
                                }
                            }
                        }
                        items(allR, key = { "req_${it.id}" }) { req ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(req.rentalTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        StatusChip(req.status)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("भाडामा बस्ने: ${req.tenantName} (${req.tenantPhone})", fontSize = 12.sp)
                                    Text("सर्न चाहेको मिति: ${req.moveInDate}", fontSize = 12.sp, color = SlateTextMedium)
                                    Text("सन्देश: ${req.note}", fontSize = 12.sp, color = SlateTextDark)

                                    if (req.status == "Pending") {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = {
                                                    viewModel.updateRentalRequestStatus(req.id, "Accepted", req.tenantId, req.rentalTitle)
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = NepalGreen),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                                modifier = Modifier.height(32.dp)
                                            ) {
                                                Text("स्वीकार गर्नुहोस् (Accept)", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val color = when (status) {
        "Confirmed", "Accepted", "Delivered", "Completed" -> NepalGreen
        "Pending" -> HamroAmber
        "Cancelled", "Rejected" -> HamroCrimson
        else -> HamroNavy
    }
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = status,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
