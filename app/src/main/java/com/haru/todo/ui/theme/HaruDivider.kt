package com.haru.todo.ui.theme

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun HaruDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp
) {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 1f), // alpha 명시
        thickness = thickness,
        modifier = modifier
    )
}

