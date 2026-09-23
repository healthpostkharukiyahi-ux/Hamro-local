package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ui.HamroViewModel
import com.example.ui.components.*
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.auth.OtpAuthDialog
import com.example.ui.screens.chat.ChatConversationScreen
import com.example.ui.screens.chat.ChatListScreen
import com.example.ui.screens.details.ProductDetailScreen
import com.example.ui.screens.details.RentalDetailScreen
import com.example.ui.screens.details.ServiceProviderDetailScreen
import com.example.ui.screens.explore.ExploreSearchScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.orders.OrdersBookingsScreen
import com.example.ui.screens.post.*
import com.example.ui.screens.profile.*
import com.example.ui.theme.HamroLocalTheme

class MainActivity : ComponentActivity() {
    private val viewModel: HamroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HamroLocalTheme {
                HamroApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun HamroApp(viewModel: HamroViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    val currentLocation by viewModel.selectedLocation.collectAsStateWithLifecycle()
    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val notifications by viewModel.userNotifications.collectAsStateWithLifecycle()
    val messages by viewModel.userMessages.collectAsStateWithLifecycle()
    val unreadNotifications = remember(notifications) { notifications.count { !it.isRead } }

    var showLocationSheet by remember { mutableStateOf(false) }
    var showAuthDialog by remember { mutableStateOf(false) }

    val bottomBarRoutes = listOf("home", "explore", "post", "messages", "profile")
    val shouldShowBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        topBar = {
            if (currentRoute == "home") {
                HamroTopBar(
                    currentLocation = currentLocation,
                    currentLang = currentLang,
                    unreadNotificationCount = unreadNotifications,
                    onLocationClick = { showLocationSheet = true },
                    onLanguageToggle = {
                        viewModel.setLanguage(if (currentLang == "ne") "en" else "ne")
                    },
                    onNotificationClick = {
                        navController.navigate("notifications")
                    },
                    onSearchClick = {
                        navController.navigate("explore")
                    }
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = shouldShowBottomBar,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                HamroBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        if (currentRoute != route) {
                            navController.navigate(route) {
                                popUpTo("home") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    currentLang = currentLang,
                    unreadMessages = 1
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToCategory = { catId ->
                        viewModel.setFilterTab(
                            when (catId) {
                                "marketplace", "vehicles", "agriculture" -> "Products"
                                "rent" -> "Rentals"
                                "services" -> "Services"
                                else -> "All"
                            }
                        )
                        navController.navigate("explore")
                    },
                    onProductClick = { id ->
                        navController.navigate("product_detail/$id")
                    },
                    onRentalClick = { id ->
                        navController.navigate("rental_detail/$id")
                    },
                    onProviderClick = { id ->
                        navController.navigate("service_detail/$id")
                    },
                    onSearchClick = {
                        navController.navigate("explore")
                    },
                    onSeeAllProducts = {
                        viewModel.setFilterTab("Products")
                        navController.navigate("explore")
                    },
                    onSeeAllRentals = {
                        viewModel.setFilterTab("Rentals")
                        navController.navigate("explore")
                    },
                    onSeeAllServices = {
                        viewModel.setFilterTab("Services")
                        navController.navigate("explore")
                    }
                )
            }

            composable("explore") {
                ExploreSearchScreen(
                    viewModel = viewModel,
                    onProductClick = { id -> navController.navigate("product_detail/$id") },
                    onRentalClick = { id -> navController.navigate("rental_detail/$id") },
                    onProviderClick = { id -> navController.navigate("service_detail/$id") }
                )
            }

            composable("post") {
                PostSelectionScreen(
                    onSelectPostType = { type ->
                        when (type) {
                            "product" -> navController.navigate("post_product")
                            "rental" -> navController.navigate("post_rental")
                            "service" -> navController.navigate("post_service")
                        }
                    }
                )
            }

            composable("post_product") {
                PostProductScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onSuccess = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }

            composable("post_rental") {
                PostRentalScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onSuccess = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }

            composable("post_service") {
                PostServiceScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onSuccess = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }

            composable("messages") {
                ChatListScreen(
                    viewModel = viewModel,
                    onOpenConversation = { receiverId, receiverName, itemId, itemTitle ->
                        navController.navigate("chat/$receiverId/$receiverName?itemId=$itemId&itemTitle=$itemTitle")
                    }
                )
            }

            composable(
                route = "chat/{receiverId}/{receiverName}?itemId={itemId}&itemTitle={itemTitle}",
                arguments = listOf(
                    navArgument("receiverId") { type = NavType.LongType },
                    navArgument("receiverName") { type = NavType.StringType },
                    navArgument("itemId") { type = NavType.LongType; defaultValue = 0L },
                    navArgument("itemTitle") { type = NavType.StringType; defaultValue = "" }
                )
            ) { backStackEntry ->
                val receiverId = backStackEntry.arguments?.getLong("receiverId") ?: 1L
                val receiverName = backStackEntry.arguments?.getString("receiverName") ?: "User"
                val itemId = backStackEntry.arguments?.getLong("itemId") ?: 0L
                val itemTitle = backStackEntry.arguments?.getString("itemTitle") ?: ""

                ChatConversationScreen(
                    receiverId = receiverId,
                    receiverName = receiverName,
                    itemId = itemId,
                    itemTitle = itemTitle,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("profile") {
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToEditProfile = { navController.navigate("edit_profile") },
                    onNavigateToOrders = { navController.navigate("orders") },
                    onNavigateToFavorites = { navController.navigate("favorites") },
                    onNavigateToNotifications = { navController.navigate("notifications") },
                    onNavigateToAdmin = { navController.navigate("admin") },
                    onOpenAuthModal = { showAuthDialog = true }
                )
            }

            composable("edit_profile") {
                EditProfileScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("orders") {
                OrdersBookingsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("favorites") {
                FavoritesScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onProductClick = { id -> navController.navigate("product_detail/$id") },
                    onRentalClick = { id -> navController.navigate("rental_detail/$id") }
                )
            }

            composable("notifications") {
                NotificationScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("admin") {
                AdminDashboardScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = "product_detail/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.LongType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
                ProductDetailScreen(
                    productId = productId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onOpenChat = { receiverId, receiverName, itemId, itemTitle ->
                        navController.navigate("chat/$receiverId/$receiverName?itemId=$itemId&itemTitle=$itemTitle")
                    }
                )
            }

            composable(
                route = "rental_detail/{rentalId}",
                arguments = listOf(navArgument("rentalId") { type = NavType.LongType })
            ) { backStackEntry ->
                val rentalId = backStackEntry.arguments?.getLong("rentalId") ?: 0L
                RentalDetailScreen(
                    rentalId = rentalId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onOpenChat = { receiverId, receiverName, itemId, itemTitle ->
                        navController.navigate("chat/$receiverId/$receiverName?itemId=$itemId&itemTitle=$itemTitle")
                    }
                )
            }

            composable(
                route = "service_detail/{providerId}",
                arguments = listOf(navArgument("providerId") { type = NavType.LongType })
            ) { backStackEntry ->
                val providerId = backStackEntry.arguments?.getLong("providerId") ?: 0L
                ServiceProviderDetailScreen(
                    providerId = providerId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onOpenChat = { receiverId, receiverName, itemId, itemTitle ->
                        navController.navigate("chat/$receiverId/$receiverName?itemId=$itemId&itemTitle=$itemTitle")
                    }
                )
            }
        }
    }

    if (showLocationSheet) {
        LocationPickerSheet(
            currentLocation = currentLocation,
            onLocationSelected = { newLoc ->
                viewModel.setLocation(newLoc)
            },
            onDismiss = { showLocationSheet = false }
        )
    }

    if (showAuthDialog) {
        OtpAuthDialog(
            viewModel = viewModel,
            onDismiss = { showAuthDialog = false },
            onSuccess = { showAuthDialog = false }
        )
    }
}
