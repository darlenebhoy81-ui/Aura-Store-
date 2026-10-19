package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.FilterState
import com.example.viewmodel.SortOption

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
  filterState: FilterState,
  onApplyFilters: (FilterState) -> Unit,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var tempSort by remember { mutableStateOf(filterState.sortOption) }
  var tempMaxPrice by remember { mutableFloatStateOf(filterState.maxPrice) }
  var tempMinRating by remember { mutableFloatStateOf(filterState.minRating) }
  var tempInStockOnly by remember { mutableStateOf(filterState.inStockOnly) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .padding(bottom = 36.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Filters & Sorting",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close filter sheet")
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Sort by section
      Text(
        text = "Sort By",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(8.dp))
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        SortOption.entries.forEach { option ->
          FilterChip(
            selected = tempSort == option,
            onClick = { tempSort = option },
            label = { Text(option.label) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Price Range Slider
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Maximum Price",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold
        )
        Text(
          text = "Up to $${tempMaxPrice.toInt()}",
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )
      }
      Slider(
        value = tempMaxPrice,
        onValueChange = { tempMaxPrice = it },
        valueRange = 50f..500f,
        steps = 9
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Rating Filter
      Text(
        text = "Minimum Customer Rating",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf(0f to "Any", 4.0f to "4.0★+", 4.5f to "4.5★+", 4.8f to "4.8★+").forEach { (rating, label) ->
          FilterChip(
            selected = tempMinRating == rating,
            onClick = { tempMinRating = rating },
            label = { Text(label) },
            leadingIcon = if (rating > 0f) {
              {
                Icon(
                  imageVector = Icons.Default.Star,
                  contentDescription = null,
                  tint = Color(0xFFFBBF24),
                  modifier = Modifier.width(14.dp)
                )
              }
            } else null
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // In-stock toggle
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "In Stock Only",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
          )
          Text(
            text = "Hide out-of-stock items",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        Switch(
          checked = tempInStockOnly,
          onCheckedChange = { tempInStockOnly = it }
        )
      }

      Spacer(modifier = Modifier.height(28.dp))

      // Action buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedButton(
          onClick = {
            tempSort = SortOption.POPULAR
            tempMaxPrice = 500f
            tempMinRating = 0f
            tempInStockOnly = false
          },
          modifier = Modifier.weight(1f)
        ) {
          Text("Reset")
        }
        Button(
          onClick = {
            onApplyFilters(
              filterState.copy(
                sortOption = tempSort,
                maxPrice = tempMaxPrice,
                minRating = tempMinRating,
                inStockOnly = tempInStockOnly
              )
            )
            onDismiss()
          },
          modifier = Modifier
            .weight(1f)
            .testTag("apply_filter_btn"),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
          )
        ) {
          Text("Apply")
        }
      }
    }
  }
}
