package com.haru.todo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.haru.todo.ui.theme.HourglassBarBg
import com.haru.todo.ui.theme.HourglassBarColor
import com.haru.todo.ui.theme.UnfilledColor
import com.haru.todo.ui.theme.haruSwitchColors

@Composable
fun StatusBar(
    remainingTime: String,
    progress: Float, // 0.0~1.0 (남은 시간 비율)
    showDoneTasks: Boolean,
    onToggleShowDoneTasks: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 남은 시간
            Text(
                text = "남은시간: $remainingTime",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            // 완료 포함 토글
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "완료 포함",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.width(4.dp))
                Switch(
                    checked = showDoneTasks,
                    onCheckedChange = onToggleShowDoneTasks,
                    colors = haruSwitchColors()
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        // 하단 ProgressBar (모래시계 컬러)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(HourglassBarBg)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress) // 진행도(남은 시간 비율)
                    .background(HourglassBarColor)
            )
        }
    }
}