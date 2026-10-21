package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Product
import com.example.model.ProductCategory
import com.example.ui.components.FilterBottomSheet
import com.example.ui.components.ProductCard
import com.example.viewmodel.StoreViewModel

@Composable
fun HomeScreen(
  viewModel: StoreViewModel,
  modifier: Modifier = Modifier
) {
  val products by viewModel.filteredProducts.collectAsState()
  val filterState by viewModel.filterState.collectAsState()
  val wishlist by viewModel.wishlist.collectAsState()
  val allProducts by viewModel.products.collectAsState()

  var showFilterSheet by remember { mutableStateOf(false) }

  val flashDeals = remember(allProducts) {
    allProducts.filter { it.isFlashDeal }
  }

  if (showFilterSheet) {
    FilterBottomSheet(
      filterState = filterState,
      onApplyFilters = { newFilters ->
        viewModel.setSortOption(newFilters.sortOption)
        viewModel.setPriceFilter(newFilters.maxPrice)
      },
      onDismiss = { showFilterSheet = false }
    )
  }

  val context = LocalContext.current
  val whatsappNumber = "233203216862"

  fun openWhatsApp() {
    try {
      context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$whatsappNumber")))
    } catch (_: Exception) {}
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Top Brand Header & Search Bar
    Surface(
      color = MaterialTheme.colorScheme.surface,
      shadowElevation = 2.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp)
      ) {
        // App Title Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(36.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.ShoppingBag,
                  contentDescription = "Daily Shop",
                  tint = Color.White,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "DAILY SHOP",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Fresh Deals Every Day",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            // WhatsApp Quick Action
            IconButton(
              onClick = { openWhatsApp() },
              modifier = Modifier.testTag("home_whatsapp_btn")
            ) {
              Surface(
                shape = CircleShape,
                color = Color(0xFF25D366),
                modifier = Modifier.size(36.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "WhatsApp Daily Shop",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Filter Button with active indicator
            IconButton(
              onClick = { showFilterSheet = true },
              modifier = Modifier.testTag("filter_btn")
            ) {
              Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(36.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filters",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Input
        OutlinedTextField(
          value = filterState.searchQuery,
          onValueChange = { viewModel.setSearchQuery(it) },
          placeholder = { Text("Search products, brands, tags...") },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Search",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          },
          trailingIcon = {
            if (filterState.searchQuery.isNotEmpty()) {
              IconButton(onClick = { viewModel.setSearchQuery("") }) {
                Icon(Icons.Default.Clear, contentDescription = "Clear search")
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.Transparent
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("home_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Pills
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
          items(ProductCategory.entries) { category ->
            val isSelected = filterState.selectedCategory == category
            FilterChip(
              selected = isSelected,
              onClick = { viewModel.setCategory(category) },
              label = {
                Text(
                  text = category.displayName,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = Color.White
              ),
              shape = RoundedCornerShape(20.dp)
            )
          }
        }
      }
    }

    // Main Scrollable Feed
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .testTag("product_feed_list"),
      contentPadding = PaddingValues(bottom = 90.dp)
    ) {
      // Show Hero Banner & Flash Deals only when not actively searching
      if (filterState.searchQuery.isBlank() && filterState.selectedCategory == ProductCategory.ALL) {
        // Hero Promotion Banner
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
            ) {
              Image(
                painter = painterResource(id = R.drawable.img_hero_banner),
                contentDescription = "Spring Drop Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
              )
              // Gradient Overlay for contrast
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(
                    Brush.horizontalGradient(
                      colors = listOf(
                        Color.Black.copy(alpha = 0.85f),
                        Color.Black.copy(alpha = 0.35f),
                        Color.Transparent
                      )
                    )
                  )
              )

              Column(
                modifier = Modifier
                  .padding(20.dp)
                  .align(Alignment.CenterStart)
              ) {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = MaterialTheme.colorScheme.secondary
                ) {
                  Text(
                    text = "LIMITED SEASON DROP",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    letterSpacing = 1.sp
                  )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                  text = "20% OFF ALL AUDIO\n& WEARABLES",
                  color = Color.White,
                  style = MaterialTheme.typography.titleLarge,
                  fontWeight = FontWeight.ExtraBold,
                  lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                  text = "Use coupon DAILY20 at checkout",
                  color = Color.White.copy(alpha = 0.9f),
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }
        }

        // Flash Deals Section
        if (flashDeals.isNotEmpty()) {
          item {
            Column(
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(24.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Flash Deals",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                  )
                }

                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                  Text(
                    text = "Ends in 04:32:18",
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }

              LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
              ) {
                items(flashDeals) { deal ->
                  ProductCard(
                    product = deal,
                    isWishlisted = wishlist.contains(deal.id),
                    onProductClick = { viewModel.openProductDetail(it) },
                    onWishlistToggle = { viewModel.toggleWishlist(it) },
                    onQuickAdd = { viewModel.quickAddToCart(it) },
                    modifier = Modifier.width(200.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(16.dp))
            }
          }
        }
      }

      // Products Section Header
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (filterState.searchQuery.isNotBlank()) "Search Results (${products.size})"
            else if (filterState.selectedCategory != ProductCategory.ALL) "${filterState.selectedCategory.displayName} (${products.size})"
            else "Trending Products",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )

          if (filterState.searchQuery.isNotBlank() || filterState.selectedCategory != ProductCategory.ALL) {
            Text(
              text = "Clear",
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.SemiBold,
              fontSize = 13.sp,
              modifier = Modifier
                .clickable { viewModel.resetFilters() }
                .padding(4.dp)
            )
          }
        }
      }

      // Products Grid (2 columns)
      if (products.isEmpty()) {
        item {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
              modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
              text = "No products found",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Try adjusting your filters or search keywords.",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
              onClick = { viewModel.resetFilters() },
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
              Text("Reset All Filters")
            }
          }
        }
      } else {
        // Chunk items into pairs for a 2-column layout
        val chunkedProducts = products.chunked(2)
        items(chunkedProducts) { pair ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            ProductCard(
              product = pair[0],
              isWishlisted = wishlist.contains(pair[0].id),
              onProductClick = { viewModel.openProductDetail(it) },
              onWishlistToggle = { viewModel.toggleWishlist(it) },
              onQuickAdd = { viewModel.quickAddToCart(it) },
              modifier = Modifier.weight(1f)
            )

            if (pair.size > 1) {
              ProductCard(
                product = pair[1],
                isWishlisted = wishlist.contains(pair[1].id),
                onProductClick = { viewModel.openProductDetail(it) },
                onWishlistToggle = { viewModel.toggleWishlist(it) },
                onQuickAdd = { viewModel.quickAddToCart(it) },
                modifier = Modifier.weight(1f)
              )
            } else {
              Spacer(modifier = Modifier.weight(1f))
            }
          }
        }
      }
    }
  }
}
