package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.RentalEntity
import com.example.data.local.entities.ServiceProviderEntity
import com.example.data.model.NepalLocations
import com.example.ui.theme.*
import com.example.ui.util.HamroStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPickerSheet(
    currentLocation: String,
    onLocationSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "स्थान छान्नुहोस् (Select Location)",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "प्रमुख शहरहरू (Popular Hubs):",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = SlateTextMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.heightIn(max = 350.dp)
            ) {
                items(NepalLocations.popularLocations) { loc ->
                    val isSelected = currentLocation == loc
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onLocationSelected(loc)
                                onDismiss()
                            }
                            .padding(vertical = 12.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Place,
                                contentDescription = null,
                                tint = if (isSelected) HamroCrimson else SlateTextMedium,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = loc,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) HamroCrimson else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        if (isSelected) {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = null,
                                tint = HamroCrimson,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    HorizontalDivider(color = BorderLight.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
fun OrderDialog(
    product: ProductEntity,
    currentLang: String,
    onDismiss: () -> Unit,
    onConfirmOrder: (quantity: Int, address: String, paymentMethod: String) -> Unit
) {
    var quantity by remember { mutableStateOf(1) }
    var deliveryAddress by remember { mutableStateOf("${product.district}, Ward ${product.ward}") }
    var selectedPaymentMethod by remember { mutableStateOf("Cash on Delivery") }

    val paymentMethods = listOf(
        "Cash on Delivery" to "सामान पाएपछि नगद भुक्तानी (COD)",
        "eSewa" to "ईसेवा वालेट (eSewa Online)",
        "Khalti" to "खल्ती वालेट (Khalti Digital)",
        "Fonepay" to "फोनपे डाइरेक्ट क्यूआर (Fonepay QR)",
        "Bank Transfer" to "बैंक ट्रान्सफर (Direct Bank Transfer)"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (currentLang == "ne") "सामान अर्डर गर्नुहोस्" else "Place Order",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = product.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "मूल्य: ${HamroStrings.formatNpr(product.priceNpr, currentLang)}",
                    fontWeight = FontWeight.Bold,
                    color = HamroCrimson,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Quantity selector
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (currentLang == "ne") "परिमाण (Quantity):" else "Quantity:",
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FilledTonalIconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = "$quantity",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )
                        FilledTonalIconButton(
                            onClick = { quantity++ },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = deliveryAddress,
                    onValueChange = { deliveryAddress = it },
                    label = { Text(if (currentLang == "ne") "डेलिभरी ठेगाना" else "Delivery Address") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = if (currentLang == "ne") "भुक्तानीको माध्यम (Payment Method):" else "Payment Method:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                paymentMethods.forEach { (key, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPaymentMethod = key }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedPaymentMethod == key,
                            onClick = { selectedPaymentMethod = key },
                            colors = RadioButtonDefaults.colors(selectedColor = HamroCrimson)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = label, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (currentLang == "ne") "कुल जम्मा रकम:" else "Total Amount:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = HamroStrings.formatNpr(product.priceNpr * quantity, currentLang),
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = HamroCrimson
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmOrder(quantity, deliveryAddress, selectedPaymentMethod)
                },
                colors = ButtonDefaults.buttonColors(containerColor = HamroCrimson)
            ) {
                Text(if (currentLang == "ne") "अर्डर पक्का गर्नुहोस्" else "Confirm Order")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (currentLang == "ne") "रद्द गर्नुहोस्" else "Cancel")
            }
        }
    )
}

