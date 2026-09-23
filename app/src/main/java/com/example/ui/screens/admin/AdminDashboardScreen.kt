package com.example.ui.screens.admin

import androidx.compose.foundation.background
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: HamroViewModel,
    onBack: () -> Unit
) {
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val allProducts by viewModel.allProductsAdmin.collectAsStateWithLifecycle()
    val allRentals by viewModel.allRentalsAdmin.collectAsStateWithLifecycle()
    val allProviders by viewModel.allProvidersAdmin.collectAsStateWithLifecycle()
    val allReports by viewModel.adminReports.collectAsStateWithLifecycle()
    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(0) } // 0: Overview, 1: Products, 2: Rentals, 3: Services, 4: Users, 5: Reports

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("हाम्रो एडमिन कन्ट्रोल (Admin Dashboard)", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
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
                .testTag("admin_dashboard_screen")
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 12.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = HamroCrimson
                    )
                }
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("ड्यासबोर्ड (Overview)") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("सामानहरू (${allProducts.size})") })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("कोठा/घर भाडा (${allRentals.size})") })
                Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("सेवा प्रदायक (${allProviders.size})") })
                Tab(selected = selectedTab == 4, onClick = { selectedTab = 4 }, text = { Text("प्रयोगकर्ता (${allUsers.size})") })
                Tab(selected = selectedTab == 5, onClick = { selectedTab = 5 }, text = { Text("उजुरी/रिपोर्ट (${allReports.size})") })
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        item {
                            Text("प्रणाली समग्र विवरण (System Analytics)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AdminStatCard("कुल प्रयोगकर्ता", "${allUsers.size}", HamroNavy, Modifier.weight(1f))
                                AdminStatCard("सामान लिस्टिङ", "${allProducts.size}", HamroCrimson, Modifier.weight(1f))
                            }
                        }
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AdminStatCard("कोठा/घर भाडा", "${allRentals.size}", Color(0xFF00796B), Modifier.weight(1f))
                                AdminStatCard("कामदार/सेवा", "${allProviders.size}", HamroAmber, Modifier.weight(1f))
                            }
                        }
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AdminStatCard("उजुरी/रिपोर्ट", "${allReports.size}", Color(0xFFC2185B), Modifier.weight(1f))
                                AdminStatCard("स्वीकृत दर", "98.5%", NepalGreen, Modifier.weight(1f))
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("एडमिन सूचना (Security Notice)", fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("तपाईं एडमिन मोडमा हुनुहुन्छ। गलत वा शंकास्पद लिस्टिङहरू तुरुन्त अस्वीकृत वा हटाउन सक्नुहुन्छ।", fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    1 -> {
                        // Product Moderation
                        items(allProducts, key = { "adm_p_${it.id}" }) { prod ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(prod.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("रू ${prod.priceNpr}", fontWeight = FontWeight.Bold, color = HamroCrimson)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("विक्रेता: ${prod.sellerName} (${prod.district})", fontSize = 12.sp, color = SlateTextMedium)
                                    Text("स्वीकृत स्थिति: ${if (prod.isApproved) "स्वीकृत (Approved)" else "अस्वीकृत (Pending)"}", fontSize = 11.sp)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = { viewModel.adminApproveProduct(prod.id, !prod.isApproved) },
                                            colors = ButtonDefaults.buttonColors(containerColor = if (prod.isApproved) HamroAmber else NepalGreen),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text(if (prod.isApproved) "रोक्नुहोस् (Reject)" else "स्वीकृत (Approve)", fontSize = 11.sp)
                                        }

                                        Button(
                                            onClick = { viewModel.adminFeatureProduct(prod.id, !prod.isFeatured) },
                                            colors = ButtonDefaults.buttonColors(containerColor = HamroNavy),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text(if (prod.isFeatured) "विशेष हटाउनुहोस्" else "विशेष बनाउनुहोस् (Feature)", fontSize = 11.sp)
                                        }

                                        OutlinedButton(
                                            onClick = { viewModel.adminDeleteProduct(prod.id) },
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text("हटाउनुहोस् (Delete)", fontSize = 11.sp, color = HamroCrimson)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // Rental Moderation
                        items(allRentals, key = { "adm_r_${it.id}" }) { rent ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(rent.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("रू ${rent.monthlyRentNpr}/mo", fontWeight = FontWeight.Bold, color = HamroCrimson)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("घरबेटी: ${rent.ownerName} (${rent.district})", fontSize = 12.sp, color = SlateTextMedium)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = { viewModel.adminApproveRental(rent.id, !rent.isApproved) },
                                            colors = ButtonDefaults.buttonColors(containerColor = if (rent.isApproved) HamroAmber else NepalGreen),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text(if (rent.isApproved) "रोक्नुहोस् (Reject)" else "स्वीकृत (Approve)", fontSize = 11.sp)
                                        }

                                        Button(
                                            onClick = { viewModel.adminFeatureRental(rent.id, !rent.isFeatured) },
                                            colors = ButtonDefaults.buttonColors(containerColor = HamroNavy),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text(if (rent.isFeatured) "विशेष हटाउनुहोस्" else "विशेष (Feature)", fontSize = 11.sp)
                                        }

                                        OutlinedButton(
                                            onClick = { viewModel.adminDeleteRental(rent.id) },
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text("हटाउनुहोस् (Delete)", fontSize = 11.sp, color = HamroCrimson)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    3 -> {
                        // Service Provider Moderation
                        items(allProviders, key = { "adm_s_${it.id}" }) { prov ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(prov.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(prov.serviceCategory, fontWeight = FontWeight.SemiBold, color = HamroNavy)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("फोन: ${prov.phone} | क्षेत्र: ${prov.serviceArea}", fontSize = 12.sp, color = SlateTextMedium)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = { viewModel.adminVerifyProvider(prov.id, !prov.isVerified) },
                                            colors = ButtonDefaults.buttonColors(containerColor = if (prov.isVerified) HamroAmber else NepalGreen),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text(if (prov.isVerified) "प्रमाणपत्र खारेज (Unverify)" else "प्रमाणित गर्नुहोस् (Verify)", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    4 -> {
                        // User Moderation
                        items(allUsers, key = { "adm_u_${it.id}" }) { usr ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(usr.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("भूमिका: ${usr.activeRole}", fontSize = 11.sp, color = HamroCrimson)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("फोन: ${usr.phone} | इमेल: ${usr.email}", fontSize = 12.sp, color = SlateTextMedium)
                                    Text("ठेगाना: ${usr.municipality}, ${usr.district}", fontSize = 11.sp, color = SlateTextMedium)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = { viewModel.adminVerifyUser(usr.id, !usr.isVerified) },
                                            colors = ButtonDefaults.buttonColors(containerColor = if (usr.isVerified) HamroAmber else NepalGreen),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text(if (usr.isVerified) "प्रमाणित खारेज" else "प्रमाणित गर्नुहोस्", fontSize = 11.sp)
                                        }

                                        Button(
                                            onClick = { viewModel.adminBlockUser(usr.id, !usr.isBlocked) },
                                            colors = ButtonDefaults.buttonColors(containerColor = if (usr.isBlocked) NepalGreen else HamroCrimson),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text(if (usr.isBlocked) "फुकुवा (Unblock)" else "प्रतिबन्ध (Block)", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    5 -> {
                        // Reports Moderation
                        if (allReports.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                                    Text("कुनै पनि उजुरी दर्ता भएको छैन।", color = SlateTextMedium)
                                }
                            }
                        }
                        items(allReports, key = { "adm_rep_${it.id}" }) { rep ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("कारण: ${rep.reason}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = HamroCrimson)
                                        Text(rep.status, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("उजुरीकर्ता: ${rep.reporterName} | प्रकार: ${rep.itemType} (ID: ${rep.itemId})", fontSize = 12.sp)
                                    Text("विवरण: ${rep.details}", fontSize = 12.sp, color = SlateTextDark)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = { viewModel.adminResolveReport(rep.id, "Resolved") },
                                            colors = ButtonDefaults.buttonColors(containerColor = NepalGreen),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text("समाधान गरियो (Resolve)", fontSize = 11.sp)
                                        }

                                        OutlinedButton(
                                            onClick = { viewModel.adminResolveReport(rep.id, "Dismissed") },
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text("खारेज (Dismiss)", fontSize = 11.sp)
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
fun AdminStatCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, fontSize = 11.sp, color = SlateTextMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
