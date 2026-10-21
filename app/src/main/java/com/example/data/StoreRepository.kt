package com.example.data

import com.example.model.CartItem
import com.example.model.Coupon
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.model.PaymentDetails
import com.example.model.PaymentType
import com.example.model.Product
import com.example.model.ProductCategory
import com.example.model.ProductColor
import com.example.model.ShippingAddress
import com.example.model.ShippingMethod
import com.example.model.UserReview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object StoreRepository {

  private val _products = MutableStateFlow<List<Product>>(emptyList())
  val products: StateFlow<List<Product>> = _products.asStateFlow()

  private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
  val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

  private val _wishlist = MutableStateFlow<Set<String>>(emptySet())
  val wishlist: StateFlow<Set<String>> = _wishlist.asStateFlow()

  private val _orders = MutableStateFlow<List<Order>>(emptyList())
  val orders: StateFlow<List<Order>> = _orders.asStateFlow()

  private val _appliedCoupon = MutableStateFlow<Coupon?>(null)
  val appliedCoupon: StateFlow<Coupon?> = _appliedCoupon.asStateFlow()

  private val _savedAddresses = MutableStateFlow<List<ShippingAddress>>(emptyList())
  val savedAddresses: StateFlow<List<ShippingAddress>> = _savedAddresses.asStateFlow()

  val availableCoupons = listOf(
    Coupon("DAILY20", discountPercent = 20.0, description = "20% off all orders at Daily Shop"),
    Coupon("AURA20", discountPercent = 20.0, description = "20% off all orders"),
    Coupon("SAVE15", fixedDiscount = 15.0, minSpend = 50.0, description = "$15 off orders over $50"),
    Coupon("FREESHIP", fixedDiscount = 9.99, description = "Free Express Shipping"),
    Coupon("WELCOME10", discountPercent = 10.0, description = "10% Welcome Discount")
  )

  init {
    initializeCatalog()
    initializeUserData()
  }

  private fun initializeCatalog() {
    val items = listOf(
      Product(
        id = "prod_1",
        title = "Acoustic Pro Wireless ANC Headphones",
        brand = "SoundAura",
        category = ProductCategory.ELECTRONICS,
        price = 199.99,
        originalPrice = 249.99,
        rating = 4.8f,
        reviewCount = 342,
        description = "Engineered with custom 40mm beryllium drivers, active hybrid noise cancellation, 45-hour battery life, and plush memory foam earcups for uninterrupted studio-grade audio fidelity.",
        features = listOf(
          "Hybrid Active Noise Cancellation (40dB reduction)",
          "45-hour battery life with Fast Fuel (10 min = 5 hrs)",
          "Multipoint Bluetooth 5.3 connection",
          "Foldable aluminum chassis with vegan leather case"
        ),
        colors = listOf(
          ProductColor("Matte Black", 0xFF1F2421),
          ProductColor("Silver Mist", 0xFFD8DBE2),
          ProductColor("Midnight Navy", 0xFF1E2A4A)
        ),
        sizes = emptyList(),
        imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&q=80",
        isTrending = true,
        isFlashDeal = true,
        stockQuantity = 14,
        tags = listOf("Bestseller", "Sale", "Audio"),
        reviews = listOf(
          UserReview("rev_1", "Marcus Vance", 5, "2 days ago", "The noise cancellation rivaling the top flagships at this price is unreal. Battery easily lasted through my entire flight to Tokyo."),
          UserReview("rev_2", "Elena Rostova", 5, "1 week ago", "Crisp highs, punchy bass that doesn't muddy the mids. Extremely comfortable for 8+ hour work sessions.")
        )
      ),
      Product(
        id = "prod_2",
        title = "Luxe Minimalist Chrono Smartwatch",
        brand = "Aura Time",
        category = ProductCategory.ELECTRONICS,
        price = 279.00,
        originalPrice = 320.00,
        rating = 4.7f,
        reviewCount = 189,
        description = "A titanium-cased luxury smartwatch blending timeless Swiss minimalist dial aesthetics with 24/7 heart-rate, ECG, sleep tracking, and a vibrant sapphire crystal AMOLED display.",
        features = listOf(
          "Always-On Ultra HD AMOLED display (1000 nits)",
          "Grade 2 Titanium case with sapphire glass",
          "SpO2, ECG, and Stress bio-monitoring sensors",
          "7-day typical battery life & 5 ATM water resistance"
        ),
        colors = listOf(
          ProductColor("Titanium Gray", 0xFF6C757D),
          ProductColor("Obsidian Black", 0xFF121212),
          ProductColor("Rose Gold", 0xFFB76E79)
        ),
        sizes = listOf("40mm", "44mm"),
        imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800&q=80",
        isTrending = true,
        isFlashDeal = false,
        stockQuantity = 22,
        tags = listOf("Premium", "Wearable"),
        reviews = listOf(
          UserReview("rev_3", "David Kim", 5, "3 days ago", "Looks like a $1000 dress watch with modern smart features. The battery easily lasts a full week.")
        )
      ),
      Product(
        id = "prod_3",
        title = "Merino Wool Cloud Knit Sweater",
        brand = "Nordic Thread",
        category = ProductCategory.FASHION,
        price = 115.00,
        originalPrice = 140.00,
        rating = 4.9f,
        reviewCount = 210,
        description = "Crafted from 100% superfine Australian Merino wool. Unbelievably soft, naturally temperature-regulating, and tailored with a modern relaxed silhouette that drapes effortlessly.",
        features = listOf(
          "100% Superfine 19.5-micron Merino Wool",
          "Ribbed collar, cuffs, and hem",
          "Breathable & odor-resistant natural fibers",
          "Pre-washed to prevent shrinkage"
        ),
        colors = listOf(
          ProductColor("Oatmeal Heather", 0xFFE0D8C3),
          ProductColor("Forest Pine", 0xFF2D5A43),
          ProductColor("Charcoal", 0xFF333333)
        ),
        sizes = listOf("XS", "S", "M", "L", "XL"),
        imageUrl = "https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=800&q=80",
        isTrending = true,
        isFlashDeal = false,
        stockQuantity = 30,
        tags = listOf("Winter Essential", "Organic"),
        reviews = listOf(
          UserReview("rev_4", "Sophia Lin", 5, "5 days ago", "Zero itchiness! So soft and cozy, fits true to size. Bought two different colors.")
        )
      ),
      Product(
        id = "prod_4",
        title = "Velocity Nitro Running Shoes",
        brand = "Aerovelo",
        category = ProductCategory.SPORTS,
        price = 149.99,
        originalPrice = 180.00,
        rating = 4.6f,
        reviewCount = 412,
        description = "Supercritical nitrogen-infused foam midsole delivers maximum energy return with every stride. Engineered breathable jacquard mesh upper locks in comfort for 5K to marathon distances.",
        features = listOf(
          "Supercritical Nitro-foam cushioning",
          "Carbon composite stabilization plate",
          "Continental rubber high-traction outsole",
          "Weight: only 215g (Men's US 9)"
        ),
        colors = listOf(
          ProductColor("Cyber Volt & White", 0xFFE8F5E9),
          ProductColor("Phantom Black", 0xFF1C1C1E),
          ProductColor("Cobalt Blue", 0xFF1976D2)
        ),
        sizes = listOf("US 8", "US 8.5", "US 9", "US 9.5", "US 10", "US 11", "US 12"),
        imageUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800&q=80",
        isTrending = true,
        isFlashDeal = true,
        stockQuantity = 18,
        tags = listOf("Flash Deal", "Footwear"),
        reviews = listOf(
          UserReview("rev_5", "Alex Rivera", 5, "Yesterday", "Shaved 45 seconds off my 10k pace on day one. The bounce from the foam plate is addictive.")
        )
      ),
      Product(
        id = "prod_5",
        title = "Handcrafted Full-Grain Leather Weekender Bag",
        brand = "Artisan & Hide",
        category = ProductCategory.ACCESSORIES,
        price = 220.00,
        originalPrice = 275.00,
        rating = 4.9f,
        reviewCount = 98,
        description = "Vegetable-tanned full-grain cowhide that develops a rich, lustrous patina over years of travels. Features heavy-duty brass YKK zippers, dedicated padded laptop sleeve, and water-resistant shoe compartment.",
        features = listOf(
          "100% full-grain vegetable-tanned leather",
          "Reinforced solid brass hardware & rivets",
          "Dedicated 16-inch laptop compartment",
          "Meets international airline carry-on dimensions"
        ),
        colors = listOf(
          ProductColor("Cognac Brown", 0xFF9E4714),
          ProductColor("Espresso Dark", 0xFF4A2E18),
          ProductColor("Jet Black", 0xFF1A1A1A)
        ),
        sizes = listOf("45 Liters"),
        imageUrl = "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800&q=80",
        isTrending = false,
        isFlashDeal = false,
        stockQuantity = 8,
        tags = listOf("Handmade", "Travel"),
        reviews = listOf(
          UserReview("rev_6", "Thomas Reed", 5, "2 weeks ago", "Smells wonderful right out of the box. The craftsmanship is bespoke quality.")
        )
      ),
      Product(
        id = "prod_6",
        title = "Artisan Ceramic Pour-Over Coffee Station",
        brand = "Kanso Home",
        category = ProductCategory.HOME,
        price = 68.00,
        originalPrice = 85.00,
        rating = 4.8f,
        reviewCount = 144,
        description = "Designed for mindful mornings. Includes a hand-thrown ceramic dripper with spiral flow ridges, a 600ml borosilicate glass decanter, and a solid American walnut serving base.",
        features = listOf(
          "Handmade ceramic cone with 60-degree extraction angle",
          "Heat-resistant 600ml borosilicate glass carafe",
          "Water-repellent solid American Walnut base",
          "Includes pack of 40 unbleached Japanese cone filters"
        ),
        colors = listOf(
          ProductColor("Sandstone Glaze", 0xFFDDD5C7),
          ProductColor("Basalt Matte Black", 0xFF2A2A2A)
        ),
        sizes = listOf("Standard (1-4 cups)"),
        imageUrl = "https://images.unsplash.com/photo-1517256064527-09c73fc73e38?w=800&q=80",
        isTrending = false,
        isFlashDeal = true,
        stockQuantity = 25,
        tags = listOf("Home", "Coffee"),
        reviews = listOf(
          UserReview("rev_7", "Clara Oswald", 5, "4 days ago", "Brew quality is on par with specialty coffee shops. Beautiful centerpiece on my kitchen counter.")
        )
      ),
      Product(
        id = "prod_7",
        title = "Botanical Hydration Glow Serum",
        brand = "Aura Botanicals",
        category = ProductCategory.BEAUTY,
        price = 48.00,
        originalPrice = 60.00,
        rating = 4.9f,
        reviewCount = 520,
        description = "A multi-molecular hyaluronic acid elixir combined with niacinamide, fermented green tea, and vegan squalane for intense plumping moisture and glass-skin radiance.",
        features = listOf(
          "5-weight Hyaluronic Acid complex for deep hydration",
          "5% Niacinamide to even skin tone & refine pores",
          "Clean, vegan, cruelty-free, and fragrance-free",
          "Dermatologist tested on sensitive skin"
        ),
        colors = emptyList(),
        sizes = listOf("30ml", "50ml"),
        imageUrl = "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=800&q=80",
        isTrending = true,
        isFlashDeal = false,
        stockQuantity = 50,
        tags = listOf("Bestseller", "Clean Beauty"),
        reviews = listOf(
          UserReview("rev_8", "Maya Johnson", 5, "3 days ago", "My skin has never felt this hydrated in winter. Absorbs quickly without any stickiness.")
        )
      ),
      Product(
        id = "prod_8",
        title = "Polarized Acetate Sunglasses",
        brand = "Sunhaus",
        category = ProductCategory.ACCESSORIES,
        price = 89.00,
        originalPrice = 110.00,
        rating = 4.7f,
        reviewCount = 167,
        description = "Hand-cut Italian Mazzucchelli acetate frames fitted with scratch-resistant nylon polarized lenses providing 100% UVA/UVB Category 3 optical clarity.",
        features = listOf(
          "Italian Mazzucchelli bio-acetate frame",
          "Tri-acetate polarized lenses (100% UV400)",
          "5-barrel German OBE hinges for longevity",
          "Includes hard protective travel case & microfiber cloth"
        ),
        colors = listOf(
          ProductColor("Tortoise Shell", 0xFF7B4B27),
          ProductColor("Onyx Black", 0xFF141414),
          ProductColor("Champagne Crystal", 0xFFE6DCBE)
        ),
        sizes = listOf("Standard 50-20-145"),
        imageUrl = "https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=800&q=80",
        isTrending = false,
        isFlashDeal = false,
        stockQuantity = 19,
        tags = listOf("Eyewear", "Summer"),
        reviews = listOf(
          UserReview("rev_9", "Jordan Miller", 4, "1 week ago", "Great build quality, substantial weight without being heavy on the bridge.")
        )
      ),
      Product(
        id = "prod_9",
        title = "Smart Ultrasonic Aromatherapy Diffuser",
        brand = "Aura Living",
        category = ProductCategory.HOME,
        price = 54.99,
        originalPrice = 69.99,
        rating = 4.7f,
        reviewCount = 230,
        description = "Silent ultrasonic vibration technology disperses pure essential oils in a fine cool mist. Features ambient warm LED ring, waterless auto-shutoff, and whisper-quiet operation under 20dB.",
        features = listOf(
          "Whisper-quiet 2.4MHz ultrasonic cool mist",
          "400ml reservoir runs up to 16 hours continuously",
          "Dimmable warm amber ambient breathing light",
          "BPA-free medical grade materials"
        ),
        colors = listOf(
          ProductColor("Nordic Stone", 0xFFD3D3D3),
          ProductColor("Warm Terracotta", 0xFFC86D51)
        ),
        sizes = listOf("400ml"),
        imageUrl = "https://images.unsplash.com/photo-1608571423902-eed4a5ad8108?w=800&q=80",
        isTrending = false,
        isFlashDeal = true,
        stockQuantity = 35,
        tags = listOf("Wellness", "Home"),
        reviews = listOf(
          UserReview("rev_10", "Rachel Green", 5, "6 days ago", "The warm ambient glow is so calming in the bedroom at night. Fills the room with lavender scent.")
        )
      ),
      Product(
        id = "prod_10",
        title = "Pro Grip Natural Rubber Yoga Mat",
        brand = "Asana Element",
        category = ProductCategory.SPORTS,
        price = 78.00,
        originalPrice = 95.00,
        rating = 4.9f,
        reviewCount = 315,
        description = "Engineered with an eco-certified sustainably harvested tree rubber base and an ultra-absorbent polyurethane top surface that gets grippier as you sweat.",
        features = listOf(
          "Unrivaled non-slip wet & dry grip surface",
          "4.5mm high-density joint cushioning",
          "Laser-etched body alignment lines",
          "100% PVC-free & biodegradable natural rubber"
        ),
        colors = listOf(
          ProductColor("Sage Green", 0xFF7A8B7B),
          ProductColor("Slate Charcoal", 0xFF36393F),
          ProductColor("Deep Plum", 0xFF4A2545)
        ),
        sizes = listOf("72\" x 26\" (Extra Wide)"),
        imageUrl = "https://images.unsplash.com/photo-1592432678016-e910b452f9a2?w=800&q=80",
        isTrending = true,
        isFlashDeal = false,
        stockQuantity = 20,
        tags = listOf("Fitness", "Eco-Friendly"),
        reviews = listOf(
          UserReview("rev_11", "Amanda Cole", 5, "2 days ago", "Hands down the best grip for hot vinyasa. No towel needed at all.")
        )
      )
    )

    _products.value = items
    _wishlist.value = setOf("prod_1", "prod_4")
  }

  private fun initializeUserData() {
    val defaultAddress = ShippingAddress(
      id = "addr_1",
      label = "Home",
      fullName = "Alex Morgan",
      phoneNumber = "+1 (555) 234-8901",
      streetAddress = "742 Evergreen Terrace, Apt 4B",
      city = "San Francisco",
      state = "CA",
      postalCode = "94107",
      country = "United States",
      isDefault = true
    )

    val officeAddress = ShippingAddress(
      id = "addr_2",
      label = "Office",
      fullName = "Alex Morgan",
      phoneNumber = "+1 (555) 890-1234",
      streetAddress = "500 Howard Street, Suite 1200",
      city = "San Francisco",
      state = "CA",
      postalCode = "94105",
      country = "United States",
      isDefault = false
    )

    _savedAddresses.value = listOf(defaultAddress, officeAddress)

    // Pre-populate an initial cart item to show functionality right away
    val firstProduct = _products.value.firstOrNull()
    if (firstProduct != null) {
      _cart.value = listOf(
        CartItem(
          id = "cart_init_1",
          product = firstProduct,
          quantity = 1,
          selectedColor = firstProduct.colors.firstOrNull(),
          selectedSize = firstProduct.sizes.firstOrNull()
        )
      )
    }

    // Pre-populate an active order so tracking can be tested immediately
    val pastOrder = Order(
      id = "ORD-89412",
      items = listOf(
        CartItem(
          id = "item_1",
          product = _products.value[1],
          quantity = 1,
          selectedColor = _products.value[1].colors.firstOrNull(),
          selectedSize = "44mm"
        )
      ),
      subtotal = 279.00,
      discount = 27.90,
      shippingCost = 0.0,
      tax = 22.60,
      total = 273.70,
      shippingAddress = defaultAddress,
      shippingMethod = ShippingMethod.STANDARD,
      paymentMethod = PaymentDetails(PaymentType.CREDIT_CARD, cardNumber = "•••• •••• •••• 4242", cardHolder = "Alex Morgan"),
      createdAt = System.currentTimeMillis() - (86400000L * 2), // 2 days ago
      status = OrderStatus.SHIPPED,
      trackingNumber = "TRK-98421034US",
      couponCode = "WELCOME10"
    )

    val deliveredOrder = Order(
      id = "ORD-76329",
      items = listOf(
        CartItem(
          id = "item_2",
          product = _products.value[2],
          quantity = 1,
          selectedColor = _products.value[2].colors.firstOrNull(),
          selectedSize = "M"
        ),
        CartItem(
          id = "item_3",
          product = _products.value[5],
          quantity = 1,
          selectedColor = _products.value[5].colors.firstOrNull(),
          selectedSize = "Standard"
        )
      ),
      subtotal = 183.00,
      discount = 0.0,
      shippingCost = 0.0,
      tax = 14.64,
      total = 197.64,
      shippingAddress = defaultAddress,
      shippingMethod = ShippingMethod.STANDARD,
      paymentMethod = PaymentDetails(PaymentType.GOOGLE_PAY),
      createdAt = System.currentTimeMillis() - (86400000L * 10), // 10 days ago
      status = OrderStatus.DELIVERED,
      trackingNumber = "TRK-77192305US"
    )

    _orders.value = listOf(pastOrder, deliveredOrder)
  }

  fun addToCart(product: Product, quantity: Int = 1, color: ProductColor? = null, size: String? = null) {
    val current = _cart.value.toMutableList()
    val existingIndex = current.indexOfFirst {
      it.product.id == product.id &&
          it.selectedColor?.name == color?.name &&
          it.selectedSize == size
    }

    if (existingIndex >= 0) {
      val existing = current[existingIndex]
      current[existingIndex] = existing.copy(quantity = existing.quantity + quantity)
    } else {
      current.add(
        CartItem(
          id = UUID.randomUUID().toString(),
          product = product,
          quantity = quantity,
          selectedColor = color ?: product.colors.firstOrNull(),
          selectedSize = size ?: product.sizes.firstOrNull()
        )
      )
    }
    _cart.value = current
  }

  fun updateCartQuantity(cartItemId: String, newQuantity: Int) {
    if (newQuantity <= 0) {
      removeFromCart(cartItemId)
      return
    }
    val current = _cart.value.toMutableList()
    val index = current.indexOfFirst { it.id == cartItemId }
    if (index >= 0) {
      current[index] = current[index].copy(quantity = newQuantity)
      _cart.value = current
    }
  }

  fun removeFromCart(cartItemId: String) {
    _cart.value = _cart.value.filterNot { it.id == cartItemId }
  }

  fun clearCart() {
    _cart.value = emptyList()
    _appliedCoupon.value = null
  }

  fun toggleWishlist(productId: String) {
    val current = _wishlist.value.toMutableSet()
    if (current.contains(productId)) {
      current.remove(productId)
    } else {
      current.add(productId)
    }
    _wishlist.value = current
  }

  fun isWishlisted(productId: String): Boolean = _wishlist.value.contains(productId)

  fun applyCoupon(code: String): Pair<Boolean, String> {
    val trimmed = code.trim().uppercase()
    val found = availableCoupons.find { it.code.equals(trimmed, ignoreCase = true) }
    return if (found != null) {
      val subtotal = _cart.value.sumOf { it.totalPrice }
      if (subtotal < found.minSpend) {
        Pair(false, "Minimum order of \$${"%.2f".format(found.minSpend)} required for ${found.code}")
      } else {
        _appliedCoupon.value = found
        Pair(true, "Promo code ${found.code} applied successfully!")
      }
    } else {
      Pair(false, "Invalid coupon code. Try AURA20, SAVE15 or WELCOME10")
    }
  }

  fun removeCoupon() {
    _appliedCoupon.value = null
  }

  fun createOrder(
    address: ShippingAddress,
    shippingMethod: ShippingMethod,
    paymentDetails: PaymentDetails
  ): Order {
    val items = _cart.value
    val subtotal = items.sumOf { it.totalPrice }
    val coupon = _appliedCoupon.value
    val discount = coupon?.calculateDiscount(subtotal) ?: 0.0
    val shippingCost = if (subtotal >= 75.0 && shippingMethod == ShippingMethod.STANDARD) 0.0 else shippingMethod.cost
    val tax = (subtotal - discount) * 0.08
    val total = (subtotal - discount + shippingCost + tax).coerceAtLeast(0.0)

    val order = Order(
      id = "ORD-${(10000..99999).random()}",
      items = items,
      subtotal = subtotal,
      discount = discount,
      shippingCost = shippingCost,
      tax = tax,
      total = total,
      shippingAddress = address,
      shippingMethod = shippingMethod,
      paymentMethod = paymentDetails,
      createdAt = System.currentTimeMillis(),
      status = OrderStatus.PLACED,
      trackingNumber = "TRK-${(10000000..99999999).random()}US",
      couponCode = coupon?.code
    )

    val currentOrders = _orders.value.toMutableList()
    currentOrders.add(0, order)
    _orders.value = currentOrders

    clearCart()
    return order
  }

  fun addReview(productId: String, rating: Int, comment: String, reviewerName: String) {
    val currentProducts = _products.value.toMutableList()
    val index = currentProducts.indexOfFirst { it.id == productId }
    if (index >= 0) {
      val product = currentProducts[index]
      val newReview = UserReview(
        id = UUID.randomUUID().toString(),
        userName = reviewerName.ifBlank { "Verified Buyer" },
        rating = rating,
        date = "Just now",
        comment = comment,
        verifiedPurchase = true
      )
      val updatedReviews = listOf(newReview) + product.reviews
      val newCount = product.reviewCount + 1
      val newAvg = ((product.rating * product.reviewCount) + rating) / newCount
      currentProducts[index] = product.copy(
        reviews = updatedReviews,
        reviewCount = newCount,
        rating = "%.1f".format(newAvg).toFloat()
      )
      _products.value = currentProducts
    }
  }

  fun addAddress(address: ShippingAddress) {
    val list = _savedAddresses.value.toMutableList()
    if (address.isDefault) {
      for (i in list.indices) {
        list[i] = list[i].copy(isDefault = false)
      }
    }
    list.add(address)
    _savedAddresses.value = list
  }
}
