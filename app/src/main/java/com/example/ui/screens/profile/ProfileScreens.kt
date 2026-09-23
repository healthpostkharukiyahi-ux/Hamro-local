package com.example.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.UserEntity
import com.example.ui.HamroViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: HamroViewModel,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onOpenAuthModal: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()

    var showSwitchUserDialog by remember { mutableStateOf(false) }

    val user = currentUser

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (currentLang == "ne") "मेरो खाता (Profile)" else "My Profile", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateToEditProfile) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit Profile")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .testTag("profile_screen_scroll")
        ) {
            // Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(HamroCrimson),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (user?.name?.take(1) ?: "U").uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = user?.name ?: "Hamro User",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (user?.isVerified == true) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        Icons.Filled.Verified,
                                        contentDescription = "Verified",
                                        tint = HamroCrimson,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Text(
                                text = user?.phone ?: "+977-9800000000",
                                fontSize = 13.sp,
                                color = SlateTextMedium
                            )
                            Text(
                                text = "${user?.municipality ?: "Kathmandu"}, ${user?.district ?: "Kathmandu"}",
                                fontSize = 12.sp,
                                color = SlateTextMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(10.dp))

                    // Roles selection: Customer, Seller, Property Owner, Service Provider, Admin
                    Text(
                        text = if (currentLang == "ne") "सक्रिय भूमिका (Switch Active Role):" else "Active Role:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SlateTextMedium
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val roles = listOf("Customer", "Seller", "Property Owner", "Service Provider", "Admin")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        roles.forEach { role ->
                            val isSelected = user?.activeRole == role
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.updateActiveRole(role) },
                                label = { Text(role, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = HamroCrimson,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Menu list
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    ProfileMenuItem(
                        icon = Icons.Filled.ShoppingBag,
                        title = if (currentLang == "ne") "अर्डर तथा सेवा बुकिङहरू (Orders & Bookings)" else "Orders & Bookings",
                        subtitle = "खरिद, बिक्री र सेवा बुकिङको स्थिति हेर्नुहोस्",
                        onClick = onNavigateToOrders,
                        testTag = "menu_orders"
                    )
                    HorizontalDivider(color = BorderLight.copy(alpha = 0.5f))

                    ProfileMenuItem(
                        icon = Icons.Filled.Favorite,
                        title = if (currentLang == "ne") "मन परेका सामग्री (Saved / Favorites)" else "Favorites",
                        subtitle = "तपाईंले सुरक्षित गर्नुभएका सामान र कोठाहरू",
                        onClick = onNavigateToFavorites,
                        testTag = "menu_favorites"
                    )
                    HorizontalDivider(color = BorderLight.copy(alpha = 0.5f))

                    ProfileMenuItem(
                        icon = Icons.Filled.Notifications,
                        title = if (currentLang == "ne") "सूचना तथा नोटिफिकेसन (Notifications)" else "Notifications",
                        subtitle = "अर्डर, च्याट र बुकिङ सम्बन्धी जानकारी",
                        onClick = onNavigateToNotifications,
                        testTag = "menu_notifications"
                    )
                    HorizontalDivider(color = BorderLight.copy(alpha = 0.5f))

                    // Admin Dashboard menu item (shown prominently if Admin role or accessible)
                    ProfileMenuItem(
                        icon = Icons.Filled.AdminPanelSettings,
                        title = "एडमिन ड्यासबोर्ड (Admin Moderation Panel)",
                        subtitle = "प्रयोगकर्ता, सामान र भाडा लिस्टिङ प्रमाणीकरण तथा रिपोर्ट व्यवस्थापन",
                        iconColor = HamroNavy,
                        onClick = onNavigateToAdmin,
                        testTag = "menu_admin"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Testing & Account Switcher
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    ProfileMenuItem(
                        icon = Icons.Filled.SupervisorAccount,
                        title = "परीक्षण खाता बदल्नुहोस् (Switch Demo User)",
                        subtitle = "Suresh (Buyer), Ramesh (Seller), Sita (Owner), Hari (Admin)",
                        iconColor = NepalGreen,
                        onClick = { showSwitchUserDialog = true },
                        testTag = "menu_switch_user"
                    )
                    HorizontalDivider(color = BorderLight.copy(alpha = 0.5f))

                    ProfileMenuItem(
                        icon = Icons.Filled.PhoneAndroid,
                        title = "नयाँ नम्बरबाट लगइन (OTP Login)",
                        subtitle = "+977 नेपाली मोबाइल नम्बर र OTP",
                        iconColor = HamroAmber,
                        onClick = onOpenAuthModal,
                        testTag = "menu_otp_login"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Version & About
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "HAMRO LOCAL - नेपालको आफ्नै डिजिटल बजार",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextDark
                )
                Text(
                    text = "Version 1.0.0 (Local Nepal Production Build)",
                    fontSize = 11.sp,
                    color = SlateTextMedium
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (showSwitchUserDialog) {
        AlertDialog(
            onDismissRequest = { showSwitchUserDialog = false },
            title = { Text("डेमो खाता छान्नुहोस् (Switch User)", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    allUsers.forEach { u ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.switchUser(u.id)
                                    showSwitchUserDialog = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(HamroCrimson.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(u.name.take(1), fontWeight = FontWeight.Bold, color = HamroCrimson)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(u.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("${u.phone} • ${u.activeRole}", fontSize = 11.sp, color = SlateTextMedium)
                            }
                            if (u.id == user?.id) {
                                Icon(Icons.Filled.Check, contentDescription = null, tint = HamroCrimson)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSwitchUserDialog = false }) { Text("बन्द गर्नुहोस्") }
            }
        )
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color = HamroCrimson,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(subtitle, fontSize = 11.sp, color = SlateTextMedium)
        }

        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = SlateTextMedium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: HamroViewModel,
    onBack: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val user = currentUser

    var name by remember(user) { mutableStateOf(user?.name ?: "") }
    var email by remember(user) { mutableStateOf(user?.email ?: "") }
    var province by remember(user) { mutableStateOf(user?.province ?: "Bagmati Province") }
    var district by remember(user) { mutableStateOf(user?.district ?: "Kathmandu") }
    var municipality by remember(user) { mutableStateOf(user?.municipality ?: "Kathmandu Metropolitan") }
    var ward by remember(user) { mutableStateOf(user?.ward ?: "10") }
    var address by remember(user) { mutableStateOf(user?.address ?: "Baneshwor") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("प्रोफाइल सम्पादन (Edit Profile)", fontWeight = FontWeight.Bold) },
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
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
                .testTag("edit_profile_form")
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("पूरा नाम (Full Name) *") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("इमेल (Email Address)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = province,
                onValueChange = { province = it },
                label = { Text("प्रदेश (Province)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = district,
                    onValueChange = { district = it },
                    label = { Text("जिल्ला (District)") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                OutlinedTextField(
                    value = ward,
                    onValueChange = { ward = it },
                    label = { Text("वडा नं. (Ward)") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = municipality,
                onValueChange = { municipality = it },
                label = { Text("नगरपालिका / गाउँपालिका (Municipality)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("टोल / ठेगाना (Tole / Street Address)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.updateProfile(name, email, province, district, municipality, ward, address)
                    onBack()
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = HamroCrimson),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_profile_button")
            ) {
                Text("सुरक्षित गर्नुहोस् (Save Changes)")
            }
        }
    }
}
