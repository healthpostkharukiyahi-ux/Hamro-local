package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.UserEntity
import com.example.data.repository.HamroRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var context: Context
    private lateinit var db: AppDatabase
    private lateinit var repo: HamroRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        db = AppDatabase.getDatabase(context)
        repo = HamroRepository(db)
    }

    @Test
    fun readStringFromContext() {
        val appName = context.getString(R.string.app_name)
        assertEquals("HAMRO LOCAL", appName)
    }

    @Test
    fun databaseInitializationAndSeed() = runBlocking {
        repo.initDatabaseIfNeeded()
        val users = repo.getAllUsers().first()
        assertTrue("Users should be seeded", users.isNotEmpty())

        val products = repo.getApprovedProducts().first()
        assertTrue("Products should be seeded", products.isNotEmpty())

        val rentals = repo.getApprovedRentals().first()
        assertTrue("Rentals should be seeded", rentals.isNotEmpty())

        val providers = repo.getApprovedProviders().first()
        assertTrue("Providers should be seeded", providers.isNotEmpty())
    }

    @Test
    fun insertAndRetrieveProduct() = runBlocking {
        repo.initDatabaseIfNeeded()
        val newProduct = ProductEntity(
            title = "Test Nepali Handcrafted Carpet",
            category = "Other",
            priceNpr = 12000,
            isNegotiable = true,
            condition = "New",
            description = "Finest quality wool carpet made in Kathmandu",
            imageUrls = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500",
            province = "Bagmati Province",
            district = "Kathmandu",
            municipality = "Kathmandu Metropolitan",
            ward = "3",
            deliveryAvailable = true,
            sellerId = 2L,
            sellerName = "Suresh Shrestha",
            sellerPhone = "+977-9841234567",
            sellerRating = 4.8f,
            isDraft = false,
            isApproved = true
        )
        val id = repo.insertProduct(newProduct)
        assertTrue(id > 0)

        val retrieved = repo.getProductById(id).first()
        assertNotNull(retrieved)
        assertEquals("Test Nepali Handcrafted Carpet", retrieved?.title)
    }
}
