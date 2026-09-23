package com.example.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.RentalEntity
import com.example.data.local.entities.ServiceProviderEntity
import com.example.ui.HamroViewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.util.HamroStrings

data class CategoryItem(
    val id: String,
    val nameNe: String,
    val nameEn: String,
    val icon: ImageVector,
    val color: Color
)

val mainCategories = listOf(
    CategoryItem("marketplace", "बजार", "Marketplace", Icons.Default.ShoppingCart, HamroCrimson),
    CategoryItem("rent", "कोठा/घर भाडा", "Rentals", Icons.Default.HomeWork, HamroNavy),
    CategoryItem("services", "सेवा तथा कामदार", "Services", Icons.Default.Handyman, HamroAmber),
    CategoryItem("vehicles", "सवारी साधन", "Vehicles", Icons.Default.DirectionsCar, Color(0xFF00897B)),
    CategoryItem("agriculture", "कृषि उपज", "Agriculture", Icons.Default.Agriculture, NepalGreen),
    CategoryItem("other", "अन्य सामग्री", "Other", Icons.Default.Category, Color(0xFF6D4C41))
)

@Composable
fun HomeScreen(
    viewModel: HamroViewModel,
    onNavigateToCategory: (String) -> Unit,
    onProductClick: (Long) -> Unit,
    onRentalClick: (Long) -> Unit,
    onProviderClick: (Long) -> Unit,
    onSearchClick: () -> Unit,
    onSeeAllProducts: () -> Unit,
    onSeeAllRentals: () -> Unit,
    onSeeAllServices: () -> Unit
) {
    val products by viewModel.products.collectAsStateWithLifecycle()
    val featuredProducts by viewModel.featuredProducts.collectAsStateWithLifecycle()
    val rentals by viewModel.rentals.collectAsStateWithLifecycle()
    val featuredRentals by viewModel.featuredRentals.collectAsStateWithLifecycle()
    val providers by viewModel.serviceProviders.collectAsStateWithLifecycle()
    val favorites by viewModel.userFavorites.collectAsStateWithLifecycle()
    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()

    var bookingProvider by remember { mutableStateOf<ServiceProviderEntity?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_lazy_column"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Hero Visual Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .height(150.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.banner_hamro_nepal),
                    contentDescription = "Hamro Local Nepal Community Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Gradient Overlay & Tagline
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.38f))
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                ) {
                    Surface(
                        color = HamroCrimson,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "नेपालको आफ्नै स्थानीय बजार",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "किन्नुहोस् • बेच्नुहोस् • भाडामा दिनुहोस् • सेवा बुक गर्नुहोस्",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Main Categories Grid / Row
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = if (currentLang == "ne") "मुख्य विधाहरू (Categories)" else "Categories",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    mainCategories.forEach { cat ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { onNavigateToCategory(cat.id) }
                                .width(70.dp)
                                .testTag("cat_button_${cat.id}")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(cat.color.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = cat.icon,
                                    contentDescription = cat.nameEn,
                                    tint = cat.color,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (currentLang == "ne") cat.nameNe else cat.nameEn,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Featured Products
        if (featuredProducts.isNotEmpty()) {
            item {
                SectionHeader(
                    title = if (currentLang == "ne") "विशेष आकर्षण (Featured Products)" else "Featured Products",
                    onSeeAll = onSeeAllProducts,
                    currentLang = currentLang
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(featuredProducts, key = { it.id }) { product ->
                        val isFav = favorites.any { it.itemType == "Product" && it.itemId == product.id }
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product.id) },
                            onFavoriteToggle = { viewModel.toggleFavorite("Product", product.id, isFav) },
                            isFavorite = isFav,
                            currentLang = currentLang,
                            modifier = Modifier.width(180.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Houses and Rooms for Rent
        if (rentals.isNotEmpty()) {
            item {
                SectionHeader(
                    title = if (currentLang == "ne") "कोठा तथा घर भाडा (Houses / Rooms for Rent)" else "Houses / Rooms for Rent",
                    onSeeAll = onSeeAllRentals,
                    currentLang = currentLang
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(rentals.take(6), key = { it.id }) { rental ->
                        val isFav = favorites.any { it.itemType == "Rental" && it.itemId == rental.id }
                        RentalCard(
                            rental = rental,
                            onClick = { onRentalClick(rental.id) },
                            onFavoriteToggle = { viewModel.toggleFavorite("Rental", rental.id, isFav) },
                            isFavorite = isFav,
                            currentLang = currentLang,
                            modifier = Modifier.width(210.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Popular Service Providers
        if (providers.isNotEmpty()) {
            item {
                SectionHeader(
                    title = if (currentLang == "ne") "भरपर्दो स्थानीय कामदार तथा सेवाहरू (Services)" else "Local Service Providers",
                    onSeeAll = onSeeAllServices,
                    currentLang = currentLang
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(providers.take(4), key = { it.id }) { provider ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                    ServiceProviderCard(
                        provider = provider,
                        onClick = { onProviderClick(provider.id) },
                        onBookClick = { bookingProvider = provider },
                        currentLang = currentLang
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }

        // Latest Products
        item {
            SectionHeader(
                title = if (currentLang == "ne") "नयाँ सामानहरू (Latest Marketplace)" else "Latest Products",
                onSeeAll = onSeeAllProducts,
                currentLang = currentLang
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Display 2-column or list of latest products
        items(products.chunked(2)) { pair ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                pair.forEach { prod ->
                    val isFav = favorites.any { it.itemType == "Product" && it.itemId == prod.id }
                    ProductCard(
                        product = prod,
                        onClick = { onProductClick(prod.id) },
                        onFavoriteToggle = { viewModel.toggleFavorite("Product", prod.id, isFav) },
                        isFavorite = isFav,
                        currentLang = currentLang,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }

    // Book Service Modal Dialog if clicked
    bookingProvider?.let { prov ->
        BookServiceDialog(
            provider = prov,
            currentLang = currentLang,
            onDismiss = { bookingProvider = null },
            onConfirmBooking = { date, time, address, desc ->
                viewModel.bookService(prov, date, time, address, desc) {
                    bookingProvider = null
                }
            }
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    onSeeAll: () -> Unit,
    currentLang: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        TextButton(
            onClick = onSeeAll,
            contentPadding = PaddingValues(0.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (currentLang == "ne") "सबै हेर्नुहोस्" else "See All",
                    fontSize = 12.sp,
                    color = HamroCrimson,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = HamroCrimson,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
