package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PaymentDetails
import com.example.model.PaymentType
import com.example.model.ShippingAddress
import com.example.model.ShippingMethod
import com.example.viewmodel.CheckoutStep
import com.example.viewmodel.Screen
import com.example.viewmodel.StoreViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
  viewModel: StoreViewModel,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val step by viewModel.checkoutStep.collectAsState()
  val savedAddresses by viewModel.savedAddresses.collectAsState()
  val selectedAddress by viewModel.selectedAddress.collectAsState()
  val shippingMethod by viewModel.selectedShippingMethod.collectAsState()
  val paymentDetails by viewModel.paymentDetails.collectAsState()
  val cartItems by viewModel.cart.collectAsState()
  val subtotal by viewModel.cartSubtotal.collectAsState()
  val discount by viewModel.couponDiscount.collectAsState()
  val appliedCoupon by viewModel.appliedCoupon.collectAsState()
  val lastOrder by viewModel.lastPlacedOrder.collectAsState()

  val shippingCost = if (subtotal >= 75.0 && shippingMethod == ShippingMethod.STANDARD) 0.0 else shippingMethod.cost
  val tax = (subtotal - discount).coerceAtLeast(0.0) * 0.08
  val total = (subtotal - discount + shippingCost + tax).coerceAtLeast(0.0)

  // Address manual form fields
  var fullName by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var street by remember { mutableStateOf("") }
  var city by remember { mutableStateOf("") }
  var postalCode by remember { mutableStateOf("") }
  var showNewAddressForm by remember { mutableStateOf(false) }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = if (step == CheckoutStep.SUCCESS) "Order Confirmation" else "Checkout",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
          )
        },
        navigationIcon = {
          if (step != CheckoutStep.SUCCESS) {
            IconButton(onClick = {
              when (step) {
                CheckoutStep.ADDRESS -> onBackClick()
                CheckoutStep.SHIPPING -> viewModel.setCheckoutStep(CheckoutStep.ADDRESS)
                CheckoutStep.PAYMENT -> viewModel.setCheckoutStep(CheckoutStep.SHIPPING)
                CheckoutStep.REVIEW -> viewModel.setCheckoutStep(CheckoutStep.PAYMENT)
                CheckoutStep.SUCCESS -> onBackClick()
              }
            }) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    },
    bottomBar = {
      if (step != CheckoutStep.SUCCESS) {
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
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Total Amount",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = "$${"%.2f".format(total)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
              )
            }

            Button(
              onClick = {
                when (step) {
                  CheckoutStep.ADDRESS -> viewModel.setCheckoutStep(CheckoutStep.SHIPPING)
                  CheckoutStep.SHIPPING -> viewModel.setCheckoutStep(CheckoutStep.PAYMENT)
                  CheckoutStep.PAYMENT -> viewModel.setCheckoutStep(CheckoutStep.REVIEW)
                  CheckoutStep.REVIEW -> viewModel.placeOrder()
                  CheckoutStep.SUCCESS -> viewModel.navigateTo(Screen.Home)
                }
              },
              modifier = Modifier
                .height(50.dp)
                .testTag("checkout_next_btn"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = if (step == CheckoutStep.REVIEW) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
              )
            ) {
              Text(
                text = when (step) {
                  CheckoutStep.ADDRESS -> "Continue to Delivery"
                  CheckoutStep.SHIPPING -> "Continue to Payment"
                  CheckoutStep.PAYMENT -> "Review Order"
                  CheckoutStep.REVIEW -> "Place Order ($${"%.2f".format(total)})"
                  CheckoutStep.SUCCESS -> "Done"
                },
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
            }
          }
        }
      }
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Step Header
      if (step != CheckoutStep.SUCCESS) {
        item {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            CheckoutStep.entries.filter { it != CheckoutStep.SUCCESS }.forEach { s ->
              val isCompleted = s.stepNumber < step.stepNumber
              val isCurrent = s == step

              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                  if (isCompleted) viewModel.setCheckoutStep(s)
                }
              ) {
                Surface(
                  shape = CircleShape,
                  color = when {
                    isCompleted -> MaterialTheme.colorScheme.primary
                    isCurrent -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                  },
                  modifier = Modifier.size(32.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    if (isCompleted) {
                      Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                      )
                    } else {
                      Text(
                        text = "${s.stepNumber}",
                        fontWeight = FontWeight.Bold,
                        color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                      )
                    }
                  }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = s.title,
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                  color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 11.sp
                )
              }
            }
          }
          HorizontalDivider()
        }
      }

      // STEP 1: ADDRESS
      if (step == CheckoutStep.ADDRESS) {
        item {
          Text(
            text = "Select Delivery Address",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
        }

        items(savedAddresses) { addr ->
          val isSelected = selectedAddress?.id == addr.id
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { viewModel.selectAddress(addr) }
              .testTag("address_card_${addr.id}"),
            shape = RoundedCornerShape(14.dp),
            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
            colors = CardDefaults.cardColors(
              containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
            )
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              RadioButton(
                selected = isSelected,
                onClick = { viewModel.selectAddress(addr) }
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = addr.label,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                  )
                  if (addr.isDefault) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                      shape = RoundedCornerShape(4.dp),
                      color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                      Text(
                        text = "DEFAULT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = addr.fullName,
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.SemiBold
                )
                Text(
                  text = "${addr.streetAddress}, ${addr.city}, ${addr.state} ${addr.postalCode}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = addr.phoneNumber,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }

        item {
          OutlinedButton(
            onClick = { showNewAddressForm = !showNewAddressForm },
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(if (showNewAddressForm) "Cancel" else "+ Add New Address")
          }
        }

        if (showNewAddressForm) {
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(14.dp)
            ) {
              Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Text(
                  text = "New Shipping Address",
                  fontWeight = FontWeight.Bold,
                  style = MaterialTheme.typography.titleSmall
                )
                OutlinedTextField(
                  value = fullName,
                  onValueChange = { fullName = it },
                  label = { Text("Full Name") },
                  modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                  value = phone,
                  onValueChange = { phone = it },
                  label = { Text("Phone Number") },
                  modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                  value = street,
                  onValueChange = { street = it },
                  label = { Text("Street Address") },
                  modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City") },
                    modifier = Modifier.weight(1f)
                  )
                  OutlinedTextField(
                    value = postalCode,
                    onValueChange = { postalCode = it },
                    label = { Text("ZIP") },
                    modifier = Modifier.weight(1f)
                  )
                }
                Button(
                  onClick = {
                    if (fullName.isNotBlank() && street.isNotBlank()) {
                      val newAddr = ShippingAddress(
                        id = UUID.randomUUID().toString(),
                        label = "Other",
                        fullName = fullName,
                        phoneNumber = phone,
                        streetAddress = street,
                        city = city,
                        state = "CA",
                        postalCode = postalCode
                      )
                      viewModel.selectAddress(newAddr)
                      showNewAddressForm = false
                    }
                  },
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Text("Use This Address")
                }
              }
            }
          }
        }
      }

      // STEP 2: SHIPPING METHOD
      if (step == CheckoutStep.SHIPPING) {
        item {
          Text(
            text = "Choose Delivery Speed",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
        }

        items(ShippingMethod.entries) { method ->
          val isSelected = shippingMethod == method
          val cost = if (subtotal >= 75.0 && method == ShippingMethod.STANDARD) 0.0 else method.cost

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { viewModel.setShippingMethod(method) }
              .testTag("shipping_method_${method.name}"),
            shape = RoundedCornerShape(14.dp),
            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
            colors = CardDefaults.cardColors(
              containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
            )
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              RadioButton(
                selected = isSelected,
                onClick = { viewModel.setShippingMethod(method) }
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = method.title,
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp
                )
                Text(
                  text = method.description,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
              Text(
                text = if (cost == 0.0) "FREE" else "$${"%.2f".format(cost)}",
                fontWeight = FontWeight.Bold,
                color = if (cost == 0.0) Color(0xFF16A34A) else MaterialTheme.colorScheme.primary,
                fontSize = 15.sp
              )
            }
          }
        }
      }

      // STEP 3: PAYMENT
      if (step == CheckoutStep.PAYMENT) {
        item {
          Text(
            text = "Payment Method",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
        }

        items(PaymentType.entries) { type ->
          val isSelected = paymentDetails.type == type
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { viewModel.updatePaymentDetails(paymentDetails.copy(type = type)) },
            shape = RoundedCornerShape(14.dp),
            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              RadioButton(
                selected = isSelected,
                onClick = { viewModel.updatePaymentDetails(paymentDetails.copy(type = type)) }
              )
              Spacer(modifier = Modifier.width(8.dp))
              Icon(
                imageVector = when (type) {
                  PaymentType.CREDIT_CARD -> Icons.Default.CreditCard
                  PaymentType.GOOGLE_PAY -> Icons.Default.Payment
                  PaymentType.CASH_ON_DELIVERY -> Icons.Default.LocalShipping
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = type.title,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        // Live Credit Card Preview (if Credit Card selected)
        if (paymentDetails.type == PaymentType.CREDIT_CARD) {
          item {
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
              shape = RoundedCornerShape(16.dp),
              elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(
                    Brush.linearGradient(
                      colors = listOf(
                        Color(0xFF1E1B4B),
                        Color(0xFF4338CA),
                        Color(0xFF6366F1)
                      )
                    )
                  )
                  .padding(20.dp)
              ) {
                Column(
                  modifier = Modifier.fillMaxSize(),
                  verticalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "AURA PLATINUM",
                      color = Color.White.copy(alpha = 0.8f),
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      letterSpacing = 2.sp
                    )
                    Icon(
                      imageVector = Icons.Default.CreditCard,
                      contentDescription = null,
                      tint = Color.White
                    )
                  }

                  Text(
                    text = paymentDetails.cardNumber.ifBlank { "•••• •••• •••• 4242" },
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 3.sp
                  )

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Column {
                      Text("CARDHOLDER", color = Color.White.copy(alpha = 0.6f), fontSize = 9.sp)
                      Text(paymentDetails.cardHolder.ifBlank { "ALEX MORGAN" }, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                      Text("EXPIRES", color = Color.White.copy(alpha = 0.6f), fontSize = 9.sp)
                      Text(paymentDetails.expiryDate.ifBlank { "08/28" }, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                  }
                }
              }
            }
          }
        }
      }

      // STEP 4: REVIEW
      if (step == CheckoutStep.REVIEW) {
        item {
          Text(
            text = "Review Order & Shipping",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
        }

        // Selected Address & Delivery Summary Card
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Deliver to: ${selectedAddress?.fullName}", fontWeight = FontWeight.Bold)
              }
              Text(
                text = "${selectedAddress?.streetAddress}, ${selectedAddress?.city}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 24.dp)
              )

              Spacer(modifier = Modifier.height(10.dp))
              HorizontalDivider()
              Spacer(modifier = Modifier.height(10.dp))

              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("${shippingMethod.title} (${shippingMethod.estimatedDelivery})", fontWeight = FontWeight.Bold)
              }
            }
          }
        }

        // Cart Items Preview
        items(cartItems) { item ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "${item.quantity}x ${item.product.title}",
              style = MaterialTheme.typography.bodyMedium,
              modifier = Modifier.weight(1f),
              maxLines = 1
            )
            Text(
              text = "$${"%.2f".format(item.totalPrice)}",
              fontWeight = FontWeight.Bold,
              style = MaterialTheme.typography.bodyMedium
            )
          }
        }

        // Price Summary
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Subtotal", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$${"%.2f".format(subtotal)}", fontWeight = FontWeight.SemiBold)
              }
              if (discount > 0.0) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("Discount (${appliedCoupon?.code})", color = Color(0xFF16A34A))
                  Text("-$${"%.2f".format(discount)}", color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                }
              }
              Spacer(modifier = Modifier.height(4.dp))
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Delivery", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(if (shippingCost == 0.0) "FREE" else "$${"%.2f".format(shippingCost)}", fontWeight = FontWeight.SemiBold)
              }
              Spacer(modifier = Modifier.height(4.dp))
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tax (8%)", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$${"%.2f".format(tax)}", fontWeight = FontWeight.SemiBold)
              }
              Spacer(modifier = Modifier.height(8.dp))
              HorizontalDivider()
              Spacer(modifier = Modifier.height(8.dp))
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total to Pay", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text("$${"%.2f".format(total)}", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
              }
            }
          }
        }
      }

      // STEP 5: SUCCESS / CONFIRMATION
      if (step == CheckoutStep.SUCCESS) {
        item {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Surface(
              shape = CircleShape,
              color = Color(0xFFDCFCE7),
              modifier = Modifier.size(80.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = Color(0xFF16A34A),
                  modifier = Modifier.size(50.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
              text = "Order Confirmed!",
              style = MaterialTheme.typography.headlineMedium,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "Thank you for your purchase. We've sent an order receipt to your email.",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Order Receipt Card
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text("Order ID", color = MaterialTheme.colorScheme.onSurfaceVariant)
                  Text(lastOrder?.id ?: "ORD-12345", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text("Tracking Number", color = MaterialTheme.colorScheme.onSurfaceVariant)
                  Text(lastOrder?.trackingNumber ?: "TRK-00000000US", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text("Total Paid", color = MaterialTheme.colorScheme.onSurfaceVariant)
                  Text("$${"%.2f".format(lastOrder?.total ?: 0.0)}", fontWeight = FontWeight.ExtraBold)
                }
              }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Action Buttons
            Button(
              onClick = {
                val order = lastOrder
                if (order != null) {
                  viewModel.viewOrderDetail(order)
                } else {
                  viewModel.navigateTo(Screen.Orders)
                }
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("track_order_btn"),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Track Live Shipment", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
              onClick = { viewModel.navigateTo(Screen.Home) },
              modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
              shape = RoundedCornerShape(12.dp)
            ) {
              Text("Continue Shopping")
            }
          }
        }
      }
    }
  }
}
