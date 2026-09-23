package com.example.ui.screens.post

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
import com.example.ui.HamroViewModel
import com.example.ui.theme.*

@Composable
fun PostSelectionScreen(
    onSelectPostType: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
            .testTag("post_selection_screen")
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "तपाईं के पोस्ट गर्न चाहनुहुन्छ?",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "What would you like to post on Hamro Local?",
            fontSize = 13.sp,
            color = SlateTextMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        PostOptionCard(
            title = "१. सामान बेच्नुहोस् (Sell Product)",
            subtitle = "मोबाइल, ल्यापटप, गाडी, फर्निचर, लुगाफाटा वा कृषि उपज बेच्नुहोस्",
            icon = Icons.Filled.ShoppingCart,
            color = HamroCrimson,
            onClick = { onSelectPostType("product") },
            testTag = "post_option_product"
        )

        Spacer(modifier = Modifier.height(16.dp))

        PostOptionCard(
            title = "२. कोठा/घर भाडामा दिनुहोस् (Rent Property)",
            subtitle = "कोठा, फ्ल्याट, अपार्टमेन्ट, सटर, अफिस वा जग्गा भाडामा लगाउनुहोस्",
            icon = Icons.Filled.HomeWork,
            color = HamroNavy,
            onClick = { onSelectPostType("rental") },
            testTag = "post_option_rental"
        )

        Spacer(modifier = Modifier.height(16.dp))

        PostOptionCard(
            title = "३. आफ्नो सीप/सेवा दिनुहोस् (Offer Service)",
            subtitle = "इलेक्ट्रीसियन, प्लम्बर, सिकर्मी, रंगरोगन, ड्राइभर, ब्युटी वा रिपेयर सेवा",
            icon = Icons.Filled.Handyman,
            color = HamroAmber,
            onClick = { onSelectPostType("service") },
            testTag = "post_option_service"
        )
    }
}

