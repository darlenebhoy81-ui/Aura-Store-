package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.StoreRepository
import com.example.model.CartItem
import com.example.model.Coupon
import com.example.model.Order
import com.example.model.PaymentDetails
import com.example.model.PaymentType
import com.example.model.Product
import com.example.model.ProductCategory
import com.example.model.ProductColor
import com.example.model.ShippingAddress
import com.example.model.ShippingMethod
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface Screen {
  object Home : Screen
  data class Detail(val productId: String) : Screen
  object Cart : Screen
  object Checkout : Screen
  object Orders : Screen
  data class OrderDetail(val orderId: String) : Screen
  object Wishlist : Screen
  object Profile : Screen
}

enum class SortOption(val label: String) {
  POPULAR("Most Popular"),
  RATING("Top Rated"),
  PRICE_LOW_HIGH("Price: Low to High"),
  PRICE_HIGH_LOW("Price: High to Low")
}

data class FilterState(
  val selectedCategory: ProductCategory = ProductCategory.ALL,
  val searchQuery: String = "",
  val sortOption: SortOption = SortOption.POPULAR,
  val maxPrice: Float = 500f,
  val minRating: Float = 0f,
  val inStockOnly: Boolean = false
)

enum class CheckoutStep(val stepNumber: Int, val title: String) {
  ADDRESS(1, "Shipping"),
  SHIPPING(2, "Delivery"),
  PAYMENT(3, "Payment"),
  REVIEW(4, "Review"),
  SUCCESS(5, "Confirmation")
}

class StoreViewModel : ViewModel() {

  // Navigation Stack
  private val _screenStack = MutableStateFlow<List<Screen>>(listOf(Screen.Home))
  val currentScreen: StateFlow<Screen> = _screenStack
    .combine(MutableStateFlow(Unit)) { stack, _ -> stack.lastOrNull() ?: Screen.Home }
    .stateIn(viewModelScope, SharingStarted.Eagerly, Screen.Home)

  // Catalog State
  val products: StateFlow<List<Product>> = StoreRepository.products
  val cart: StateFlow<List<CartItem>> = StoreRepository.cart
  val wishlist: StateFlow<Set<String>> = StoreRepository.wishlist
  val orders: StateFlow<List<Order>> = StoreRepository.orders
  val appliedCoupon: StateFlow<Coupon?> = StoreRepository.appliedCoupon
  val savedAddresses: StateFlow<List<ShippingAddress>> = StoreRepository.savedAddresses

  // Filter & Search
  private val _filterState = MutableStateFlow(FilterState())
  val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

