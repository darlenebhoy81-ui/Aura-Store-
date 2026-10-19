package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.OrderStatus
import com.example.viewmodel.StoreViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
  viewModel: StoreViewModel,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val order by viewModel.selectedOrder.collectAsState()
  val clipboardManager: ClipboardManager = LocalClipboardManager.current

  if (order == null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("Order not found")
    }
    return
  }

  val currentOrder = order!!

  val dateFormatted = remember(currentOrder.createdAt) {
    val sdf = SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
    sdf.format(Date(currentOrder.createdAt))
  }

  val trackingSteps = listOf(
    OrderStatus.PLACED to "Order Confirmed & Placed",
    OrderStatus.PROCESSING to "Packed at Aura Warehouse",
    OrderStatus.SHIPPED to "In Transit with Carrier",
    OrderStatus.OUT_FOR_DELIVERY to "Out for Final Delivery",
    OrderStatus.DELIVERED to "Package Delivered"
  )

  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Track ${currentOrder.id}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
        },
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Live Tracking Stepper Card
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("order_tracking_card"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Live Tracking",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer
              ) {
                Text(
                  text = currentOrder.status.title,
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tracking number row with copy action
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Icon(Icons.Default.LocalShipping, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = currentOrder.trackingNumber,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
              Spacer(modifier = Modifier.weight(1f))
              IconButton(
                onClick = { clipboardManager.setText(AnnotatedString(currentOrder.trackingNumber)) },
                modifier = Modifier.size(24.dp)
              ) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy tracking number", modifier = Modifier.size(14.dp))
              }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Timeline steps
            trackingSteps.forEachIndexed { index, (stepStatus, description) ->
              val isCompleted = stepStatus.stepIndex <= currentOrder.status.stepIndex
              val isCurrent = stepStatus == currentOrder.status

              Row(modifier = Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Surface(
                    shape = CircleShape,
                    color = when {
                      isCompleted -> MaterialTheme.colorScheme.primary
                      else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    modifier = Modifier.size(24.dp)
                  ) {
                    Box(contentAlignment = Alignment.Center) {
                      if (isCompleted) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                      }
                    }
                  }

                  if (index < trackingSteps.size - 1) {
                    Box(
                      modifier = Modifier
                        .width(2.dp)
                        .height(32.dp)
                        .background(
                          if (stepStatus.stepIndex < currentOrder.status.stepIndex) MaterialTheme.colorScheme.primary
                          else MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                  }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                  Text(
                    text = stepStatus.title,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (isCompleted) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                  )
                  Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                  )
                  Spacer(modifier = Modifier.height(14.dp))
                }
              }
            }
          }
        }
      }

      // Shipping Address Card
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Place, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Delivery Destination", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(currentOrder.shippingAddress.fullName, fontWeight = FontWeight.SemiBold)
            Text(
              "${currentOrder.shippingAddress.streetAddress}, ${currentOrder.shippingAddress.city}, ${currentOrder.shippingAddress.state} ${currentOrder.shippingAddress.postalCode}",
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              style = MaterialTheme.typography.bodyMedium
            )
            Text(currentOrder.shippingAddress.phoneNumber, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
          }
        }
      }

      // Ordered Items List
      item {
        Text("Purchased Items (${currentOrder.items.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
      }

      items(currentOrder.items) { item ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            AsyncImage(
              model = item.product.imageUrl,
              contentDescription = item.product.title,
              contentScale = ContentScale.Crop,
              modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(item.product.title, fontWeight = FontWeight.Bold, maxLines = 1, fontSize = 14.sp)
              Text("Qty: ${item.quantity} • $${"%.2f".format(item.product.price)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("$${"%.2f".format(item.totalPrice)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
          }
        }
      }

      // Payment Summary
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text("Payment Receipt", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Subtotal", color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text("$${"%.2f".format(currentOrder.subtotal)}", fontWeight = FontWeight.SemiBold)
            }
            if (currentOrder.discount > 0.0) {
              Spacer(modifier = Modifier.height(6.dp))
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Discount (${currentOrder.couponCode ?: "PROMO"})", color = Color(0xFF16A34A))
                Text("-$${"%.2f".format(currentOrder.discount)}", color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
              }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Shipping (${currentOrder.shippingMethod.title})", color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text(if (currentOrder.shippingCost == 0.0) "FREE" else "$${"%.2f".format(currentOrder.shippingCost)}", fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Tax", color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text("$${"%.2f".format(currentOrder.tax)}", fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Total Paid", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
              Text("$${"%.2f".format(currentOrder.total)}", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
            }
          }
        }
      }
    }
  }
}
