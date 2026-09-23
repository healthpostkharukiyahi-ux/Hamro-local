package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.local.entities.ProductEntity
import com.example.ui.components.ProductCard
import com.example.ui.theme.HamroLocalTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleProduct = ProductEntity(
      id = 1L,
      title = "OnePlus 11 5G (16GB / 256GB)",
      category = "Mobile & Electronics",
      priceNpr = 68000,
      isNegotiable = true,
      condition = "Like New",
      description = "Official warranty in Nepal",
      imageUrls = "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500",
      province = "Bagmati Province",
      district = "Kathmandu",
      municipality = "Kathmandu Metropolitan",
      ward = "10",
      deliveryAvailable = true,
      sellerId = 1L,
      sellerName = "Ramesh Thapa",
      sellerPhone = "+977-9851234567"
    )

    composeTestRule.setContent {
      HamroLocalTheme {
        ProductCard(
          product = sampleProduct,
          onClick = {},
          onFavoriteToggle = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
