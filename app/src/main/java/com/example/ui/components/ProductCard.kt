package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Product

@Composable
fun ProductCard(
  product: Product,
  isWishlisted: Boolean,
  onProductClick: (Product) -> Unit,
  onWishlistToggle: (Product) -> Unit,
  onQuickAdd: (Product) -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("product_card_${product.id}")
      .clickable { onProductClick(product) },
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column {
      // Product Image with badges and wishlist button
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(180.dp)
          .background(MaterialTheme.colorScheme.surfaceVariant)
      ) {
        AsyncImage(
          model = product.imageUrl,
          contentDescription = product.title,
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
        )

        // Discount or Flash Deal Badge
        if (product.discountPercent != null) {
          Surface(
            modifier = Modifier
              .padding(8.dp)
              .align(Alignment.TopStart),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.secondary
          ) {
            Text(
              text = "-${product.discountPercent}%",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        } else if (product.isTrending) {
          Surface(
            modifier = Modifier
              .padding(8.dp)
              .align(Alignment.TopStart),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primary
          ) {
            Text(
              text = "POPULAR",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
            )
          }
        }

        // Wishlist Button
        Surface(
          modifier = Modifier
            .padding(8.dp)
            .size(36.dp)
            .align(Alignment.TopEnd),
          shape = CircleShape,
          color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
          shadowElevation = 2.dp
        ) {
          IconButton(
            onClick = { onWishlistToggle(product) },
            modifier = Modifier.testTag("wishlist_btn_${product.id}")
          ) {
            Icon(
              imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
              contentDescription = if (isWishlisted) "Remove from wishlist" else "Add to wishlist",
              tint = if (isWishlisted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      // Product Details
      Column(
        modifier = Modifier.padding(12.dp)
      ) {
        // Brand & Category
        Text(
          text = product.brand.uppercase(),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.SemiBold,
          letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Title
        Text(
          text = product.title,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          color = MaterialTheme.colorScheme.onSurface,
          fontSize = 14.sp,
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Rating
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Rating",
            tint = Color(0xFFFBBF24),
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${product.rating}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "(${product.reviewCount})",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Price and Quick Add
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "$${"%.2f".format(product.price)}",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            if (product.originalPrice != null && product.originalPrice > product.price) {
              Text(
                text = "$${"%.2f".format(product.originalPrice)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textDecoration = TextDecoration.LineThrough,
                fontSize = 12.sp
              )
            }
          }

          // Quick Add to Cart button
          Surface(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .clickable { onQuickAdd(product) }
              .testTag("quick_add_${product.id}"),
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = CircleShape
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.AddShoppingCart,
                contentDescription = "Quick add to cart",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }
    }
  }
}
