package com.example.model

data class CartItem(
  val id: String,
  val product: Product,
  val quantity: Int,
  val selectedColor: ProductColor? = null,
  val selectedSize: String? = null
) {
  val totalPrice: Double
    get() = product.price * quantity
}

data class Coupon(
  val code: String,
  val discountPercent: Double = 0.0,
  val fixedDiscount: Double = 0.0,
  val minSpend: Double = 0.0,
  val description: String
) {
  fun calculateDiscount(subtotal: Double): Double {
    if (subtotal < minSpend) return 0.0
    return when {
      discountPercent > 0 -> subtotal * (discountPercent / 100.0)
      fixedDiscount > 0 -> fixedDiscount.coerceAtMost(subtotal)
      else -> 0.0
    }
  }
}

data class ShippingAddress(
  val id: String,
  val label: String, // "Home", "Office"
  val fullName: String,
  val phoneNumber: String,
  val streetAddress: String,
  val city: String,
  val state: String,
  val postalCode: String,
  val country: String = "United States",
  val isDefault: Boolean = false
)

enum class ShippingMethod(
  val title: String,
  val description: String,
  val cost: Double,
  val estimatedDelivery: String
) {
  STANDARD("Standard Delivery", "Arrives in 3-5 business days", 0.0, "3-5 days"),
  EXPRESS("Express Air", "Arrives in 1-2 business days", 9.99, "1-2 days"),
  SAME_DAY("Priority Courier", "Delivery by 8:00 PM today", 14.99, "Same Day")
}

enum class PaymentType(val title: String) {
  CREDIT_CARD("Credit / Debit Card"),
  GOOGLE_PAY("Google Pay"),
  CASH_ON_DELIVERY("Cash on Delivery")
}

data class PaymentDetails(
  val type: PaymentType,
  val cardNumber: String = "",
  val cardHolder: String = "",
  val expiryDate: String = "",
  val cvv: String = ""
)

enum class OrderStatus(val title: String, val stepIndex: Int) {
  PLACED("Order Placed", 0),
  PROCESSING("Processing & Packing", 1),
  SHIPPED("Shipped with Carrier", 2),
  OUT_FOR_DELIVERY("Out for Delivery", 3),
  DELIVERED("Delivered", 4),
  CANCELLED("Cancelled", -1)
}

data class Order(
  val id: String,
  val items: List<CartItem>,
  val subtotal: Double,
  val discount: Double,
  val shippingCost: Double,
  val tax: Double,
  val total: Double,
  val shippingAddress: ShippingAddress,
  val shippingMethod: ShippingMethod,
  val paymentMethod: PaymentDetails,
  val createdAt: Long,
  val status: OrderStatus,
  val trackingNumber: String,
  val couponCode: String? = null
)
