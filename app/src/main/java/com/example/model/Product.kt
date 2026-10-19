package com.example.model

enum class ProductCategory(val displayName: String) {
  ALL("All"),
  ELECTRONICS("Tech & Gadgets"),
  FASHION("Fashion"),
  HOME("Home & Living"),
  BEAUTY("Beauty & Wellness"),
  ACCESSORIES("Accessories"),
  SPORTS("Sports & Fitness")
}

data class ProductColor(
  val name: String,
  val hexValue: Long
)

data class UserReview(
  val id: String,
  val userName: String,
  val rating: Int,
  val date: String,
  val comment: String,
  val verifiedPurchase: Boolean = true
)

data class Product(
  val id: String,
  val title: String,
  val brand: String,
  val category: ProductCategory,
  val price: Double,
  val originalPrice: Double? = null,
  val rating: Float,
  val reviewCount: Int,
  val description: String,
  val features: List<String>,
  val colors: List<ProductColor> = emptyList(),
  val sizes: List<String> = emptyList(),
  val imageUrl: String,
  val isTrending: Boolean = false,
  val isFlashDeal: Boolean = false,
  val stockQuantity: Int = 25,
  val tags: List<String> = emptyList(),
  val reviews: List<UserReview> = emptyList()
) {
  val discountPercent: Int?
    get() = if (originalPrice != null && originalPrice > price) {
      (((originalPrice - price) / originalPrice) * 100).toInt()
    } else null
}
