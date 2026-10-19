package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun WriteReviewDialog(
  productTitle: String,
  onDismiss: () -> Unit,
  onSubmit: (rating: Int, comment: String, reviewerName: String) -> Unit
) {
  var rating by remember { mutableIntStateOf(5) }
  var reviewerName by remember { mutableStateOf("") }
  var reviewText by remember { mutableStateOf("") }
  var isError by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "Write a Review",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = productTitle,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Rating Stars Selector
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          (1..5).forEach { starIndex ->
            Icon(
              imageVector = if (starIndex <= rating) Icons.Default.Star else Icons.Outlined.StarOutline,
              contentDescription = "Rate $starIndex stars",
              tint = if (starIndex <= rating) Color(0xFFFBBF24) else MaterialTheme.colorScheme.outline,
              modifier = Modifier
                .size(36.dp)
                .clickable { rating = starIndex }
                .padding(4.dp)
            )
          }
        }

        OutlinedTextField(
          value = reviewerName,
          onValueChange = { reviewerName = it },
          label = { Text("Your Name (Optional)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = reviewText,
          onValueChange = {
            reviewText = it
            isError = false
          },
          label = { Text("Your Feedback") },
          placeholder = { Text("What did you like or dislike about this product?") },
          minLines = 3,
          maxLines = 5,
          isError = isError,
          supportingText = if (isError) { { Text("Please write a brief comment") } } else null,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (reviewText.isBlank()) {
            isError = true
          } else {
            onSubmit(rating, reviewText.trim(), reviewerName.trim())
            onDismiss()
          }
        },
        modifier = Modifier.testTag("submit_review_btn"),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary
        )
      ) {
        Text("Submit Review")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
