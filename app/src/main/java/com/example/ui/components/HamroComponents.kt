package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.RentalEntity
import com.example.data.local.entities.ServiceProviderEntity
import com.example.data.model.NepalLocations
import com.example.ui.theme.*
import com.example.ui.util.HamroStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HamroTopBar(
    currentLocation: String,
    currentLang: String,
    unreadNotificationCount: Int,
    onLocationClick: () -> Unit,
    onLanguageToggle: () -> Unit,
    onNotificationClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Logo & Tagline
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { }
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(HamroCrimson),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = "Hamro Local Logo",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "HAMRO",
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp,
                                color = HamroCrimson
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "LOCAL",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = HamroNavy
                            )
                        }
                        Text(
                            text = if (currentLang == "ne") "हाम्रो स्थानीय बजार" else "Nepal Local Hub",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Actions: Location Chip, Language Toggle, Notification Bell
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Location Chip
                    AssistChip(
                        onClick = onLocationClick,
                        label = {
                            Text(
                                text = currentLocation.split(" ").firstOrNull() ?: currentLocation,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Place,
                                contentDescription = "Location",
                                tint = HamroCrimson,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("location_chip")
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Language Toggle
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clickable { onLanguageToggle() }
                            .padding(4.dp)
                    ) {
                        Text(
                            text = if (currentLang == "ne") "नेपाली" else "EN",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = HamroCrimson,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Notification Icon with Badge
                    BadgedBox(
                        badge = {
                            if (unreadNotificationCount > 0) {
                                Badge(
                                    containerColor = HamroCrimson,
                                    contentColor = Color.White
                                ) {
                                    Text("$unreadNotificationCount")
                                }
                            }
                        }
                    ) {
                        IconButton(
                            onClick = onNotificationClick,
                            modifier = Modifier.testTag("notification_button")
                        ) {
                            Icon(
                                Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Search Bar Mock / Launcher
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSearchClick() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (currentLang == "ne") "सामान, कोठा भाडा वा सेवाहरू खोज्नुहोस्..." else "Search products, rentals, services...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun HamroBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    currentLang: String,
    unreadMessages: Int = 0
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { onNavigate("home") },
            icon = {
                Icon(
                    if (currentRoute == "home") Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text(HamroStrings.get("nav_home", currentLang), fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = HamroCrimson,
                indicatorColor = HamroCrimson.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_home")
        )

        NavigationBarItem(
            selected = currentRoute == "explore",
            onClick = { onNavigate("explore") },
            icon = {
                Icon(
                    if (currentRoute == "explore") Icons.Filled.Search else Icons.Outlined.Search,
                    contentDescription = "Explore"
                )
            },
            label = { Text(HamroStrings.get("nav_explore", currentLang), fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = HamroCrimson,
                indicatorColor = HamroCrimson.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_explore")
        )

        // Prominent Post Action
        NavigationBarItem(
            selected = currentRoute == "post",
            onClick = { onNavigate("post") },
            icon = {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(HamroCrimson),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Post Listing",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            label = { Text(HamroStrings.get("nav_post", currentLang), fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                indicatorColor = Color.Transparent
            ),
            modifier = Modifier.testTag("nav_post")
        )

        NavigationBarItem(
            selected = currentRoute == "messages",
            onClick = { onNavigate("messages") },
            icon = {
                BadgedBox(
                    badge = {
                        if (unreadMessages > 0) {
                            Badge(containerColor = HamroCrimson) {
                                Text("$unreadMessages")
                            }
                        }
                    }
                ) {
                    Icon(
                        if (currentRoute == "messages") Icons.Filled.Chat else Icons.Outlined.Chat,
                        contentDescription = "Messages"
                    )
                }
            },
            label = { Text(HamroStrings.get("nav_messages", currentLang), fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = HamroCrimson,
                indicatorColor = HamroCrimson.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_messages")
        )

        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = { onNavigate("profile") },
            icon = {
                Icon(
                    if (currentRoute == "profile") Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Profile"
                )
            },
            label = { Text(HamroStrings.get("nav_profile", currentLang), fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = HamroCrimson,
                indicatorColor = HamroCrimson.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_profile")
        )
    }
}

@Composable
fun ProductCard(
    product: ProductEntity,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    isFavorite: Boolean = false,
    currentLang: String = "ne",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                AsyncImage(
                    model = product.imageUrls.ifEmpty { "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500" },
                    contentDescription = product.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Top Condition Badge
                Surface(
                    shape = RoundedCornerShape(bottomEnd = 8.dp),
                    color = HamroNavy.copy(alpha = 0.85f),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = product.condition,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                // Favorite Icon
                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.85f))
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) HamroCrimson else SlateTextDark,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Negotiable Tag
                if (product.isNegotiable) {
                    Surface(
                        shape = RoundedCornerShape(topStart = 8.dp),
                        color = NepalGreen.copy(alpha = 0.9f),
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        Text(
                            text = if (currentLang == "ne") "मिल्ने" else "Nego",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = product.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = HamroStrings.formatNpr(product.priceNpr, currentLang),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = HamroCrimson
                )

                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Place,
                        contentDescription = null,
                        tint = SlateTextMedium,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${product.district}, Ward ${product.ward}",
                        fontSize = 11.sp,
                        color = SlateTextMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun RentalCard(
    rental: RentalEntity,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    isFavorite: Boolean = false,
    currentLang: String = "ne",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("rental_card_${rental.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                AsyncImage(
                    model = rental.imageUrls.ifEmpty { "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=500" },
                    contentDescription = rental.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Property Type Badge
                Surface(
                    shape = RoundedCornerShape(bottomEnd = 8.dp),
                    color = HamroNavy.copy(alpha = 0.9f),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = rental.propertyType,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                // Favorite Icon
                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.85f))
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) HamroCrimson else SlateTextDark,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Rental status
                Surface(
                    shape = RoundedCornerShape(topStart = 8.dp),
                    color = if (rental.status == "Available") NepalGreen else HamroAmber,
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Text(
                        text = rental.status,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = rental.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${HamroStrings.formatNpr(rental.monthlyRentNpr, currentLang)}/महिना",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = HamroCrimson
                    )
                    Text(
                        text = rental.furnishedStatus,
                        fontSize = 10.sp,
                        color = SlateTextMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Place,
                        contentDescription = null,
                        tint = SlateTextMedium,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${rental.district}, ${rental.floor}",
                        fontSize = 11.sp,
                        color = SlateTextMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun ServiceProviderCard(
    provider: ServiceProviderEntity,
    onClick: () -> Unit,
    onBookClick: () -> Unit,
    currentLang: String = "ne",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("service_card_${provider.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(HamroNavy.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Handyman,
                        contentDescription = null,
                        tint = HamroNavy,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = provider.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (provider.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Filled.Verified,
                                contentDescription = "Verified Provider",
                                tint = HamroCrimson,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = provider.serviceCategory,
                        color = HamroNavy,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = "Rating",
                            tint = HamroAmber,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${provider.rating} (${provider.completedJobs} काम सम्पन्न)",
                            fontSize = 11.sp,
                            color = SlateTextMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = provider.skills,
                fontSize = 11.sp,
                color = SlateTextMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (currentLang == "ne") "सुरुवाती शुल्क:" else "Starting:",
                        fontSize = 10.sp,
                        color = SlateTextMedium
                    )
                    Text(
                        text = "${HamroStrings.formatNpr(provider.startingPriceNpr, currentLang)} (${provider.pricingType})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = HamroCrimson
                    )
                }

                Row {
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${provider.phone}"))
                            context.startActivity(intent)
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Filled.Call, contentDescription = "Call", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (currentLang == "ne") "फोन" else "Call", fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Button(
                        onClick = onBookClick,
                        colors = ButtonDefaults.buttonColors(containerColor = HamroCrimson),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = "Book", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (currentLang == "ne") "बुक" else "Book", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
