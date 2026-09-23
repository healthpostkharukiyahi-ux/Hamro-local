package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val phone: String, // e.g. "+977 9841234567"
    val name: String,
    val email: String = "",
    val profilePhoto: String = "",
    val province: String = "Bagmati Province",
    val district: String = "Kathmandu",
    val municipality: String = "Kathmandu Metropolitan",
    val ward: String = "10",
    val address: String = "New Baneshwor",
    val locationLat: Double = 27.6915,
    val locationLng: Double = 85.3420,
    val roles: String = "Customer,Seller,Property Owner,Service Provider",
    val activeRole: String = "Customer",
    val isVerified: Boolean = false,
    val isBlocked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // Mobile & Electronics, Computers, Furniture, etc.
    val priceNpr: Long,
    val isNegotiable: Boolean = true,
    val condition: String = "Used", // New, Used, Like New
    val description: String,
    val imageUrls: String = "",
    val videoUrl: String = "",
    val province: String = "Bagmati Province",
    val district: String = "Kathmandu",
    val municipality: String = "Kathmandu Metropolitan",
    val ward: String = "10",
    val deliveryAvailable: Boolean = true,
    val sellerId: Long,
    val sellerName: String,
    val sellerPhone: String,
    val sellerRating: Float = 4.8f,
    val isDraft: Boolean = false,
    val isApproved: Boolean = true,
    val isFeatured: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "rental_listings")
data class RentalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val propertyType: String, // House, Room, Apartment, Shop, Office, Land, Warehouse, Other
    val monthlyRentNpr: Long,
    val securityDepositNpr: Long = 0,
    val rooms: Int = 1,
    val bathrooms: Int = 1,
    val floor: String = "1st Floor",
    val areaSqFt: String = "300 sq.ft",
    val furnishedStatus: String = "Semi-Furnished", // Furnished, Semi-Furnished, Unfurnished
    val hasWater: Boolean = true,
    val hasElectricity: Boolean = true,
    val hasParking: Boolean = true,
    val hasInternet: Boolean = true,
    val description: String,
    val imageUrls: String = "",
    val province: String = "Bagmati Province",
    val district: String = "Kathmandu",
    val municipality: String = "Kathmandu Metropolitan",
    val ward: String = "32",
    val availableDate: String = "Immediate",
    val status: String = "Available", // Available, Reserved, Rented
    val ownerId: Long,
    val ownerName: String,
    val ownerPhone: String,
    val isApproved: Boolean = true,
    val isFeatured: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "service_providers")
data class ServiceProviderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val name: String,
    val serviceCategory: String, // Electrician, Plumber, Carpenter, etc.
    val skills: String,
    val experienceYears: Int = 5,
    val serviceArea: String = "Kathmandu Valley",
    val startingPriceNpr: Long = 500,
    val pricingType: String = "Per Job", // Hourly, Daily, Per Job, Inspection Fee
    val availableDays: String = "Sun - Fri",
    val availableHours: String = "8:00 AM - 7:00 PM",
    val description: String,
    val isVerified: Boolean = true,
    val rating: Float = 4.9f,
    val completedJobs: Int = 42,
    val phone: String,
    val avatarUrl: String = "",
    val isApproved: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "service_bookings")
data class ServiceBookingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val serviceId: Long,
    val providerId: Long,
    val providerName: String,
    val serviceName: String,
    val customerId: Long,
    val customerName: String,
    val customerPhone: String,
    val bookingDate: String,
    val bookingTime: String,
    val address: String,
    val workDescription: String,
    val status: String = "Pending", // Pending, Accepted, On the Way, Started, Completed, Cancelled
    val estimatedPriceNpr: Long = 500,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val productTitle: String,
    val productPriceNpr: Long,
    val quantity: Int = 1,
    val totalPriceNpr: Long,
    val buyerId: Long,
    val buyerName: String,
    val buyerPhone: String,
    val sellerId: Long,
    val sellerName: String,
    val deliveryAddress: String,
    val paymentMethod: String = "Cash on Delivery", // Cash on Delivery, eSewa, Khalti, Fonepay, Bank Transfer
    val status: String = "Pending", // Pending, Confirmed, Preparing, Shipped, Delivered, Cancelled
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: String, // e.g. "user1_user2"
    val senderId: Long,
    val senderName: String,
    val receiverId: Long,
    val receiverName: String,
    val text: String,
    val itemType: String = "None", // None, Product, Rental, Service
    val itemId: Long = 0,
    val itemTitle: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val title: String,
    val message: String,
    val type: String = "System", // Order, Booking, Rental, Message, Promotion, System
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val itemType: String, // Product, Rental, Service
    val itemId: Long,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetType: String, // Product, Rental, Service, User
    val targetId: Long,
    val reviewerId: Long,
    val reviewerName: String,
    val rating: Int = 5,
    val comment: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reporterId: Long,
    val reporterName: String,
    val itemType: String, // Product, Rental, Service, User
    val itemId: Long,
    val reason: String, // Fake listing, Scam, Wrong information, Offensive content, Illegal item, Spam, Other
    val details: String = "",
    val status: String = "Pending", // Pending, Resolved, Dismissed
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "rental_requests")
data class RentalRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rentalId: Long,
    val rentalTitle: String,
    val ownerId: Long,
    val tenantId: Long,
    val tenantName: String,
    val tenantPhone: String,
    val moveInDate: String,
    val note: String = "",
    val status: String = "Pending", // Pending, Approved, Rejected
    val createdAt: Long = System.currentTimeMillis()
)
