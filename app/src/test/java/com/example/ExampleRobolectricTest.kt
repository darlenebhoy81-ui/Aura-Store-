package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.StoreRepository
import com.example.model.PaymentDetails
import com.example.model.PaymentType
import com.example.model.ShippingAddress
import com.example.model.ShippingMethod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Aura Store", appName)
  }

  @Test
  fun `store repository provides products and handles cart operations`() {
    val products = StoreRepository.products.value
    assertTrue("Products catalog should not be empty", products.isNotEmpty())

    val firstProduct = products.first()
    StoreRepository.addToCart(firstProduct, quantity = 2)

    val cart = StoreRepository.cart.value
    assertTrue("Cart should contain added product", cart.any { it.product.id == firstProduct.id })

    // Test coupon application
    val (success, _) = StoreRepository.applyCoupon("AURA20")
    assertTrue("Valid coupon code should apply", success)
    assertNotNull("Applied coupon should be set", StoreRepository.appliedCoupon.value)
  }

  @Test
  fun `order creation flow completes successfully`() {
    val dummyAddress = ShippingAddress(
      id = "test_addr",
      label = "Home",
      fullName = "Test User",
      phoneNumber = "1234567890",
      streetAddress = "123 Main St",
      city = "Springfield",
      state = "IL",
      postalCode = "62701"
    )
    val dummyPayment = PaymentDetails(type = PaymentType.CREDIT_CARD)

    val product = StoreRepository.products.value.first()
    StoreRepository.addToCart(product, quantity = 1)

    val order = StoreRepository.createOrder(
      address = dummyAddress,
      shippingMethod = ShippingMethod.STANDARD,
      paymentDetails = dummyPayment
    )

    assertNotNull(order)
    assertTrue(order.id.startsWith("ORD-"))
    assertTrue("Cart should be cleared after order placement", StoreRepository.cart.value.isEmpty())
  }
}
