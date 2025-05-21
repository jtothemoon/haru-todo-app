package com.haru.todo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable

@Composable
fun haruSwitchColors() = SwitchDefaults.colors(
    checkedThumbColor = MaterialTheme.colorScheme.primary,
    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
    uncheckedThumbColor = UnfilledColor,
    uncheckedTrackColor = UnfilledColor.copy(alpha = 0.3f)
)
