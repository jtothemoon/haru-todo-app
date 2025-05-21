package com.haru.todo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.haru.todo.ui.theme.GeneralColor
import com.haru.todo.ui.theme.ImportantColor
import com.haru.todo.ui.theme.MediumColor
import com.haru.todo.ui.theme.UnfilledColor
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ProgressBarSection(
    importantCompleted: Int, importantTotal: Int,
    mediumCompleted: Int, mediumTotal: Int,
    generalCompleted: Int, generalTotal: Int,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd E", Locale.KOREAN))
    val totalDone = importantCompleted + mediumCompleted + generalCompleted
    val totalCount = importantTotal + mediumTotal + generalTotal
    val percent = if (totalCount == 0) 0 else (totalDone * 100 / totalCount)

    // 각 카테고리별 비율 (0~1 사이)
    val impRatio = if (totalCount == 0) 0f else importantCompleted.toFloat() / totalCount
    val medRatio = if (totalCount == 0) 0f else mediumCompleted.toFloat() / totalCount
    val genRatio = if (totalCount == 0) 0f else generalCompleted.toFloat() / totalCount
    val unfilledRatio = 1f - (impRatio + medRatio + genRatio)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 날짜 (왼쪽)
        Text(
            text = today,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            modifier = Modifier.padding(end = 10.dp)
        )

        // 진행률 (오른쪽)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 3색 막대 (양 끝만 둥글게)
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(UnfilledColor)
            ) {
                // 비율과 색 정보를 한 번에 리스트로
                val colorList = listOf(
                    Pair(impRatio, ImportantColor),
                    Pair(medRatio, MediumColor),
                    Pair(genRatio, GeneralColor),
                    Pair(unfilledRatio, UnfilledColor)
                )
                val visibleBlocks = colorList.filter { it.first > 0f }
                val lastIdx = visibleBlocks.lastIndex

                Row(Modifier.fillMaxSize()) {
                    visibleBlocks.forEachIndexed { idx, (ratio, color) ->
                        val shape = when (idx) {
                            0 -> if (idx == lastIdx)
                                RoundedCornerShape(12.dp) // 혼자일 땐 pill 전체 둥글게
                            else
                                RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp) // 왼쪽 끝
                            lastIdx -> RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp) // 오른쪽 끝
                            else -> RoundedCornerShape(0.dp) // 중간은 각지게
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(ratio)
                                .background(color, shape)
                        )
                    }
                }
            }
            // 퍼센트 텍스트
            Text(
                text = "$percent%",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(start = 8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End
            )
        }
    }
}