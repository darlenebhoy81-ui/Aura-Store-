package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.WriteReviewDialog
import com.example.viewmodel.StoreViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProductDetailScreen(
  viewModel: StoreViewModel,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val product = viewModel.selectedProduct.collectAsState().value
  val selectedColor by viewModel.selectedColor.collectAsState()
  val selectedSize by viewModel.selectedSize.collectAsState()
  val quantity by viewModel.detailQuantity.collectAsState()
  val wishlist by viewModel.wishlist.collectAsState()

  var showReviewDialog by remember { mutableStateOf(false) }

  if (product == null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("Product not found")
    }
    return
  }

  val isWishlisted = wishlist.contains(product.id)

  if (showReviewDialog) {
    WriteReviewDialog(
      productTitle = product.title,
      onDismiss = { showReviewDialog = false },
      onSubmit = { rating, comment, name ->
        viewModel.submitReview(product.id, rating, comment, name)
      }
    )
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = product.brand,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
        },
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(onClick = { viewModel.toggleWishlist(product) }) {
            Icon(
              imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
              contentDescription = "Toggle wishlist",
              tint = if (isWishlisted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    bottomBar = {
      // Sticky Purchase Bar
      Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        modifier = Modifier
          .fillMaxWidth()
          .navigationBarsPadding()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedButton(
            onClick = { viewModel.addCurrentToCart(andCheckout = false) },
            modifier = Modifier
              .weight(1f)
              .height(50.dp)
              .testTag("detail_add_to_cart_btn"),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(
              imageVector = Icons.Default.ShoppingBag,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add to Bag", fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = { viewModel.addCurrentToCart(andCheckout = true) },
            modifier = Modifier
              .weight(1f)
              .height(50.dp)
              .testTag("detail_buy_now_btn"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.secondary
            )
          ) {
            Text("Buy Now", fontWeight = FontWeight.Bold, color = Color.White)
          }
        }
      }
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentPadding = PaddingValues(bottom = 24.dp)
    ) {
      // Hero Product Image
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
          AsyncImage(
            model = product.imageUrl,
            contentDescription = product.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )

          if (product.discountPercent != null) {
            Surface(
              modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart),
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.secondary
            ) {
              Text(
                text = "SAVE ${product.discountPercent}%",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
              )
            }
          }
        }
      }

      // Title & Price Section
      item {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = product.category.displayName.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = product.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Rating & Reviews row
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFFFEF3C7)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Star,
                  contentDescription = null,
                  tint = Color(0xFFD97706),
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "${product.rating}",
                  color = Color(0xFF92400E),
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
              }
            }

            Text(
              text = "${product.reviewCount} customer reviews",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.weight(1f))

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (product.stockQuantity > 5) Color(0xFFDCFCE7) else Color(0xFFFFE4E6)
            ) {
              Text(
                text = if (product.stockQuantity > 5) "In Stock (${product.stockQuantity})" else "Low Stock (${product.stockQuantity} left)",
                color = if (product.stockQuantity > 5) Color(0xFF166534) else Color(0xFF9F1239),
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Price Row
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text(
              text = "$${"%.2f".format(product.price)}",
              style = MaterialTheme.typography.headlineMedium,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.onSurface
            )
            if (product.originalPrice != null && product.originalPrice > product.price) {
              Text(
                text = "$${"%.2f".format(product.originalPrice)}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textDecoration = TextDecoration.LineThrough
              )
            }
          }
        }
      }

      item {
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
      }

      // Color Selection (if available)
      if (product.colors.isNotEmpty()) {
        item {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "Color: ${selectedColor?.name ?: "Choose Color"}",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
              product.colors.forEach { color ->
                val isSelected = selectedColor?.name == color.name
                Box(
                  modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(color.hexValue))
                    .border(
                      width = if (isSelected) 3.dp else 1.dp,
                      color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                      shape = CircleShape
                    )
                    .clickable { viewModel.setSelectedColor(color) }
                    .testTag("color_${color.name}"),
                  contentAlignment = Alignment.Center
                ) {
                  if (isSelected) {
                    Icon(
                      imageVector = Icons.Default.Check,
                      contentDescription = "Selected color",
                      tint = if (color.hexValue > 0xFF888888) Color.Black else Color.White,
                      modifier = Modifier.size(20.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }

      // Size Selection (if available)
      if (product.sizes.isNotEmpty()) {
        item {
          Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(
              text = "Size: ${selectedSize ?: "Choose Size"}",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(10.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              product.sizes.forEach { size ->
                val isSelected = selectedSize == size
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                  modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { viewModel.setSelectedSize(size) }
                    .testTag("size_$size")
                ) {
                  Text(
                    text = size,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                  )
                }
              }
            }
          }
        }
      }

      // Quantity Selector
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Quantity",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
          )

          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
              .padding(4.dp)
          ) {
            IconButton(
              onClick = { viewModel.setDetailQuantity(quantity - 1) },
              modifier = Modifier.size(36.dp)
            ) {
              Icon(Icons.Default.Remove, contentDescription = "Decrease quantity")
            }
            Text(
              text = "$quantity",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 16.dp)
            )
            IconButton(
              onClick = { viewModel.setDetailQuantity(quantity + 1) },
              modifier = Modifier.size(36.dp)
            ) {
              Icon(Icons.Default.Add, contentDescription = "Increase quantity")
            }
          }
        }
      }

      item {
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
      }

      // Key Features Checklist
      item {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Highlights & Features",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(12.dp))
          product.features.forEach { feature ->
            Row(
              modifier = Modifier.padding(vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = feature,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }

      // Description
      item {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
          Text(
            text = "Overview",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = product.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
          )
        }
      }

      // Customer Reviews Header + Write Review Button
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Customer Reviews",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
          )
          OutlinedButton(
            onClick = { showReviewDialog = true },
            modifier = Modifier.testTag("write_review_btn"),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Write Review")
          }
        }
      }

      // Reviews List
      if (product.reviews.isEmpty()) {
        item {
          Text(
            text = "No reviews yet. Be the first to share your thoughts!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
          )
        }
      } else {
        items(product.reviews) { review ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 6.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = review.userName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                  )
                  if (review.verifiedPurchase) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                      imageVector = Icons.Default.Verified,
                      contentDescription = "Verified buyer",
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(14.dp)
                    )
                  }
                }
                Text(
                  text = review.date,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 11.sp
                )
              }

              Spacer(modifier = Modifier.height(4.dp))

              // Rating stars
              Row {
                (1..5).forEach { star ->
                  Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = if (star <= review.rating) Color(0xFFFBBF24) else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(14.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }
    }
  }
}
