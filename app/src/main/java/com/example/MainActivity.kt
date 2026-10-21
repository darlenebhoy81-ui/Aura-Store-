package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OrderDetailScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.WishlistScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.Screen
import com.example.viewmodel.StoreViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        DailyShopApp()
      }
    }
  }
}

@Composable
fun DailyShopApp(
  viewModel: StoreViewModel = viewModel()
) {
  val currentScreen by viewModel.currentScreen.collectAsState()
  val cartItems by viewModel.cart.collectAsState()
  val wishlist by viewModel.wishlist.collectAsState()
  val snackbarHostState = remember { SnackbarHostState() }

  // Listen for user messages (toasts/snackbars)
  LaunchedEffect(Unit) {
    viewModel.userMessage.collect { message ->
      snackbarHostState.showSnackbar(message)
    }
  }

  // Handle system back navigation
  val canGoBack = currentScreen !is Screen.Home
  BackHandler(enabled = canGoBack) {
    viewModel.navigateBack()
  }

  val totalCartCount = cartItems.sumOf { it.quantity }
  val wishlistCount = wishlist.size

  // Only show bottom navigation on main tabs
  val showBottomBar = currentScreen is Screen.Home ||
      currentScreen is Screen.Wishlist ||
      currentScreen is Screen.Cart ||
      currentScreen is Screen.Orders ||
      currentScreen is Screen.Profile

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    snackbarHost = { SnackbarHost(snackbarHostState) },
    bottomBar = {
      if (showBottomBar) {
        NavigationBar(
          containerColor = MaterialTheme.colorScheme.surface,
          tonalElevation = 8.dp,
          modifier = Modifier.testTag("bottom_nav_bar")
        ) {
          // 1. Shop
          NavigationBarItem(
            selected = currentScreen is Screen.Home,
            onClick = { viewModel.navigateTo(Screen.Home) },
            icon = {
              Icon(
                imageVector = if (currentScreen is Screen.Home) Icons.Filled.Storefront else Icons.Outlined.Storefront,
                contentDescription = "Shop"
              )
            },
            label = { Text("Shop", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
          )

          // 2. Wishlist
          NavigationBarItem(
            selected = currentScreen is Screen.Wishlist,
            onClick = { viewModel.navigateTo(Screen.Wishlist) },
            icon = {
              BadgedBox(
                badge = {
                  if (wishlistCount > 0) {
                    Badge(
                      containerColor = MaterialTheme.colorScheme.secondary,
                      contentColor = androidx.compose.ui.graphics.Color.White
                    ) {
                      Text("$wishlistCount")
                    }
                  }
                }
              ) {
                Icon(
                  imageVector = if (currentScreen is Screen.Wishlist) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                  contentDescription = "Wishlist"
                )
              }
            },
            label = { Text("Wishlist", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
          )

          // 3. Cart
          NavigationBarItem(
            selected = currentScreen is Screen.Cart,
            onClick = { viewModel.navigateTo(Screen.Cart) },
            icon = {
              BadgedBox(
                badge = {
                  if (totalCartCount > 0) {
                    Badge(
                      containerColor = MaterialTheme.colorScheme.primary,
                      contentColor = androidx.compose.ui.graphics.Color.White
                    ) {
                      Text("$totalCartCount")
                    }
                  }
                }
              ) {
                Icon(
                  imageVector = if (currentScreen is Screen.Cart) Icons.Filled.ShoppingBag else Icons.Outlined.ShoppingBag,
                  contentDescription = "Bag"
                )
              }
            },
            label = { Text("Bag", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
          )

          // 4. Orders
          NavigationBarItem(
            selected = currentScreen is Screen.Orders,
            onClick = { viewModel.navigateTo(Screen.Orders) },
            icon = {
              Icon(
                imageVector = if (currentScreen is Screen.Orders) Icons.Filled.ReceiptLong else Icons.Outlined.ReceiptLong,
                contentDescription = "Orders"
              )
            },
            label = { Text("Orders", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
          )

          // 5. Profile
          NavigationBarItem(
            selected = currentScreen is Screen.Profile,
            onClick = { viewModel.navigateTo(Screen.Profile) },
            icon = {
              Icon(
                imageVector = if (currentScreen is Screen.Profile) Icons.Filled.Person else Icons.Outlined.Person,
                contentDescription = "Account"
              )
            },
            label = { Text("Account", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
        when (screen) {
          is Screen.Home -> HomeScreen(viewModel = viewModel)
          is Screen.Detail -> ProductDetailScreen(
            viewModel = viewModel,
            onBackClick = { viewModel.navigateBack() }
          )
          is Screen.Cart -> CartScreen(viewModel = viewModel)
          is Screen.Checkout -> CheckoutScreen(
            viewModel = viewModel,
            onBackClick = { viewModel.navigateBack() }
          )
          is Screen.Orders -> OrdersScreen(viewModel = viewModel)
          is Screen.OrderDetail -> OrderDetailScreen(
            viewModel = viewModel,
            onBackClick = { viewModel.navigateBack() }
          )
          is Screen.Wishlist -> WishlistScreen(viewModel = viewModel)
          is Screen.Profile -> ProfileScreen(viewModel = viewModel)
        }
      }
    }
  }
}
