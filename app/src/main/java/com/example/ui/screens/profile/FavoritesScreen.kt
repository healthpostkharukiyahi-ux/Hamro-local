package com.example.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
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
import com.example.ui.components.ProductCard
import com.example.ui.components.RentalCard
import com.example.ui.theme.SlateTextMedium

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: HamroViewModel,
    onBack: () -> Unit,
    onProductClick: (Long) -> Unit,
    onRentalClick: (Long) -> Unit
) {
    val favorites by viewModel.userFavorites.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val rentals by viewModel.rentals.collectAsStateWithLifecycle()
    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()

    val favProducts = remember(favorites, products) {
        val prodIds = favorites.filter { it.itemType == "Product" }.map { it.itemId }
        products.filter { it.id in prodIds }
    }

    val favRentals = remember(favorites, rentals) {
        val rentIds = favorites.filter { it.itemType == "Rental" }.map { it.itemId }
        rentals.filter { it.id in rentIds }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (currentLang == "ne") "मन परेका सामग्री (Favorites)" else "Favorites", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (favProducts.isEmpty() && favRentals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .testTag("empty_favorites"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = null,
                        modifier = Modifier.size(54.dp),
                        tint = SlateTextMedium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (currentLang == "ne") "कुनै पनि सामग्री सुरक्षित गरिएको छैन।" else "No favorites saved.",
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
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (favProducts.isNotEmpty()) {
                    item {
                        Text(
                            text = if (currentLang == "ne") "सामानहरू (${favProducts.size})" else "Products (${favProducts.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                    items(favProducts, key = { "fav_p_${it.id}" }) { prod ->
                        ProductCard(
                            product = prod,
                            onClick = { onProductClick(prod.id) },
                            onFavoriteToggle = { viewModel.toggleFavorite("Product", prod.id, true) },
                            isFavorite = true,
                            currentLang = currentLang
                        )
                    }
                }

                if (favRentals.isNotEmpty()) {
                    item {
                        Text(
                            text = if (currentLang == "ne") "कोठा/घर भाडा (${favRentals.size})" else "Rentals (${favRentals.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(top = 16.dp, bottom = 6.dp)
                        )
                    }
                    items(favRentals, key = { "fav_r_${it.id}" }) { rent ->
                        RentalCard(
                            rental = rent,
                            onClick = { onRentalClick(rent.id) },
                            onFavoriteToggle = { viewModel.toggleFavorite("Rental", rent.id, true) },
                            isFavorite = true,
                            currentLang = currentLang
                        )
                    }
                }
            }
        }
    }
}
