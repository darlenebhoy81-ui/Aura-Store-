package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.viewmodel.Screen
import com.example.viewmodel.StoreViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
  viewModel: StoreViewModel,
  modifier: Modifier = Modifier
) {
  val orders by viewModel.orders.collectAsState()

  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "My Orders",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
          )
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    }
  ) { innerPadding ->
    if (orders.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(80.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.ReceiptLong,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
              )
            }
          }
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = "No Orders Yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "When you place orders, they will appear here with live tracking.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(20.dp))
          Button(onClick = { viewModel.navigateTo(Screen.Home) }) {
            Text("Start Shopping")
          }
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        items(orders, key = { it.id }) { order ->
          OrderCard(
            order = order,
            onClick = { viewModel.viewOrderDetail(order) },
            onTrackClick = { viewModel.viewOrderDetail(order) }
          )
        }
      }
    }
  }
}

@Composable
fun OrderCard(
  order: Order,
  onClick: () -> Unit,
  onTrackClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val dateFormatted = remember(order.createdAt) {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    sdf.format(Date(order.createdAt))
  }

  val statusBgColor = when (order.status) {
    OrderStatus.DELIVERED -> Color(0xFFDCFCE7)
    OrderStatus.SHIPPED, OrderStatus.OUT_FOR_DELIVERY -> Color(0xFFE0E7FF)
    OrderStatus.PROCESSING -> Color(0xFFFEF3C7)
    OrderStatus.PLACED -> Color(0xFFF3F4F6)
    OrderStatus.CANCELLED -> Color(0xFFFFE4E6)
  }

  val statusTextColor = when (order.status) {
    OrderStatus.DELIVERED -> Color(0xFF166534)
    OrderStatus.SHIPPED, OrderStatus.OUT_FOR_DELIVERY -> Color(0xFF3730A3)
    OrderStatus.PROCESSING -> Color(0xFF92400E)
    OrderStatus.PLACED -> Color(0xFF374151)
    OrderStatus.CANCELLED -> Color(0xFF9F1239)
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag("order_item_${order.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header: Order ID & Status
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = order.id,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = dateFormatted,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = statusBgColor
        ) {
          Text(
            text = order.status.title.uppercase(),
            color = statusTextColor,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Thumbnail row of purchased products
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        order.items.take(3).forEach { item ->
          AsyncImage(
            model = item.product.imageUrl,
            contentDescription = item.product.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .size(60.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant)
          )
        }
        if (order.items.size > 3) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(60.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = "+${order.items.size - 3}",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))
      HorizontalDivider()
      Spacer(modifier = Modifier.height(12.dp))

      // Total and Track Action
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "${order.items.sumOf { it.quantity }} items",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "$${"%.2f".format(order.total)}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        OutlinedButton(
          onClick = onTrackClick,
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Track Order")
        }
      }
    }
  }
}
