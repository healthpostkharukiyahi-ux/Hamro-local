package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.DemoDataProvider
import com.example.data.local.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HamroRepository(private val db: AppDatabase) {
    private val dao = db.appDao()

    private val _currentUserId = MutableStateFlow<Long>(2) // Defaults to Suresh Shrestha (active user)
    val currentUserId: StateFlow<Long> = _currentUserId.asStateFlow()

    private val _selectedLocation = MutableStateFlow("काठमाडौं (Kathmandu)")
    val selectedLocation: StateFlow<String> = _selectedLocation.asStateFlow()

    private val _currentLanguage = MutableStateFlow("ne") // "ne" for Nepali, "en" for English
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    fun setLanguage(lang: String) {
        _currentLanguage.value = lang
    }

    fun setLocation(loc: String) {
        _selectedLocation.value = loc
    }

    fun switchUser(userId: Long) {
        _currentUserId.value = userId
    }

    suspend fun initDatabaseIfNeeded() = withContext(Dispatchers.IO) {
        val users = dao.getAllUsers().firstOrNull()
        if (users.isNullOrEmpty()) {
            dao.insertUsers(DemoDataProvider.getDemoUsers())
            dao.insertProducts(DemoDataProvider.getDemoProducts())
            dao.insertRentals(DemoDataProvider.getDemoRentals())
            dao.insertProviders(DemoDataProvider.getDemoServiceProviders())
            dao.insertOrders(DemoDataProvider.getDemoOrders())
            dao.insertNotifications(DemoDataProvider.getDemoNotifications())
            dao.insertReviews(DemoDataProvider.getDemoReviews())
            dao.insertMessages(DemoDataProvider.getDemoMessages())
            for (req in DemoDataProvider.getDemoRentalRequests()) {
                dao.insertRentalRequest(req)
            }
            for (booking in DemoDataProvider.getDemoBookings()) {
                dao.insertBooking(booking)
            }
        }
    }

    // USER & AUTH
    fun getCurrentUser(userId: Long): Flow<UserEntity?> = dao.getUserById(userId)
    fun getAllUsers(): Flow<List<UserEntity>> = dao.getAllUsers()
    suspend fun getUserByPhone(phone: String): UserEntity? = dao.getUserByPhoneDirect(phone)
    suspend fun insertUser(user: UserEntity): Long = dao.insertUser(user)
    suspend fun updateUser(user: UserEntity) = dao.updateUser(user)
    suspend fun updateUserVerification(userId: Long, isVerified: Boolean) = dao.updateUserVerification(userId, isVerified)
    suspend fun updateUserBlocked(userId: Long, isBlocked: Boolean) = dao.updateUserBlocked(userId, isBlocked)

    // PRODUCTS
    fun getAllProducts(): Flow<List<ProductEntity>> = dao.getAllProducts()
    fun getApprovedProducts(): Flow<List<ProductEntity>> = dao.getApprovedProducts()
    fun getFeaturedProducts(): Flow<List<ProductEntity>> = dao.getFeaturedProducts()
    fun getProductsByCategory(cat: String): Flow<List<ProductEntity>> = dao.getProductsByCategory(cat)
    fun getProductsBySeller(sellerId: Long): Flow<List<ProductEntity>> = dao.getProductsBySeller(sellerId)
    fun getProductById(id: Long): Flow<ProductEntity?> = dao.getProductById(id)
    suspend fun insertProduct(product: ProductEntity): Long = dao.insertProduct(product)
    suspend fun updateProduct(product: ProductEntity) = dao.updateProduct(product)
    suspend fun deleteProduct(id: Long) = dao.deleteProduct(id)
    suspend fun updateProductApproval(id: Long, approved: Boolean) = dao.updateProductApproval(id, approved)
    suspend fun updateProductFeatured(id: Long, featured: Boolean) = dao.updateProductFeatured(id, featured)

    // RENTALS
    fun getAllRentals(): Flow<List<RentalEntity>> = dao.getAllRentals()
    fun getApprovedRentals(): Flow<List<RentalEntity>> = dao.getApprovedRentals()
    fun getFeaturedRentals(): Flow<List<RentalEntity>> = dao.getFeaturedRentals()
    fun getRentalsByType(type: String): Flow<List<RentalEntity>> = dao.getRentalsByType(type)
    fun getRentalsByOwner(ownerId: Long): Flow<List<RentalEntity>> = dao.getRentalsByOwner(ownerId)
    fun getRentalById(id: Long): Flow<RentalEntity?> = dao.getRentalById(id)
    suspend fun insertRental(rental: RentalEntity): Long = dao.insertRental(rental)
    suspend fun updateRental(rental: RentalEntity) = dao.updateRental(rental)
    suspend fun deleteRental(id: Long) = dao.deleteRental(id)
    suspend fun updateRentalStatus(id: Long, status: String) = dao.updateRentalStatus(id, status)
    suspend fun updateRentalApproval(id: Long, approved: Boolean) = dao.updateRentalApproval(id, approved)
    suspend fun updateRentalFeatured(id: Long, featured: Boolean) = dao.updateRentalFeatured(id, featured)

    // SERVICE PROVIDERS
    fun getAllProviders(): Flow<List<ServiceProviderEntity>> = dao.getAllProviders()
    fun getApprovedProviders(): Flow<List<ServiceProviderEntity>> = dao.getApprovedProviders()
    fun getProvidersByCategory(cat: String): Flow<List<ServiceProviderEntity>> = dao.getProvidersByCategory(cat)
    fun getProviderById(id: Long): Flow<ServiceProviderEntity?> = dao.getProviderById(id)
    suspend fun insertProvider(provider: ServiceProviderEntity): Long = dao.insertProvider(provider)
    suspend fun updateProvider(provider: ServiceProviderEntity) = dao.updateProvider(provider)
    suspend fun updateProviderVerification(id: Long, verified: Boolean) = dao.updateProviderVerification(id, verified)
    suspend fun updateProviderApproval(id: Long, approved: Boolean) = dao.updateProviderApproval(id, approved)

    // BOOKINGS
    fun getBookingsForCustomer(customerId: Long): Flow<List<ServiceBookingEntity>> = dao.getBookingsForCustomer(customerId)
    fun getBookingsForProvider(providerId: Long): Flow<List<ServiceBookingEntity>> = dao.getBookingsForProvider(providerId)
    fun getAllBookings(): Flow<List<ServiceBookingEntity>> = dao.getAllBookings()
    suspend fun insertBooking(booking: ServiceBookingEntity): Long {
        val id = dao.insertBooking(booking)
        dao.insertNotification(
            NotificationEntity(
                userId = booking.providerId,
                title = "नयाँ सेवा बुकिङ अनुरोध!",
                message = "${booking.customerName} ले ${booking.serviceName} को लागि बुकिङ गर्नुभएको छ (${booking.bookingDate} ${booking.bookingTime})।",
                type = "Booking"
            )
        )
        return id
    }
    suspend fun updateBookingStatus(id: Long, status: String, customerId: Long, serviceName: String) {
        dao.updateBookingStatus(id, status)
        dao.insertNotification(
            NotificationEntity(
                userId = customerId,
                title = "बुकिङ स्थिति अपडेट: $status",
                message = "तपाईंको '$serviceName' बुकिङ स्थिति '$status' मा परिवर्तन भएको छ।",
                type = "Booking"
            )
        )
    }

    // ORDERS
    fun getOrdersForBuyer(buyerId: Long): Flow<List<OrderEntity>> = dao.getOrdersForBuyer(buyerId)
    fun getOrdersForSeller(sellerId: Long): Flow<List<OrderEntity>> = dao.getOrdersForSeller(sellerId)
    fun getAllOrders(): Flow<List<OrderEntity>> = dao.getAllOrders()
    suspend fun insertOrder(order: OrderEntity): Long {
        val id = dao.insertOrder(order)
        dao.insertNotification(
            NotificationEntity(
                userId = order.sellerId,
                title = "नयाँ अर्डर प्राप्त भयो!",
                message = "${order.buyerName} ले तपाईंको सामान '${order.productTitle}' को लागि ${order.paymentMethod} बाट अर्डर गर्नुभएको छ।",
                type = "Order"
            )
        )
        return id
    }
    suspend fun updateOrderStatus(id: Long, status: String, buyerId: Long, productTitle: String) {
        dao.updateOrderStatus(id, status)
        dao.insertNotification(
            NotificationEntity(
                userId = buyerId,
                title = "अर्डर स्थिति: $status",
                message = "तपाईंको अर्डर '$productTitle' को स्थिति '$status' भएको छ।",
                type = "Order"
            )
        )
    }

    // MESSAGES
    fun getConversationMessages(convId: String): Flow<List<MessageEntity>> = dao.getConversationMessages(convId)
    fun getAllMessagesForUser(userId: Long): Flow<List<MessageEntity>> = dao.getAllMessagesForUser(userId)
    suspend fun sendMessage(msg: MessageEntity): Long {
        val id = dao.insertMessage(msg)
        dao.insertNotification(
            NotificationEntity(
                userId = msg.receiverId,
                title = "नयाँ सन्देश (${msg.senderName})",
                message = msg.text.take(60),
                type = "Message"
            )
        )
        return id
    }
    suspend fun markMessagesAsRead(convId: String, receiverId: Long) = dao.markMessagesAsRead(convId, receiverId)

    // NOTIFICATIONS
    fun getNotificationsForUser(userId: Long): Flow<List<NotificationEntity>> = dao.getNotificationsForUser(userId)
    suspend fun markNotificationAsRead(id: Long) = dao.markNotificationAsRead(id)
    suspend fun markAllNotificationsAsRead(userId: Long) = dao.markAllNotificationsAsRead(userId)

    // FAVORITES
    fun getFavoritesForUser(userId: Long): Flow<List<FavoriteEntity>> = dao.getFavoritesForUser(userId)
    fun isFavorite(userId: Long, type: String, itemId: Long): Flow<Boolean> = dao.isFavorite(userId, type, itemId)
    suspend fun toggleFavorite(userId: Long, type: String, itemId: Long, isFav: Boolean) {
        if (isFav) {
            dao.deleteFavorite(userId, type, itemId)
        } else {
            dao.insertFavorite(FavoriteEntity(userId = userId, itemType = type, itemId = itemId))
        }
    }

    // REVIEWS
    fun getReviewsForTarget(type: String, targetId: Long): Flow<List<ReviewEntity>> = dao.getReviewsForTarget(type, targetId)
    suspend fun insertReview(review: ReviewEntity): Long = dao.insertReview(review)

    // REPORTS
    fun getAllReports(): Flow<List<ReportEntity>> = dao.getAllReports()
    suspend fun insertReport(report: ReportEntity): Long = dao.insertReport(report)
    suspend fun updateReportStatus(id: Long, status: String) = dao.updateReportStatus(id, status)

    // RENTAL REQUESTS
    fun getRequestsForTenant(tenantId: Long): Flow<List<RentalRequestEntity>> = dao.getRequestsForTenant(tenantId)
    fun getRequestsForOwner(ownerId: Long): Flow<List<RentalRequestEntity>> = dao.getRequestsForOwner(ownerId)
    fun getAllRentalRequests(): Flow<List<RentalRequestEntity>> = dao.getAllRentalRequests()
    suspend fun insertRentalRequest(req: RentalRequestEntity): Long {
        val id = dao.insertRentalRequest(req)
        dao.insertNotification(
            NotificationEntity(
                userId = req.ownerId,
                title = "नयाँ कोठा/घर भाडा अनुरोध!",
                message = "${req.tenantName} ले '${req.rentalTitle}' को लागि भाडा अनुरोध पठाउनुभएको छ।",
                type = "Rental"
            )
        )
        return id
    }
    suspend fun updateRentalRequestStatus(id: Long, status: String, tenantId: Long, rentalTitle: String) {
        dao.updateRentalRequestStatus(id, status)
        dao.insertNotification(
            NotificationEntity(
                userId = tenantId,
                title = "भाडा अनुरोध अपडेट: $status",
                message = "तपाईंको '$rentalTitle' भाडा अनुरोध घरबेटीले '$status' गर्नुभएको छ।",
                type = "Rental"
            )
        )
    }
}
