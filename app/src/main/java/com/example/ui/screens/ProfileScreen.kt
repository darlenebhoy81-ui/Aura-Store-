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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.Screen
import com.example.viewmodel.StoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
  viewModel: StoreViewModel,
  modifier: Modifier = Modifier
) {
  val savedAddresses by viewModel.savedAddresses.collectAsState()
  var showSupportDialog by remember { mutableStateOf(false) }
  var showAddressesDialog by remember { mutableStateOf(false) }
  var notificationsEnabled by remember { mutableStateOf(true) }

  if (showSupportDialog) {
    AlertDialog(
      onDismissRequest = { showSupportDialog = false },
      title = { Text("Customer Support & Help", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Need help with an order, return, or warranty claim?", style = MaterialTheme.typography.bodyMedium)
          Spacer(modifier = Modifier.height(4.dp))
          Text("• Email: support@aurastore.com", fontWeight = FontWeight.SemiBold)
          Text("• Toll-Free: 1-800-AURA-SHOP", fontWeight = FontWeight.SemiBold)
          Text("• Live Chat: Mon-Fri 8am - 8pm PST", fontWeight = FontWeight.SemiBold)
          Text("• Return Policy: 30-day hassle-free money-back guarantee.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      },
      confirmButton = {
        TextButton(onClick = { showSupportDialog = false }) {
          Text("Close")
        }
      }
    )
  }

  if (showAddressesDialog) {
    AlertDialog(
      onDismissRequest = { showAddressesDialog = false },
      title = { Text("Saved Addresses", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          savedAddresses.forEach { addr ->
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text("${addr.label} • ${addr.fullName}", fontWeight = FontWeight.Bold)
                Text("${addr.streetAddress}, ${addr.city}, ${addr.state}", style = MaterialTheme.typography.bodySmall)
                Text(addr.phoneNumber, style = MaterialTheme.typography.bodySmall)
              }
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showAddressesDialog = false }) {
          Text("Close")
        }
      }
    )
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {
          Text("My Account", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
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
      // User Profile Header
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(64.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Person,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(36.dp)
                )
              }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
              Text(
                text = "Alex Morgan",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "alex.morgan@example.com",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(6.dp))
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFFFEF3C7)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFD97706),
                    modifier = Modifier.size(12.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "GOLD VIP MEMBER",
                    color = Color(0xFF92400E),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                  )
                }
              }
            }
          }
        }
      }

      // Rewards Banner
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                Brush.horizontalGradient(
                  listOf(
                    Color(0xFF312E81),
                    Color(0xFF4338CA),
                    Color(0xFF6366F1)
                  )
                )
              )
              .padding(18.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "AURA REWARDS",
                  color = Color.White.copy(alpha = 0.8f),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                )
                Text(
                  text = "420 Points",
                  color = Color.White,
                  style = MaterialTheme.typography.headlineMedium,
                  fontWeight = FontWeight.ExtraBold
                )
                Text(
                  text = "$20 in store discount available",
                  color = Color.White.copy(alpha = 0.9f),
                  fontSize = 12.sp
                )
              }

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.secondary
              ) {
                Text(
                  text = "Redeem",
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
              }
            }
          }
        }
      }

      // Settings & Features
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
          Column {
            ProfileMenuRow(
              icon = Icons.Default.ReceiptLong,
              title = "Order History",
              subtitle = "View past purchases and shipments",
              onClick = { viewModel.navigateTo(Screen.Orders) }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            ProfileMenuRow(
              icon = Icons.Default.Place,
              title = "Saved Addresses",
              subtitle = "${savedAddresses.size} addresses saved",
              onClick = { showAddressesDialog = true }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            ProfileMenuRow(
              icon = Icons.Default.CreditCard,
              title = "Payment Methods",
              subtitle = "Credit Card (•••• 4242), Google Pay",
              onClick = { viewModel.navigateTo(Screen.Cart) }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            ProfileMenuRow(
              icon = Icons.AutoMirrored.Filled.HelpOutline,
              title = "Help & Customer Support",
              subtitle = "FAQs, returns, and live chat",
              onClick = { showSupportDialog = true }
            )
          }
        }
      }

      // Notification toggle
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              Spacer(modifier = Modifier.width(14.dp))
              Column {
                Text("Order & Deal Alerts", fontWeight = FontWeight.SemiBold)
                Text("Stay updated on flash discounts", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
            }
            Switch(
              checked = notificationsEnabled,
              onCheckedChange = { notificationsEnabled = it }
            )
          }
        }
      }

      // App version & information
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "Aura Store v1.0 • Built with Jetpack Compose",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}

@Composable
fun ProfileMenuRow(
  icon: ImageVector,
  title: String,
  subtitle: String,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Surface(
      shape = CircleShape,
      color = MaterialTheme.colorScheme.surfaceVariant,
      modifier = Modifier.size(40.dp)
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
      }
    }
    Spacer(modifier = Modifier.width(14.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
      Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
  }
}
