package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entities.*
import com.example.data.repository.HamroRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HamroViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    val repository = HamroRepository(database)

    val currentUserId = repository.currentUserId
    val selectedLocation = repository.selectedLocation
    val currentLanguage = repository.currentLanguage

    val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentUser: StateFlow<UserEntity?> = currentUserId.flatMapLatest { uid ->
        repository.getCurrentUser(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val products: StateFlow<List<ProductEntity>> = repository.getApprovedProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProductsAdmin: StateFlow<List<ProductEntity>> = repository.getAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val featuredProducts: StateFlow<List<ProductEntity>> = repository.getFeaturedProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rentals: StateFlow<List<RentalEntity>> = repository.getApprovedRentals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRentalsAdmin: StateFlow<List<RentalEntity>> = repository.getAllRentals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val featuredRentals: StateFlow<List<RentalEntity>> = repository.getFeaturedRentals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val serviceProviders: StateFlow<List<ServiceProviderEntity>> = repository.getApprovedProviders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProvidersAdmin: StateFlow<List<ServiceProviderEntity>> = repository.getAllProviders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userNotifications: StateFlow<List<NotificationEntity>> = currentUserId.flatMapLatest { uid ->
        repository.getNotificationsForUser(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userFavorites: StateFlow<List<FavoriteEntity>> = currentUserId.flatMapLatest { uid ->
        repository.getFavoritesForUser(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userOrders: StateFlow<List<OrderEntity>> = currentUserId.flatMapLatest { uid ->
        repository.getOrdersForBuyer(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userSellerOrders: StateFlow<List<OrderEntity>> = currentUserId.flatMapLatest { uid ->
        repository.getOrdersForSeller(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userBookings: StateFlow<List<ServiceBookingEntity>> = currentUserId.flatMapLatest { uid ->
        repository.getBookingsForCustomer(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val providerBookings: StateFlow<List<ServiceBookingEntity>> = currentUserId.flatMapLatest { uid ->
        repository.getBookingsForProvider(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userRentalRequests: StateFlow<List<RentalRequestEntity>> = currentUserId.flatMapLatest { uid ->
        repository.getRequestsForTenant(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ownerRentalRequests: StateFlow<List<RentalRequestEntity>> = currentUserId.flatMapLatest { uid ->
        repository.getRequestsForOwner(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userMessages: StateFlow<List<MessageEntity>> = currentUserId.flatMapLatest { uid ->
        repository.getAllMessagesForUser(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminReports: StateFlow<List<ReportEntity>> = repository.getAllReports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search and Filtering State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow("All")
    val selectedCategoryFilter: StateFlow<String> = _selectedCategoryFilter.asStateFlow()

    private val _selectedSortBy = MutableStateFlow("Newest") // Newest, Price Low to High, Price High to Low, Rating
    val selectedSortBy: StateFlow<String> = _selectedSortBy.asStateFlow()

    private val _filterTab = MutableStateFlow("All") // All, Products, Rentals, Services
    val filterTab: StateFlow<String> = _filterTab.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initDatabaseIfNeeded()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(category: String) {
        _selectedCategoryFilter.value = category
    }

    fun setSortBy(sortBy: String) {
        _selectedSortBy.value = sortBy
    }

    fun setFilterTab(tab: String) {
        _filterTab.value = tab
    }

    fun setLanguage(lang: String) = repository.setLanguage(lang)
    fun setLocation(loc: String) = repository.setLocation(loc)
    fun switchUser(userId: Long) = repository.switchUser(userId)

    fun loginOrRegister(phone: String, name: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val existing = repository.getUserByPhone(phone)
            if (existing != null) {
                repository.switchUser(existing.id)
            } else {
                val newId = repository.insertUser(
                    UserEntity(
                        phone = phone,
                        name = name.ifBlank { "Hamro User" },
                        roles = "Customer,Seller,Property Owner,Service Provider",
                        activeRole = "Customer",
                        isVerified = true
                    )
                )
                repository.switchUser(newId)
            }
            onSuccess()
        }
    }

    fun updateActiveRole(newRole: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            repository.updateUser(user.copy(activeRole = newRole))
        }
    }

    fun updateProfile(name: String, email: String, province: String, district: String, municipality: String, ward: String, address: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            repository.updateUser(
                user.copy(
                    name = name,
                    email = email,
                    province = province,
                    district = district,
                    municipality = municipality,
                    ward = ward,
                    address = address
                )
            )
        }
    }

    // ACTIONS
    fun postProduct(
        title: String,
        category: String,
        priceNpr: Long,
        isNegotiable: Boolean,
        condition: String,
        description: String,
        deliveryAvailable: Boolean,
        isDraft: Boolean = false,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val product = ProductEntity(
                title = title,
                category = category,
                priceNpr = priceNpr,
                isNegotiable = isNegotiable,
                condition = condition,
                description = description,
                imageUrls = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500",
                province = user.province,
                district = user.district,
                municipality = user.municipality,
                ward = user.ward,
                deliveryAvailable = deliveryAvailable,
                sellerId = user.id,
                sellerName = user.name,
                sellerPhone = user.phone,
                sellerRating = 4.8f,
                isDraft = isDraft,
                isApproved = true // Auto-approved for fast local testing
            )
            repository.insertProduct(product)
            onSuccess()
        }
    }

    fun postRental(
        title: String,
        propertyType: String,
        monthlyRentNpr: Long,
        securityDepositNpr: Long,
        rooms: Int,
        bathrooms: Int,
        floor: String,
        areaSqFt: String,
        furnishedStatus: String,
        hasWater: Boolean,
        hasElectricity: Boolean,
        hasParking: Boolean,
        hasInternet: Boolean,
        description: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val rental = RentalEntity(
                title = title,
                propertyType = propertyType,
                monthlyRentNpr = monthlyRentNpr,
                securityDepositNpr = securityDepositNpr,
                rooms = rooms,
                bathrooms = bathrooms,
                floor = floor,
                areaSqFt = areaSqFt,
                furnishedStatus = furnishedStatus,
                hasWater = hasWater,
                hasElectricity = hasElectricity,
                hasParking = hasParking,
                hasInternet = hasInternet,
                description = description,
                imageUrls = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=500",
                province = user.province,
                district = user.district,
                municipality = user.municipality,
                ward = user.ward,
                ownerId = user.id,
                ownerName = user.name,
                ownerPhone = user.phone,
                isApproved = true
            )
            repository.insertRental(rental)
            onSuccess()
        }
    }

    fun offerService(
        serviceCategory: String,
        skills: String,
        experienceYears: Int,
        serviceArea: String,
        startingPriceNpr: Long,
        pricingType: String,
        availableDays: String,
        availableHours: String,
        description: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val provider = ServiceProviderEntity(
                userId = user.id,
                name = user.name,
                serviceCategory = serviceCategory,
                skills = skills,
                experienceYears = experienceYears,
                serviceArea = serviceArea,
                startingPriceNpr = startingPriceNpr,
                pricingType = pricingType,
                availableDays = availableDays,
                availableHours = availableHours,
                description = description,
                isVerified = user.isVerified,
                rating = 5.0f,
                completedJobs = 1,
                phone = user.phone,
                isApproved = true
            )
            repository.insertProvider(provider)
            onSuccess()
        }
    }

    fun placeOrder(
        product: ProductEntity,
        quantity: Int,
        deliveryAddress: String,
        paymentMethod: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val order = OrderEntity(
                productId = product.id,
                productTitle = product.title,
                productPriceNpr = product.priceNpr,
                quantity = quantity,
                totalPriceNpr = product.priceNpr * quantity,
                buyerId = user.id,
                buyerName = user.name,
                buyerPhone = user.phone,
                sellerId = product.sellerId,
                sellerName = product.sellerName,
                deliveryAddress = deliveryAddress,
                paymentMethod = paymentMethod,
                status = "Confirmed"
            )
            repository.insertOrder(order)
            onSuccess()
        }
    }

    fun bookService(
        provider: ServiceProviderEntity,
        date: String,
        time: String,
        address: String,
        description: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val booking = ServiceBookingEntity(
                serviceId = provider.id,
                providerId = provider.userId,
                providerName = provider.name,
                serviceName = provider.serviceCategory,
                customerId = user.id,
                customerName = user.name,
                customerPhone = user.phone,
                bookingDate = date,
                bookingTime = time,
                address = address,
                workDescription = description,
                status = "Pending",
                estimatedPriceNpr = provider.startingPriceNpr
            )
            repository.insertBooking(booking)
            onSuccess()
        }
    }

    fun sendRentalRequest(
        rental: RentalEntity,
        moveInDate: String,
        note: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val request = RentalRequestEntity(
                rentalId = rental.id,
                rentalTitle = rental.title,
                ownerId = rental.ownerId,
                tenantId = user.id,
                tenantName = user.name,
                tenantPhone = user.phone,
                moveInDate = moveInDate,
                note = note,
                status = "Pending"
            )
            repository.insertRentalRequest(request)
            onSuccess()
        }
    }

    fun sendMessage(
        receiverId: Long,
        receiverName: String,
        text: String,
        itemType: String = "None",
        itemId: Long = 0,
        itemTitle: String = ""
    ) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val convId = if (user.id < receiverId) "${user.id}_$receiverId" else "${receiverId}_${user.id}"
            val msg = MessageEntity(
                conversationId = convId,
                senderId = user.id,
                senderName = user.name,
                receiverId = receiverId,
                receiverName = receiverName,
                text = text,
                itemType = itemType,
                itemId = itemId,
                itemTitle = itemTitle
            )
            repository.sendMessage(msg)
        }
    }

    fun toggleFavorite(type: String, itemId: Long, currentlyFav: Boolean) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            repository.toggleFavorite(user.id, type, itemId, currentlyFav)
        }
    }

    fun submitReview(targetType: String, targetId: Long, rating: Int, comment: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val review = ReviewEntity(
                targetType = targetType,
                targetId = targetId,
                reviewerId = user.id,
                reviewerName = user.name,
                rating = rating,
                comment = comment
            )
            repository.insertReview(review)
            onSuccess()
        }
    }

    fun submitReport(itemType: String, itemId: Long, reason: String, details: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val report = ReportEntity(
                reporterId = user.id,
                reporterName = user.name,
                itemType = itemType,
                itemId = itemId,
                reason = reason,
                details = details
            )
            repository.insertReport(report)
            onSuccess()
        }
    }

    // ADMIN ACTIONS
    fun adminApproveProduct(id: Long, approve: Boolean) {
        viewModelScope.launch { repository.updateProductApproval(id, approve) }
    }

    fun adminFeatureProduct(id: Long, feature: Boolean) {
        viewModelScope.launch { repository.updateProductFeatured(id, feature) }
    }

    fun adminDeleteProduct(id: Long) {
        viewModelScope.launch { repository.deleteProduct(id) }
    }

    fun adminApproveRental(id: Long, approve: Boolean) {
        viewModelScope.launch { repository.updateRentalApproval(id, approve) }
    }

    fun adminFeatureRental(id: Long, feature: Boolean) {
        viewModelScope.launch { repository.updateRentalFeatured(id, feature) }
    }

    fun adminDeleteRental(id: Long) {
        viewModelScope.launch { repository.deleteRental(id) }
    }

    fun adminVerifyUser(userId: Long, verify: Boolean) {
        viewModelScope.launch { repository.updateUserVerification(userId, verify) }
    }

    fun adminBlockUser(userId: Long, block: Boolean) {
        viewModelScope.launch { repository.updateUserBlocked(userId, block) }
    }

    fun adminVerifyProvider(id: Long, verify: Boolean) {
        viewModelScope.launch { repository.updateProviderVerification(id, verify) }
    }

    fun adminResolveReport(reportId: Long, status: String) {
        viewModelScope.launch { repository.updateReportStatus(reportId, status) }
    }

    fun updateOrderStatus(orderId: Long, status: String, buyerId: Long, title: String) {
        viewModelScope.launch { repository.updateOrderStatus(orderId, status, buyerId, title) }
    }

    fun updateBookingStatus(bookingId: Long, status: String, customerId: Long, serviceName: String) {
        viewModelScope.launch { repository.updateBookingStatus(bookingId, status, customerId, serviceName) }
    }

    fun updateRentalRequestStatus(reqId: Long, status: String, tenantId: Long, title: String) {
        viewModelScope.launch { repository.updateRentalRequestStatus(reqId, status, tenantId, title) }
    }

    fun markNotificationAsRead(id: Long) {
        viewModelScope.launch { repository.markNotificationAsRead(id) }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            val uid = currentUserId.value
            repository.markAllNotificationsAsRead(uid)
        }
    }
}
