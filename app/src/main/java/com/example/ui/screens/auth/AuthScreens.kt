package com.example.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.HamroViewModel
import com.example.ui.theme.HamroCrimson
import com.example.ui.theme.HamroNavy
import com.example.ui.theme.NepalGreen
import com.example.ui.theme.SlateTextMedium

@Composable
fun OtpAuthDialog(
    viewModel: HamroViewModel,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var step by remember { mutableStateOf(1) } // 1: Enter Phone, 2: Enter OTP
    var phone by remember { mutableStateOf("9812345678") }
    var name by remember { mutableStateOf("Bikash Sharma") }
    var otp by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (step == 1) "लगइन वा दर्ता (Login/Register)" else "OTP प्रमाणीकरण (Verify OTP)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (step == 1) {
                    Text(
                        text = "नेपाल मोबाइल नम्बर प्रयोग गरी तुरुन्तै लगइन गर्नुहोस्:",
                        fontSize = 13.sp,
                        color = SlateTextMedium
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("तपाईंको नाम (Full Name)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it.filter { c -> c.isDigit() } },
                        label = { Text("मोबाइल नम्बर (Mobile Number)") },
                        leadingIcon = {
                            Text(
                                text = "+977 ",
                                fontWeight = FontWeight.Bold,
                                color = HamroCrimson,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (errorMsg.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(errorMsg, color = HamroCrimson, fontSize = 12.sp)
                    }
                } else {
                    Text(
                        text = "+977-$phone मा ६-अंकको OTP कोड पठाइएको छ:",
                        fontSize = 13.sp,
                        color = SlateTextMedium
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = otp,
                        onValueChange = { otp = it.filter { c -> c.isDigit() }.take(6) },
                        label = { Text("६-अंकको OTP कोड") },
                        placeholder = { Text("123456") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("otp_input_field")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = { otp = "123456" },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("परीक्षण कोड भर्नुहोस् (Auto-fill 123456)", fontSize = 11.sp, color = HamroCrimson)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (step == 1) {
                        if (phone.length >= 10) {
                            step = 2
                            errorMsg = ""
                        } else {
                            errorMsg = "कृपया कम्तिमा १० अंकको मोबाइल नम्बर हाल्नुहोस्।"
                        }
                    } else {
                        val fullPhone = if (phone.startsWith("+977")) phone else "+977-$phone"
                        viewModel.loginOrRegister(fullPhone, name) {
                            onSuccess()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = HamroCrimson)
            ) {
                Text(if (step == 1) "OTP पठाउनुहोस्" else "लगइन गर्नुहोस्")
            }
        },
        dismissButton = {
            if (step == 2) {
                TextButton(onClick = { step = 1 }) {
                    Text("पछाडि फर्कनुहोस्")
                }
            }
        }
    )
}
