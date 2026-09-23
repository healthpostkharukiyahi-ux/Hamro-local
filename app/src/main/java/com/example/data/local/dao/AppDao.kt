package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // USERS
    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhoneDirect(phone: String): UserEntity?

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    fun getUserByPhone(phone: String): Flow<UserEntity?>

    @Query("SELECT * FROM users ORDER BY id ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isVerified = :isVerified WHERE id = :userId")
    suspend fun updateUserVerification(userId: Long, isVerified: Boolean)

    @Query("UPDATE users SET isBlocked = :isBlocked WHERE id = :userId")
    suspend fun updateUserBlocked(userId: Long, isBlocked: Boolean)

    // PRODUCTS
    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isApproved = 1 AND isDraft = 0 ORDER BY id DESC")
    fun getApprovedProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isFeatured = 1 AND isApproved = 1 AND isDraft = 0 ORDER BY id DESC")
    fun getFeaturedProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE category = :category AND isApproved = 1 AND isDraft = 0 ORDER BY id DESC")
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE sellerId = :sellerId ORDER BY id DESC")
    fun getProductsBySeller(sellerId: Long): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    fun getProductById(id: Long): Flow<ProductEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProduct(id: Long)

    @Query("UPDATE products SET isApproved = :approved WHERE id = :id")
    suspend fun updateProductApproval(id: Long, approved: Boolean)

    @Query("UPDATE products SET isFeatured = :featured WHERE id = :id")
    suspend fun updateProductFeatured(id: Long, featured: Boolean)

    // RENTALS
    @Query("SELECT * FROM rental_listings ORDER BY id DESC")
    fun getAllRentals(): Flow<List<RentalEntity>>

    @Query("SELECT * FROM rental_listings WHERE isApproved = 1 ORDER BY id DESC")
    fun getApprovedRentals(): Flow<List<RentalEntity>>

    @Query("SELECT * FROM rental_listings WHERE isFeatured = 1 AND isApproved = 1 ORDER BY id DESC")
    fun getFeaturedRentals(): Flow<List<RentalEntity>>

    @Query("SELECT * FROM rental_listings WHERE propertyType = :propertyType AND isApproved = 1 ORDER BY id DESC")
    fun getRentalsByType(propertyType: String): Flow<List<RentalEntity>>

    @Query("SELECT * FROM rental_listings WHERE ownerId = :ownerId ORDER BY id DESC")
    fun getRentalsByOwner(ownerId: Long): Flow<List<RentalEntity>>

    @Query("SELECT * FROM rental_listings WHERE id = :id")
    fun getRentalById(id: Long): Flow<RentalEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRental(rental: RentalEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRentals(rentals: List<RentalEntity>)

    @Update
    suspend fun updateRental(rental: RentalEntity)

    @Query("DELETE FROM rental_listings WHERE id = :id")
    suspend fun deleteRental(id: Long)

    @Query("UPDATE rental_listings SET status = :status WHERE id = :id")
    suspend fun updateRentalStatus(id: Long, status: String)

    @Query("UPDATE rental_listings SET isApproved = :approved WHERE id = :id")
    suspend fun updateRentalApproval(id: Long, approved: Boolean)

    @Query("UPDATE rental_listings SET isFeatured = :featured WHERE id = :id")
    suspend fun updateRentalFeatured(id: Long, featured: Boolean)

    // SERVICE PROVIDERS
    @Query("SELECT * FROM service_providers ORDER BY id DESC")
    fun getAllProviders(): Flow<List<ServiceProviderEntity>>

    @Query("SELECT * FROM service_providers WHERE isApproved = 1 ORDER BY id DESC")
    fun getApprovedProviders(): Flow<List<ServiceProviderEntity>>

    @Query("SELECT * FROM service_providers WHERE serviceCategory = :category AND isApproved = 1 ORDER BY id DESC")
    fun getProvidersByCategory(category: String): Flow<List<ServiceProviderEntity>>

    @Query("SELECT * FROM service_providers WHERE id = :id")
    fun getProviderById(id: Long): Flow<ServiceProviderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProvider(provider: ServiceProviderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProviders(providers: List<ServiceProviderEntity>)

    @Update
    suspend fun updateProvider(provider: ServiceProviderEntity)

    @Query("UPDATE service_providers SET isVerified = :verified WHERE id = :id")
    suspend fun updateProviderVerification(id: Long, verified: Boolean)

    @Query("UPDATE service_providers SET isApproved = :approved WHERE id = :id")
    suspend fun updateProviderApproval(id: Long, approved: Boolean)

    // SERVICE BOOKINGS
    @Query("SELECT * FROM service_bookings WHERE customerId = :customerId ORDER BY id DESC")
    fun getBookingsForCustomer(customerId: Long): Flow<List<ServiceBookingEntity>>

    @Query("SELECT * FROM service_bookings WHERE providerId = :providerId ORDER BY id DESC")
    fun getBookingsForProvider(providerId: Long): Flow<List<ServiceBookingEntity>>

    @Query("SELECT * FROM service_bookings ORDER BY id DESC")
    fun getAllBookings(): Flow<List<ServiceBookingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: ServiceBookingEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookings(bookings: List<ServiceBookingEntity>)

    @Query("UPDATE service_bookings SET status = :status WHERE id = :id")
    suspend fun updateBookingStatus(id: Long, status: String)

    // ORDERS
    @Query("SELECT * FROM orders WHERE buyerId = :buyerId ORDER BY id DESC")
    fun getOrdersForBuyer(buyerId: Long): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE sellerId = :sellerId ORDER BY id DESC")
    fun getOrdersForSeller(sellerId: Long): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders ORDER BY id DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<OrderEntity>)

    @Query("UPDATE orders SET status = :status WHERE id = :id")
    suspend fun updateOrderStatus(id: Long, status: String)

    // MESSAGES
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getConversationMessages(conversationId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE senderId = :userId OR receiverId = :userId ORDER BY timestamp DESC")
    fun getAllMessagesForUser(userId: Long): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Query("UPDATE messages SET isRead = 1 WHERE conversationId = :conversationId AND receiverId = :receiverId")
    suspend fun markMessagesAsRead(conversationId: String, receiverId: Long)

    // NOTIFICATIONS
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotificationsForUser(userId: Long): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllNotificationsAsRead(userId: Long)

    // FAVORITES
    @Query("SELECT * FROM favorites WHERE userId = :userId ORDER BY id DESC")
    fun getFavoritesForUser(userId: Long): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND itemType = :itemType AND itemId = :itemId)")
    fun isFavorite(userId: Long, itemType: String, itemId: Long): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity): Long

    @Query("DELETE FROM favorites WHERE userId = :userId AND itemType = :itemType AND itemId = :itemId")
    suspend fun deleteFavorite(userId: Long, itemType: String, itemId: Long)

    // REVIEWS
    @Query("SELECT * FROM reviews WHERE targetType = :targetType AND targetId = :targetId ORDER BY id DESC")
    fun getReviewsForTarget(targetType: String, targetId: Long): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ReviewEntity>)

    // REPORTS
    @Query("SELECT * FROM reports ORDER BY id DESC")
    fun getAllReports(): Flow<List<ReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity): Long

    @Query("UPDATE reports SET status = :status WHERE id = :id")
    suspend fun updateReportStatus(id: Long, status: String)

    // RENTAL REQUESTS
    @Query("SELECT * FROM rental_requests WHERE tenantId = :tenantId ORDER BY id DESC")
    fun getRequestsForTenant(tenantId: Long): Flow<List<RentalRequestEntity>>

    @Query("SELECT * FROM rental_requests WHERE ownerId = :ownerId ORDER BY id DESC")
    fun getRequestsForOwner(ownerId: Long): Flow<List<RentalRequestEntity>>

    @Query("SELECT * FROM rental_requests ORDER BY id DESC")
    fun getAllRentalRequests(): Flow<List<RentalRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRentalRequest(request: RentalRequestEntity): Long

    @Query("UPDATE rental_requests SET status = :status WHERE id = :id")
    suspend fun updateRentalRequestStatus(id: Long, status: String)
}
