package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
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
  val context = LocalContext.current
  val savedAddresses by viewModel.savedAddresses.collectAsState()
  var showSupportDialog by remember { mutableStateOf(false) }
  var showAddressesDialog by remember { mutableStateOf(false) }
  var notificationsEnabled by remember { mutableStateOf(true) }

  // Contact details provided by user
  val customerEmail = "darlenebhoy81@gmail.com"
  val customerPhone = "0203216862"
  val whatsappNumber = "233203216862"

  fun launchCall() {
    try {
      val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$customerPhone"))
      context.startActivity(intent)
    } catch (_: Exception) {}
  }

  fun launchWhatsApp() {
    try {
      val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$whatsappNumber"))
      context.startActivity(intent)
    } catch (_: Exception) {}
  }

  fun launchEmail() {
    try {
      val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$customerEmail")).apply {
        putExtra(Intent.EXTRA_SUBJECT, "Daily Shop Customer Inquiry")
      }
      context.startActivity(intent)
    } catch (_: Exception) {}
  }

  if (showSupportDialog) {
    AlertDialog(
      onDismissRequest = { showSupportDialog = false },
      title = { Text("Daily Shop Support", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "We are here to assist you with inquiries, orders, and returns.",
            style = MaterialTheme.typography.bodyMedium
          )

          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
          ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Call: $customerPhone", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
              }
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Chat, contentDescription = null, tint = Color(0xFF25D366), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("WhatsApp: +$whatsappNumber", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
              }
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Email: $customerEmail", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
              }
            }
          }

          Text(
            text = "Fast delivery, verified products, and 30-day money-back guarantee.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            showSupportDialog = false
            launchWhatsApp()
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
        ) {
          Text("WhatsApp Us", color = Color.White)
        }
      },
      dismissButton = {
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
          Text("Daily Shop Account", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
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
                text = "Daily Shop Member",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = customerEmail,
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

      // Quick Customer Support Contact Hub (WhatsApp & Phone)
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("customer_contact_card"),
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
                text = "Customer Support",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFDCFCE7)
              ) {
                Text(
                  text = "AVAILABLE NOW",
                  color = Color(0xFF166534),
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "Have questions or want to place an order? Contact Daily Shop directly:",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Contact Action Buttons
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              // WhatsApp Chat Button
              Button(
                onClick = { launchWhatsApp() },
                modifier = Modifier
                  .weight(1f)
                  .height(46.dp)
                  .testTag("whatsapp_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                shape = RoundedCornerShape(12.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Chat,
                  contentDescription = "WhatsApp",
                  tint = Color.White,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("WhatsApp", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
              }

              // Call Button
              Button(
                onClick = { launchCall() },
                modifier = Modifier
                  .weight(1f)
                  .height(46.dp)
                  .testTag("call_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Call,
                  contentDescription = "Call",
                  tint = Color.White,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Call Shop", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Email Button
            OutlinedButton(
              onClick = { launchEmail() },
              modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("email_btn"),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Email",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(customerEmail, fontSize = 13.sp, fontWeight = FontWeight.Medium)
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
                  text = "DAILY SHOP REWARDS",
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
              subtitle = "Call $customerPhone or WhatsApp +$whatsappNumber",
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
                Text("Stay updated on daily flash discounts", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
            text = "Daily Shop v1.0 • Built with Jetpack Compose",
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