@Composable
fun BookServiceDialog(
    provider: ServiceProviderEntity,
    currentLang: String,
    onDismiss: () -> Unit,
    onConfirmBooking: (date: String, time: String, address: String, desc: String) -> Unit
) {
    var bookingDate by remember { mutableStateOf("2026-09-25") }
    var bookingTime by remember { mutableStateOf("10:00 AM") }
    var address by remember { mutableStateOf("Kathmandu, Ward 10") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "${provider.serviceCategory} सेवा बुक गर्नुहोस्",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "सेवा प्रदायक: ${provider.name}",
                    fontSize = 13.sp,
                    color = SlateTextMedium
                )
                Text(
                    text = "सुरुवाती शुल्क: ${HamroStrings.formatNpr(provider.startingPriceNpr, currentLang)} (${provider.pricingType})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = HamroCrimson
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = bookingDate,
                    onValueChange = { bookingDate = it },
                    label = { Text(if (currentLang == "ne") "मिति (Date)" else "Date") },
                    leadingIcon = { Icon(Icons.Filled.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = bookingTime,
                    onValueChange = { bookingTime = it },
                    label = { Text(if (currentLang == "ne") "समय (Time Slot)" else "Time Slot") },
                    leadingIcon = { Icon(Icons.Filled.Schedule, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text(if (currentLang == "ne") "घर/कार्यस्थलको ठेगाना" else "Service Address") },
                    leadingIcon = { Icon(Icons.Filled.Place, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(if (currentLang == "ne") "समस्या वा कामकाे विवरण" else "Work Description") },
                    placeholder = { Text("जस्तै: भित्तामा पानी चुहिएको, नयाँ बत्ती जोड्नुपर्ने...") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmBooking(
                        bookingDate,
                        bookingTime,
                        address,
                        description.ifBlank { "सामान्य मर्मत कार्य (General maintenance)" }
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = HamroCrimson)
            ) {
                Text(if (currentLang == "ne") "अनुरोध पठाउनुहोस्" else "Send Request")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (currentLang == "ne") "रद्द गर्नुहोस्" else "Cancel")
            }
        }
    )
}

@Composable
fun RentalRequestDialog(
    rental: RentalEntity,
    currentLang: String,
    onDismiss: () -> Unit,
    onConfirmRequest: (moveInDate: String, note: String) -> Unit
) {
    var moveInDate by remember { mutableStateOf("2026-10-01") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (currentLang == "ne") "भाडा अनुरोध पठाउनुहोस्" else "Send Rental Request",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = rental.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Text(
                    text = "भाडा: ${HamroStrings.formatNpr(rental.monthlyRentNpr, currentLang)}/महिना",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = HamroCrimson
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = moveInDate,
                    onValueChange = { moveInDate = it },
                    label = { Text(if (currentLang == "ne") "सर्न चाहेको मिति (Move-in Date)" else "Move-in Date") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(if (currentLang == "ne") "घरबेटीलाई सन्देश (Note to Owner)" else "Note to Owner") },
                    placeholder = { Text("परिवार सदस्य संख्या, पेशा, बसाई अवधि आदि खुलाउनुहोस्...") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmRequest(moveInDate, note) },
                colors = ButtonDefaults.buttonColors(containerColor = HamroCrimson)
            ) {
                Text(if (currentLang == "ne") "अनुरोध पठाउनुहोस्" else "Send Request")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (currentLang == "ne") "रद्द गर्नुहोस्" else "Cancel")
            }
        }
    )
}

@Composable
fun ReportDialog(
    itemTitle: String,
    onDismiss: () -> Unit,
    onSubmitReport: (reason: String, details: String) -> Unit
) {
    val reasons = listOf(
        "Fake listing",
        "Scam",
        "Wrong information",
        "Offensive content",
        "Illegal item",
        "Spam",
        "Other"
    )
    var selectedReason by remember { mutableStateOf(reasons.first()) }
    var details by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "उजुरी / रिपोर्ट गर्नुहोस् (Report)", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(text = "सामग्री: $itemTitle", fontSize = 12.sp, color = SlateTextMedium)
                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "उजुरीको कारण (Reason):", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))

                reasons.forEach { r ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReason = r }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedReason == r,
                            onClick = { selectedReason = r },
                            colors = RadioButtonDefaults.colors(selectedColor = HamroCrimson)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = r, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text("थप विवरण (Optional details)") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmitReport(selectedReason, details) },
                colors = ButtonDefaults.buttonColors(containerColor = HamroCrimson)
            ) {
                Text("उजुरी बुझाउनुहोस्")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("रद्द") }
        }
    )
}

@Composable
fun ReviewDialog(
    targetName: String,
    onDismiss: () -> Unit,
    onSubmitReview: (rating: Int, comment: String) -> Unit
) {
    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "समीक्षा तथा रेटिङ (Review)", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "$targetName को लागि प्रतिक्रिया:", fontSize = 13.sp, color = SlateTextMedium)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    (1..5).forEach { star ->
                        IconButton(onClick = { rating = star }) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "$star Stars",
                                tint = if (star <= rating) HamroAmber else SlateTextMedium.copy(alpha = 0.3f),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("तपाईंको अनुभव लेख्नुहोस् (Comment)") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmitReview(rating, comment.ifBlank { "धेरै राम्रो सेवा र सामान। सिफारिस गर्दछु।" })
                },
                colors = ButtonDefaults.buttonColors(containerColor = HamroCrimson)
            ) {
                Text("समीक्षा बुझाउनुहोस्")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("रद्द") }
        }
    )
}
