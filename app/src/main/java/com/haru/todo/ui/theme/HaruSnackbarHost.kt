package com.haru.todo.ui.theme

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HaruSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier.padding(bottom = 32.dp),
        snackbar = { data ->
            Snackbar(
                shape = RoundedCornerShape(10.dp),
                containerColor = HaruSnackbarBg,
                contentColor = HaruSnackbarText,
                modifier = Modifier
                    .padding(horizontal = 40.dp)
                    .defaultMinSize(minHeight = 36.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        data.visuals.message,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    )
                    IconButton(
                        onClick = {
                            // snackbarHostState의 현재 snackbar를 닫는다!
                            hostState.currentSnackbarData?.dismiss()
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .padding(end = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "닫기",
                            tint = HaruSnackbarText.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    )
}