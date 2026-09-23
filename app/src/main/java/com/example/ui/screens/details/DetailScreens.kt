package com.example.ui.screens.details

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.RentalEntity
import com.example.data.local.entities.ServiceProviderEntity
import com.example.ui.HamroViewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.util.HamroStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Long,
    viewModel: HamroViewModel,
    onBack: () -> Unit,
    onOpenChat: (receiverId: Long, receiverName: String, itemId: Long, itemTitle: String) -> Unit
) {
    val context = LocalContext.current
    val products by viewModel.allProductsAdmin.collectAsStateWithLifecycle()
    val product = products.find { it.id == productId }
    val favorites by viewModel.userFavorites.collectAsStateWithLifecycle()
    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()

    var showOrderDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf(false) }
    var orderSuccessSnackbar by remember { mutableStateOf(false) }

    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = HamroCrimson)
        }
        return
    }

    val isFav = favorites.any { it.itemType == "Product" && it.itemId == product.id }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product.title, maxLines = 1, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite("Product", product.id, isFav) }) {
                        Icon(
                            if (isFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFav) HamroCrimson else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { showReportDialog = true }) {
                        Icon(Icons.Filled.ReportProblem, contentDescription = "Report", tint = SlateTextMedium)
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${product.sellerPhone}"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Call, contentDescription = "Call", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (currentLang == "ne") "फोन" else "Call", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            onOpenChat(product.sellerId, product.sellerName, product.id, product.title)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Chat, contentDescription = "Chat", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (currentLang == "ne") "च्याट" else "Chat", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { showOrderDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = HamroCrimson),
                        modifier = Modifier
                            .weight(1.4f)
                            .testTag("buy_now_button")
                    ) {
                        Icon(Icons.Filled.ShoppingBag, contentDescription = "Buy", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (currentLang == "ne") "अर्डर गर्नुहोस्" else "Buy Now", fontSize = 12.sp)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .testTag("product_detail_scroll")
        ) {
            // Product Hero Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                AsyncImage(
                    model = product.imageUrls.ifEmpty { "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500" },
                    contentDescription = product.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Surface(
                    shape = RoundedCornerShape(bottomEnd = 8.dp),
                    color = HamroNavy,
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = product.condition,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                if (product.deliveryAvailable) {
                    Surface(
                        shape = RoundedCornerShape(topStart = 8.dp),
                        color = NepalGreen,
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        Text(
                            text = if (currentLang == "ne") "डेलिभरी उपलब्ध" else "Delivery Available",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Title and Price
                Text(
                    text = product.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = HamroStrings.formatNpr(product.priceNpr, currentLang),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = HamroCrimson
                    )

                    if (product.isNegotiable) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = NepalGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (currentLang == "ne") "मूल्य केही मिल्न सक्छ (Negotiable)" else "Negotiable",
                                color = NepalGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(14.dp))

                // Location & Category details
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Place, contentDescription = null, tint = HamroCrimson, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${product.municipality}, ${product.district} (वडा नं. ${product.ward})",
                        fontSize = 13.sp,
                        color = SlateTextDark
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Category, contentDescription = null, tint = HamroNavy, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Category: ${product.category}",
                        fontSize = 13.sp,
                        color = SlateTextMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    text = if (currentLang == "ne") "सामानको विवरण (Description)" else "Description",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = product.description,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Seller Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(HamroNavy),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Person, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = product.sellerName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    Icons.Filled.Verified,
                                    contentDescription = "Verified Seller",
                                    tint = HamroCrimson,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "फोन: ${product.sellerPhone}",
                                fontSize = 12.sp,
                                color = SlateTextMedium
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Star, contentDescription = null, tint = HamroAmber, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${product.sellerRating} विक्रेता रेटिङ",
                                    fontSize = 11.sp,
                                    color = SlateTextMedium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action to write a review
                OutlinedButton(
                    onClick = { showReviewDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.RateReview, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (currentLang == "ne") "समीक्षा तथा प्रतिक्रिया दिनुहोस्" else "Write a Review")
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    if (showOrderDialog) {
        OrderDialog(
            product = product,
            currentLang = currentLang,
            onDismiss = { showOrderDialog = false },
            onConfirmOrder = { quantity, address, payMethod ->
                viewModel.placeOrder(product, quantity, address, payMethod) {
                    showOrderDialog = false
                    orderSuccessSnackbar = true
                }
            }
        )
    }

    if (showReportDialog) {
        ReportDialog(
            itemTitle = product.title,
            onDismiss = { showReportDialog = false },
            onSubmitReport = { reason, details ->
                viewModel.submitReport("Product", product.id, reason, details) {
                    showReportDialog = false
                }
            }
        )
    }

    if (showReviewDialog) {
        ReviewDialog(
            targetName = product.title,
            onDismiss = { showReviewDialog = false },
            onSubmitReview = { rating, comment ->
                viewModel.submitReview("Product", product.id, rating, comment) {
                    showReviewDialog = false
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentalDetailScreen(
    rentalId: Long,
    viewModel: HamroViewModel,
    onBack: () -> Unit,
    onOpenChat: (receiverId: Long, receiverName: String, itemId: Long, itemTitle: String) -> Unit
) {
    val context = LocalContext.current
    val rentals by viewModel.allRentalsAdmin.collectAsStateWithLifecycle()
    val rental = rentals.find { it.id == rentalId }
    val favorites by viewModel.userFavorites.collectAsStateWithLifecycle()
    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()

    var showRequestDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf(false) }

    if (rental == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = HamroCrimson)
        }
        return
    }

    val isFav = favorites.any { it.itemType == "Rental" && it.itemId == rental.id }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(rental.title, maxLines = 1, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite("Rental", rental.id, isFav) }) {
                        Icon(
                            if (isFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFav) HamroCrimson else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { showReportDialog = true }) {
                        Icon(Icons.Filled.ReportProblem, contentDescription = "Report", tint = SlateTextMedium)
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${rental.ownerPhone}"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Call, contentDescription = "Call", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (currentLang == "ne") "फोन" else "Call", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            onOpenChat(rental.ownerId, rental.ownerName, rental.id, rental.title)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Chat, contentDescription = "Chat", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (currentLang == "ne") "च्याट" else "Chat", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { showRequestDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = HamroCrimson),
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("request_rental_button")
                    ) {
                        Icon(Icons.Filled.HomeWork, contentDescription = "Request", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (currentLang == "ne") "भाडा अनुरोध" else "Request", fontSize = 12.sp)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .testTag("rental_detail_scroll")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                AsyncImage(
                    model = rental.imageUrls.ifEmpty { "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=500" },
                    contentDescription = rental.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Surface(
                    shape = RoundedCornerShape(bottomEnd = 8.dp),
                    color = HamroNavy,
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = rental.propertyType,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(topStart = 8.dp),
                    color = if (rental.status == "Available") NepalGreen else HamroAmber,
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Text(
                        text = rental.status,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = rental.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${HamroStrings.formatNpr(rental.monthlyRentNpr, currentLang)}/महिना",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = HamroCrimson
                    )

                    if (rental.securityDepositNpr > 0) {
                        Text(
                            text = "धरौटी: ${HamroStrings.formatNpr(rental.securityDepositNpr, currentLang)}",
                            fontSize = 12.sp,
                            color = SlateTextMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(14.dp))

                // Location and stats grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SpecItem("प्रकार", rental.propertyType)
                    SpecItem("कोठा", "${rental.rooms} Rooms")
                    SpecItem("शौचालय", "${rental.bathrooms} Bath")
                    SpecItem("तल्ला", rental.floor)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SpecItem("फर्निसिङ", rental.furnishedStatus)
                    SpecItem("क्षेत्रफल", rental.areaSqFt)
                    SpecItem("उपलब्ध मिति", rental.availableDate)
                    SpecItem("वडा", "Ward ${rental.ward}")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Amenities
                Text(
                    text = if (currentLang == "ne") "उपलब्ध सुविधाहरू (Amenities)" else "Amenities",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AmenityChip("खानेपानी", rental.hasWater)
                    AmenityChip("विद्युत", rental.hasElectricity)
                    AmenityChip("पार्किङ", rental.hasParking)
                    AmenityChip("इन्टरनेट", rental.hasInternet)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    text = if (currentLang == "ne") "विस्तृत विवरण (Description)" else "Description",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = rental.description,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Owner Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(HamroCrimson),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Person, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "घरबेटी: ${rental.ownerName}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "सम्पर्क: ${rental.ownerPhone}",
                                fontSize = 12.sp,
                                color = SlateTextMedium
                            )
                            Text(
                                text = "${rental.municipality}, ${rental.district}",
                                fontSize = 11.sp,
                                color = SlateTextMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    if (showRequestDialog) {
        RentalRequestDialog(
            rental = rental,
            currentLang = currentLang,
            onDismiss = { showRequestDialog = false },
            onConfirmRequest = { moveIn, note ->
                viewModel.sendRentalRequest(rental, moveIn, note) {
                    showRequestDialog = false
                }
            }
        )
    }

    if (showReportDialog) {
        ReportDialog(
            itemTitle = rental.title,
            onDismiss = { showReportDialog = false },
            onSubmitReport = { reason, details ->
                viewModel.submitReport("Rental", rental.id, reason, details) {
                    showReportDialog = false
                }
            }
        )
    }
}

@Composable
fun SpecItem(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 11.sp, color = SlateTextMedium)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun AmenityChip(label: String, available: Boolean) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (available) NepalGreen.copy(alpha = 0.12f) else SlateTextMedium.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                if (available) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                contentDescription = null,
                tint = if (available) NepalGreen else SlateTextMedium,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = if (available) NepalGreen else SlateTextMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceProviderDetailScreen(
    providerId: Long,
    viewModel: HamroViewModel,
    onBack: () -> Unit,
    onOpenChat: (receiverId: Long, receiverName: String, itemId: Long, itemTitle: String) -> Unit
) {
    val context = LocalContext.current
    val providers by viewModel.allProvidersAdmin.collectAsStateWithLifecycle()
    val provider = providers.find { it.id == providerId }
    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()

    var showBookDialog by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }

    if (provider == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = HamroCrimson)
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(provider.name, maxLines = 1, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showReportDialog = true }) {
                        Icon(Icons.Filled.ReportProblem, contentDescription = "Report", tint = SlateTextMedium)
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${provider.phone}"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Call, contentDescription = "Call", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (currentLang == "ne") "फोन" else "Call", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            onOpenChat(provider.userId, provider.name, provider.id, provider.serviceCategory)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Chat, contentDescription = "Chat", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (currentLang == "ne") "च्याट" else "Chat", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { showBookDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = HamroCrimson),
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("book_service_btn")
                    ) {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = "Book", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (currentLang == "ne") "सेवा बुक गर्नुहोस्" else "Book Now", fontSize = 12.sp)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .testTag("provider_detail_scroll")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(HamroNavy.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Handyman,
                        contentDescription = null,
                        tint = HamroNavy,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = provider.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (provider.isVerified) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                Icons.Filled.Verified,
                                contentDescription = "Verified Provider",
                                tint = HamroCrimson,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Text(
                        text = provider.serviceCategory,
                        fontSize = 14.sp,
                        color = HamroNavy,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = HamroAmber, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${provider.rating} (${provider.completedJobs} काम सम्पन्न)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = HamroCrimson.copy(alpha = 0.08f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("सुरुवाती सेवा शुल्क", fontSize = 11.sp, color = SlateTextMedium)
                        Text(
                            text = HamroStrings.formatNpr(provider.startingPriceNpr, currentLang),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = HamroCrimson
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = HamroNavy
                    ) {
                        Text(
                            text = provider.pricingType,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("सीप तथा दक्षता (Skills & Capabilities)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = provider.skills, fontSize = 13.sp, lineHeight = 20.sp)

            Spacer(modifier = Modifier.height(14.dp))

            Text("कार्य अनुभव तथा क्षेत्र", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text("• अनुभव: ${provider.experienceYears} वर्ष", fontSize = 13.sp)
            Text("• सेवा क्षेत्र: ${provider.serviceArea}", fontSize = 13.sp)
            Text("• उपलब्ध दिनहरू: ${provider.availableDays}", fontSize = 13.sp)
            Text("• उपलब्ध समय: ${provider.availableHours}", fontSize = 13.sp)

            Spacer(modifier = Modifier.height(14.dp))

            Text("विवरण (About)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = provider.description, fontSize = 13.sp, lineHeight = 20.sp)

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = { showReviewDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.RateReview, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("सेवा प्रदायकलाई समीक्षा दिनुहोस्")
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    if (showBookDialog) {
        BookServiceDialog(
            provider = provider,
            currentLang = currentLang,
            onDismiss = { showBookDialog = false },
            onConfirmBooking = { date, time, address, desc ->
                viewModel.bookService(provider, date, time, address, desc) {
                    showBookDialog = false
                }
            }
        )
    }

    if (showReviewDialog) {
        ReviewDialog(
            targetName = provider.name,
            onDismiss = { showReviewDialog = false },
            onSubmitReview = { rating, comment ->
                viewModel.submitReview("Service", provider.id, rating, comment) {
                    showReviewDialog = false
                }
            }
        )
    }

    if (showReportDialog) {
        ReportDialog(
            itemTitle = provider.name,
            onDismiss = { showReportDialog = false },
            onSubmitReport = { reason, details ->
                viewModel.submitReport("Service", provider.id, reason, details) {
                    showReportDialog = false
                }
            }
        )
    }
}
