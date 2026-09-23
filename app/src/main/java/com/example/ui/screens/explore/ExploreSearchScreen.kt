package com.example.ui.screens.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.ServiceProviderEntity
import com.example.ui.HamroViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreSearchScreen(
    viewModel: HamroViewModel,
    onProductClick: (Long) -> Unit,
    onRentalClick: (Long) -> Unit,
    onProviderClick: (Long) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filterTab by viewModel.filterTab.collectAsStateWithLifecycle()
    val sortBy by viewModel.selectedSortBy.collectAsStateWithLifecycle()
    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()

    val products by viewModel.products.collectAsStateWithLifecycle()
    val rentals by viewModel.rentals.collectAsStateWithLifecycle()
    val providers by viewModel.serviceProviders.collectAsStateWithLifecycle()
    val favorites by viewModel.userFavorites.collectAsStateWithLifecycle()

    var bookingProvider by remember { mutableStateOf<ServiceProviderEntity?>(null) }
    var showSortMenu by remember { mutableStateOf(false) }

    // Filtered lists
    val filteredProducts = remember(products, searchQuery, sortBy) {
        products.filter {
            searchQuery.isBlank() ||
                    it.title.contains(searchQuery, ignoreCase = true) ||
                    it.category.contains(searchQuery, ignoreCase = true) ||
                    it.district.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
        }.let { list ->
            when (sortBy) {
                "Lowest Price" -> list.sortedBy { it.priceNpr }
                "Highest Price" -> list.sortedByDescending { it.priceNpr }
                "Rating" -> list.sortedByDescending { it.sellerRating }
                else -> list.sortedByDescending { it.createdAt }
            }
        }
    }

    val filteredRentals = remember(rentals, searchQuery, sortBy) {
        rentals.filter {
            searchQuery.isBlank() ||
                    it.title.contains(searchQuery, ignoreCase = true) ||
                    it.propertyType.contains(searchQuery, ignoreCase = true) ||
                    it.district.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
        }.let { list ->
            when (sortBy) {
                "Lowest Price" -> list.sortedBy { it.monthlyRentNpr }
                "Highest Price" -> list.sortedByDescending { it.monthlyRentNpr }
                else -> list.sortedByDescending { it.createdAt }
            }
        }
    }

    val filteredProviders = remember(providers, searchQuery, sortBy) {
        providers.filter {
            searchQuery.isBlank() ||
                    it.name.contains(searchQuery, ignoreCase = true) ||
                    it.serviceCategory.contains(searchQuery, ignoreCase = true) ||
                    it.skills.contains(searchQuery, ignoreCase = true) ||
                    it.serviceArea.contains(searchQuery, ignoreCase = true)
        }.let { list ->
            when (sortBy) {
                "Rating" -> list.sortedByDescending { it.rating }
                "Lowest Price" -> list.sortedBy { it.startingPriceNpr }
                "Highest Price" -> list.sortedByDescending { it.startingPriceNpr }
                else -> list.sortedByDescending { it.completedJobs }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("explore_screen")
    ) {
        // Search Input
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text(if (currentLang == "ne") "सामान, कोठा वा सेवा खोज्नुहोस्..." else "Search products, rentals, services...") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("explore_search_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Filter Tabs: All, Products, Rentals, Services & Sort
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val tabs = listOf(
                        "All" to if (currentLang == "ne") "सबै" else "All",
                        "Products" to if (currentLang == "ne") "सामान (Products)" else "Products",
                        "Rentals" to if (currentLang == "ne") "भाडा (Rentals)" else "Rentals",
                        "Services" to if (currentLang == "ne") "सेवाहरू (Services)" else "Services"
                    )

                    tabs.forEach { (tabKey, label) ->
                        val isSelected = filterTab == tabKey
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setFilterTab(tabKey) },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = HamroCrimson,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .testTag("filter_tab_$tabKey")
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Sort By Menu Button
                    Box {
                        AssistChip(
                            onClick = { showSortMenu = true },
                            label = { Text(sortBy, fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Filled.Sort, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            listOf("Newest", "Lowest Price", "Highest Price", "Rating").forEach { sortOption ->
                                DropdownMenuItem(
                                    text = { Text(sortOption) },
                                    onClick = {
                                        viewModel.setSortBy(sortOption)
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Result lists
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Products Section
            if (filterTab == "All" || filterTab == "Products") {
                if (filteredProducts.isNotEmpty()) {
                    item {
                        Text(
                            text = if (currentLang == "ne") "सामानहरू (${filteredProducts.size})" else "Products (${filteredProducts.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(filteredProducts.chunked(2), key = { it.first().id }) { pair ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
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
            }

            // Rentals Section
            if (filterTab == "All" || filterTab == "Rentals") {
                if (filteredRentals.isNotEmpty()) {
                    item {
                        Text(
                            text = if (currentLang == "ne") "कोठा/घर भाडा (${filteredRentals.size})" else "Rentals (${filteredRentals.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                    }

                    items(filteredRentals.chunked(2), key = { it.first().id }) { pair ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            pair.forEach { rent ->
                                val isFav = favorites.any { it.itemType == "Rental" && it.itemId == rent.id }
                                RentalCard(
                                    rental = rent,
                                    onClick = { onRentalClick(rent.id) },
                                    onFavoriteToggle = { viewModel.toggleFavorite("Rental", rent.id, isFav) },
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
            }

            // Services Section
            if (filterTab == "All" || filterTab == "Services") {
                if (filteredProviders.isNotEmpty()) {
                    item {
                        Text(
                            text = if (currentLang == "ne") "सेवा प्रदायकहरू (${filteredProviders.size})" else "Service Providers (${filteredProviders.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                    }

                    items(filteredProviders, key = { it.id }) { prov ->
                        Box(modifier = Modifier.padding(vertical = 4.dp)) {
                            ServiceProviderCard(
                                provider = prov,
                                onClick = { onProviderClick(prov.id) },
                                onBookClick = { bookingProvider = prov },
                                currentLang = currentLang
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

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