  // Filtered Products
  val filteredProducts: StateFlow<List<Product>> = combine(products, filterState) { list, filter ->
    var result = list

    if (filter.selectedCategory != ProductCategory.ALL) {
      result = result.filter { it.category == filter.selectedCategory }
    }

    if (filter.searchQuery.isNotBlank()) {
      val q = filter.searchQuery.trim().lowercase()
      result = result.filter {
        it.title.lowercase().contains(q) ||
            it.brand.lowercase().contains(q) ||
            it.description.lowercase().contains(q) ||
            it.tags.any { tag -> tag.lowercase().contains(q) }
      }
    }

    result = result.filter { it.price <= filter.maxPrice }

    if (filter.minRating > 0f) {
      result = result.filter { it.rating >= filter.minRating }
    }

    if (filter.inStockOnly) {
      result = result.filter { it.stockQuantity > 0 }
    }

    when (filter.sortOption) {
      SortOption.POPULAR -> result.sortedByDescending { it.reviewCount }
      SortOption.RATING -> result.sortedByDescending { it.rating }
      SortOption.PRICE_LOW_HIGH -> result.sortedBy { it.price }
      SortOption.PRICE_HIGH_LOW -> result.sortedByDescending { it.price }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Product Detail Selection State
  private val _selectedProduct = MutableStateFlow<Product?>(null)
  val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

  private val _selectedColor = MutableStateFlow<ProductColor?>(null)
  val selectedColor: StateFlow<ProductColor?> = _selectedColor.asStateFlow()

  private val _selectedSize = MutableStateFlow<String?>(null)
  val selectedSize: StateFlow<String?> = _selectedSize.asStateFlow()

  private val _detailQuantity = MutableStateFlow(1)
  val detailQuantity: StateFlow<Int> = _detailQuantity.asStateFlow()

  // Cart Calculations
  val cartSubtotal: StateFlow<Double> = cart.combine(MutableStateFlow(Unit)) { items, _ ->
    items.sumOf { it.totalPrice }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

  val couponDiscount: StateFlow<Double> = combine(cartSubtotal, appliedCoupon) { subtotal, coupon ->
    coupon?.calculateDiscount(subtotal) ?: 0.0
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

  // Checkout State
  private val _checkoutStep = MutableStateFlow(CheckoutStep.ADDRESS)
  val checkoutStep: StateFlow<CheckoutStep> = _checkoutStep.asStateFlow()

  private val _selectedAddress = MutableStateFlow<ShippingAddress?>(null)
  val selectedAddress: StateFlow<ShippingAddress?> = _selectedAddress.asStateFlow()

  private val _selectedShippingMethod = MutableStateFlow(ShippingMethod.STANDARD)
  val selectedShippingMethod: StateFlow<ShippingMethod> = _selectedShippingMethod.asStateFlow()

  private val _paymentDetails = MutableStateFlow(
    PaymentDetails(
      type = PaymentType.CREDIT_CARD,
      cardNumber = "4242 •••• •••• 4242",
      cardHolder = "Alex Morgan",
      expiryDate = "08/28",
      cvv = "888"
    )
  )
  val paymentDetails: StateFlow<PaymentDetails> = _paymentDetails.asStateFlow()

  private val _lastPlacedOrder = MutableStateFlow<Order?>(null)
  val lastPlacedOrder: StateFlow<Order?> = _lastPlacedOrder.asStateFlow()

  // Selected Order for Detail View
  private val _selectedOrder = MutableStateFlow<Order?>(null)
  val selectedOrder: StateFlow<Order?> = _selectedOrder.asStateFlow()

  // User notification message
  private val _userMessage = MutableSharedFlow<String>()
  val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

  init {
    viewModelScope.launch {
      savedAddresses.collect { list ->
        if (_selectedAddress.value == null && list.isNotEmpty()) {
          _selectedAddress.value = list.firstOrNull { it.isDefault } ?: list.first()
        }
      }
    }
  }

  // Navigation Methods
  fun navigateTo(screen: Screen) {
    val current = _screenStack.value.toMutableList()
    if (screen is Screen.Home) {
      _screenStack.value = listOf(Screen.Home)
      return
    }
    // Prevent duplicate adjacent destinations
    if (current.lastOrNull() != screen) {
      current.add(screen)
      _screenStack.value = current
    }
  }

  fun navigateBack(): Boolean {
    val current = _screenStack.value.toMutableList()
    if (current.size > 1) {
      current.removeAt(current.size - 1)
      _screenStack.value = current
      return true
    }
    return false
  }

  // Detail Screen Handling
  fun openProductDetail(product: Product) {
    _selectedProduct.value = product
    _selectedColor.value = product.colors.firstOrNull()
    _selectedSize.value = product.sizes.firstOrNull()
    _detailQuantity.value = 1
    navigateTo(Screen.Detail(product.id))
  }

  fun setSelectedColor(color: ProductColor) {
    _selectedColor.value = color
  }

  fun setSelectedSize(size: String) {
    _selectedSize.value = size
  }

  fun setDetailQuantity(quantity: Int) {
    _detailQuantity.value = quantity.coerceIn(1, 10)
  }

  // Cart Operations
  fun addCurrentToCart(andCheckout: Boolean = false) {
    val product = _selectedProduct.value ?: return
    StoreRepository.addToCart(
      product = product,
      quantity = _detailQuantity.value,
      color = _selectedColor.value,
      size = _selectedSize.value
    )
    viewModelScope.launch {
      _userMessage.emit("Added ${product.title} to Bag")
    }
    if (andCheckout) {
      startCheckout()
    }
  }

  fun quickAddToCart(product: Product) {
    StoreRepository.addToCart(product, 1)
    viewModelScope.launch {
      _userMessage.emit("Added ${product.title} to Bag")
    }
  }

  fun updateCartQty(cartItemId: String, qty: Int) {
    StoreRepository.updateCartQuantity(cartItemId, qty)
  }

  fun removeCartItem(cartItemId: String) {
    StoreRepository.removeFromCart(cartItemId)
  }

  fun applyCoupon(code: String) {
    val result = StoreRepository.applyCoupon(code)
    viewModelScope.launch {
      _userMessage.emit(result.second)
    }
  }

  fun removeCoupon() {
    StoreRepository.removeCoupon()
    viewModelScope.launch {
      _userMessage.emit("Coupon removed")
    }
  }

  // Wishlist
  fun toggleWishlist(product: Product) {
    StoreRepository.toggleWishlist(product.id)
    val isNowFav = StoreRepository.isWishlisted(product.id)
    viewModelScope.launch {
      _userMessage.emit(if (isNowFav) "Saved to Wishlist" else "Removed from Wishlist")
    }
  }

  fun isWishlisted(productId: String): Boolean = StoreRepository.isWishlisted(productId)

  // Filters
  fun setCategory(category: ProductCategory) {
    _filterState.value = _filterState.value.copy(selectedCategory = category)
  }

  fun setSearchQuery(query: String) {
    _filterState.value = _filterState.value.copy(searchQuery = query)
  }

  fun setSortOption(sort: SortOption) {
    _filterState.value = _filterState.value.copy(sortOption = sort)
  }

  fun setPriceFilter(maxPrice: Float) {
    _filterState.value = _filterState.value.copy(maxPrice = maxPrice)
  }

  fun resetFilters() {
    _filterState.value = FilterState()
  }

  // Checkout Flow
  fun startCheckout() {
    _checkoutStep.value = CheckoutStep.ADDRESS
    navigateTo(Screen.Checkout)
  }

  fun setCheckoutStep(step: CheckoutStep) {
    _checkoutStep.value = step
  }

  fun selectAddress(address: ShippingAddress) {
    _selectedAddress.value = address
  }

  fun setShippingMethod(method: ShippingMethod) {
    _selectedShippingMethod.value = method
  }

  fun updatePaymentDetails(details: PaymentDetails) {
    _paymentDetails.value = details
  }

  fun placeOrder() {
    val address = _selectedAddress.value ?: return
    val method = _selectedShippingMethod.value
    val payment = _paymentDetails.value

    val newOrder = StoreRepository.createOrder(
      address = address,
      shippingMethod = method,
      paymentDetails = payment
    )
    _lastPlacedOrder.value = newOrder
    _checkoutStep.value = CheckoutStep.SUCCESS
  }

  // Order Details
  fun viewOrderDetail(order: Order) {
    _selectedOrder.value = order
    navigateTo(Screen.OrderDetail(order.id))
  }

  // Reviews
  fun submitReview(productId: String, rating: Int, comment: String, reviewerName: String) {
    StoreRepository.addReview(productId, rating, comment, reviewerName)
    // Update local selected product if viewing
    _selectedProduct.value = StoreRepository.products.value.firstOrNull { it.id == productId }
    viewModelScope.launch {
      _userMessage.emit("Thank you! Your review has been published.")
    }
  }
}