@Composable
fun PostOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = SlateTextMedium,
                    lineHeight = 16.sp
                )
            }

            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = SlateTextMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostProductScreen(
    viewModel: HamroViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Mobile & Electronics") }
    var priceNpr by remember { mutableStateOf("") }
    var isNegotiable by remember { mutableStateOf(true) }
    var condition by remember { mutableStateOf("Used") }
    var description by remember { mutableStateOf("") }
    var deliveryAvailable by remember { mutableStateOf(true) }
    var showCategoryMenu by remember { mutableStateOf(false) }

    val categories = listOf(
        "Mobile & Electronics", "Computers", "Furniture", "Home Appliances",
        "Vehicles", "Clothes", "Agriculture", "Construction Materials",
        "Books", "Tools", "Kids Items", "Other"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("सामान बेच्नुहोस् (Sell Product)", fontWeight = FontWeight.Bold, fontSize = 17.sp) },
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
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .testTag("post_product_form")
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("सामानको नाम / शीर्षक (Product Title) *") },
                placeholder = { Text("जस्तै: iPhone 13 Pro 128GB Sierra Blue") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("product_title_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Picker
            Box {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("वर्ग / क्याटगोरी (Category) *") },
                    trailingIcon = {
                        IconButton(onClick = { showCategoryMenu = true }) {
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCategoryMenu = true }
                )
                DropdownMenu(
                    expanded = showCategoryMenu,
                    onDismissRequest = { showCategoryMenu = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                category = cat
                                showCategoryMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = priceNpr,
                onValueChange = { priceNpr = it.filter { char -> char.isDigit() } },
                label = { Text("मूल्य (Price in NPR) *") },
                placeholder = { Text("जस्तै: 45000") },
                leadingIcon = { Text("रू ", fontWeight = FontWeight.Bold, color = HamroCrimson) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("product_price_input")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Negotiable switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("मूल्य केही मिल्न सक्छ (Negotiable)?", fontSize = 13.sp)
                Switch(
                    checked = isNegotiable,
                    onCheckedChange = { isNegotiable = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = HamroCrimson)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Condition radio
            Text("सामानको अवस्था (Condition):", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                listOf("New", "Like New", "Used").forEach { cond ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { condition = cond }
                            .padding(end = 16.dp, top = 4.dp, bottom = 4.dp)
                    ) {
                        RadioButton(
                            selected = condition == cond,
                            onClick = { condition = cond },
                            colors = RadioButtonDefaults.colors(selectedColor = HamroCrimson)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(cond, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("सामानको विस्तृत विवरण (Description) *") },
                placeholder = { Text("सामानको विशेषता, वारेन्टी, कति समय चलाएको आदि लेख्नुहोस्...") },
                minLines = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("product_desc_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("डेलिभरी सुविधा उपलब्ध छ (Delivery Available)?", fontSize = 13.sp)
                Switch(
                    checked = deliveryAvailable,
                    onCheckedChange = { deliveryAvailable = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = HamroCrimson)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.postProduct(
                                title = title,
                                category = category,
                                priceNpr = priceNpr.toLongOrNull() ?: 1000,
                                isNegotiable = isNegotiable,
                                condition = condition,
                                description = description,
                                deliveryAvailable = deliveryAvailable,
                                isDraft = true,
                                onSuccess = onSuccess
                            )
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text("ड्राफ्ट राख्नुहोस् (Draft)")
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.postProduct(
                                title = title,
                                category = category,
                                priceNpr = priceNpr.toLongOrNull() ?: 1000,
                                isNegotiable = isNegotiable,
                                condition = condition,
                                description = description.ifBlank { "सम्पर्क गरेर थप जानकारी लिनुहोला।" },
                                deliveryAvailable = deliveryAvailable,
                                isDraft = false,
                                onSuccess = onSuccess
                            )
                        }
                    },
                    enabled = title.isNotBlank() && priceNpr.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = HamroCrimson),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("publish_product_button")
                ) {
                    Text("प्रकाशित गर्नुहोस् (Publish)")
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostRentalScreen(
    viewModel: HamroViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var propertyType by remember { mutableStateOf("Room") }
    var monthlyRent by remember { mutableStateOf("") }
    var securityDeposit by remember { mutableStateOf("") }
    var rooms by remember { mutableStateOf("1") }
    var bathrooms by remember { mutableStateOf("1") }
    var floor by remember { mutableStateOf("1st Floor") }
    var areaSqFt by remember { mutableStateOf("250 sq.ft") }
    var furnishedStatus by remember { mutableStateOf("Semi-Furnished") }
    var hasWater by remember { mutableStateOf(true) }
    var hasElectricity by remember { mutableStateOf(true) }
    var hasParking by remember { mutableStateOf(true) }
    var hasInternet by remember { mutableStateOf(true) }
    var description by remember { mutableStateOf("") }

    val propertyTypes = listOf("Room", "House", "Apartment", "Shop", "Office", "Land", "Warehouse", "Other")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("कोठा/घर भाडा लिस्टिङ (Rent Property)", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
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
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .testTag("post_rental_form")
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("शीर्षक (Rental Title) *") },
                placeholder = { Text("जस्तै: 2 BHK Flat with Dedicated Parking") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("rental_title_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("सम्पत्तिको प्रकार (Property Type):", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                propertyTypes.forEach { type ->
                    FilterChip(
                        selected = propertyType == type,
                        onClick = { propertyType = type },
                        label = { Text(type, fontSize = 12.sp) },
                        modifier = Modifier.padding(end = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = monthlyRent,
                    onValueChange = { monthlyRent = it.filter { c -> c.isDigit() } },
                    label = { Text("मासिक भाडा (NPR/mo) *") },
                    leadingIcon = { Text("रू ", fontWeight = FontWeight.Bold, color = HamroCrimson) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                OutlinedTextField(
                    value = securityDeposit,
                    onValueChange = { securityDeposit = it.filter { c -> c.isDigit() } },
                    label = { Text("धरौटी (Deposit)") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = rooms,
                    onValueChange = { rooms = it },
                    label = { Text("कोठा संख्या") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = bathrooms,
                    onValueChange = { bathrooms = it },
                    label = { Text("शौचालय") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = floor,
                    onValueChange = { floor = it },
                    label = { Text("तल्ला (Floor)") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("सुविधाहरू (Amenities):", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = hasWater, onCheckedChange = { hasWater = it })
                Text("खानेपानी (Water)", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Checkbox(checked = hasElectricity, onCheckedChange = { hasElectricity = it })
                Text("विद्युत (Electricity)", fontSize = 12.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = hasParking, onCheckedChange = { hasParking = it })
                Text("पार्किङ (Parking)", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(14.dp))
                Checkbox(checked = hasInternet, onCheckedChange = { hasInternet = it })
                Text("इन्टरनेट (Internet)", fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("विवरण तथा नियमहरू (Description)") },
                placeholder = { Text("पानीको तालिका, गाडी जाने बाटो, बत्तीको छुट्टै मिटर आदि...") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (title.isNotBlank() && monthlyRent.isNotBlank()) {
                        viewModel.postRental(
                            title = title,
                            propertyType = propertyType,
                            monthlyRentNpr = monthlyRent.toLongOrNull() ?: 10000,
                            securityDepositNpr = securityDeposit.toLongOrNull() ?: 0,
                            rooms = rooms.toIntOrNull() ?: 1,
                            bathrooms = bathrooms.toIntOrNull() ?: 1,
                            floor = floor,
                            areaSqFt = areaSqFt,
                            furnishedStatus = furnishedStatus,
                            hasWater = hasWater,
                            hasElectricity = hasElectricity,
                            hasParking = hasParking,
                            hasInternet = hasInternet,
                            description = description.ifBlank { "सम्पर्क गरेर हेर्न आउनुहोला।" },
                            onSuccess = onSuccess
                        )
                    }
                },
                enabled = title.isNotBlank() && monthlyRent.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = HamroCrimson),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("publish_rental_button")
            ) {
                Text("भाडा लिस्टिङ प्रकाशित गर्नुहोस् (Publish)")
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostServiceScreen(
    viewModel: HamroViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    var serviceCategory by remember { mutableStateOf("Electrician") }
    var skills by remember { mutableStateOf("") }
    var experienceYears by remember { mutableStateOf("5") }
    var serviceArea by remember { mutableStateOf("Kathmandu Valley") }
    var startingPriceNpr by remember { mutableStateOf("500") }
    var pricingType by remember { mutableStateOf("Per Job") }
    var availableDays by remember { mutableStateOf("Sun - Fri") }
    var availableHours by remember { mutableStateOf("8:00 AM - 7:00 PM") }
    var description by remember { mutableStateOf("") }

    val serviceCategories = listOf(
        "Electrician", "Plumber", "Carpenter", "Mason", "Painter",
        "Welder", "Mechanic", "Driver", "Cleaner", "Agriculture Worker",
        "Computer Repair", "Mobile Repair", "AC/Fridge Repair",
        "Beauty Service", "Photographer", "Tutor", "Other"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("सीप/सेवा दर्ता (Offer Service)", fontWeight = FontWeight.Bold, fontSize = 17.sp) },
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
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .testTag("post_service_form")
        ) {
            Text("सेवाको विधा (Service Category) *", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                serviceCategories.forEach { cat ->
                    FilterChip(
                        selected = serviceCategory == cat,
                        onClick = { serviceCategory = cat },
                        label = { Text(cat, fontSize = 12.sp) },
                        modifier = Modifier.padding(end = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = skills,
                onValueChange = { skills = it },
                label = { Text("मुख्य सीप तथा दक्षता (Skills) *") },
                placeholder = { Text("जस्तै: बत्ती जोड्ने, सर्ट सर्किट मर्मत, नयाँ वाइरिङ...") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = experienceYears,
                    onValueChange = { experienceYears = it },
                    label = { Text("अनुभव (वर्ष)") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                OutlinedTextField(
                    value = serviceArea,
                    onValueChange = { serviceArea = it },
                    label = { Text("कार्य क्षेत्र (Area)") },
                    modifier = Modifier.weight(1.5f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = startingPriceNpr,
                    onValueChange = { startingPriceNpr = it.filter { c -> c.isDigit() } },
                    label = { Text("सुरुवाती शुल्क (NPR)") },
                    leadingIcon = { Text("रू ", fontWeight = FontWeight.Bold, color = HamroCrimson) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                OutlinedTextField(
                    value = pricingType,
                    onValueChange = { pricingType = it },
                    label = { Text("शुल्क प्रकार (Hourly/Daily/Job)") },
                    modifier = Modifier.weight(1.2f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("आफ्नो सेवाबारे परिचय (About your service)") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (skills.isNotBlank()) {
                        viewModel.offerService(
                            serviceCategory = serviceCategory,
                            skills = skills,
                            experienceYears = experienceYears.toIntOrNull() ?: 3,
                            serviceArea = serviceArea,
                            startingPriceNpr = startingPriceNpr.toLongOrNull() ?: 500,
                            pricingType = pricingType,
                            availableDays = availableDays,
                            availableHours = availableHours,
                            description = description.ifBlank { "इमान्दारितापूर्वक राम्रो काम गरिदिनेछु।" },
                            onSuccess = onSuccess
                        )
                    }
                },
                enabled = skills.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = HamroCrimson),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("publish_service_button")
            ) {
                Text("सेवा प्रदायक दर्ता गर्नुहोस् (Register Service)")
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
