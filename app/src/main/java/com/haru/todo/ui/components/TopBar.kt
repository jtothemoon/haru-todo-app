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
import androidx.compose.ui.unit.dp
import com.haru.todo.ui.theme.GeneralColor
import com.haru.todo.ui.theme.ImportantColor
import com.haru.todo.ui.theme.MediumColor
import com.haru.todo.ui.theme.UnfilledColor
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TopBar(
    importantCompleted: Int, importantTotal: Int,
    mediumCompleted: Int, mediumTotal: Int,
    generalCompleted: Int, generalTotal: Int
) {
    val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd (E)", Locale.getDefault()))
    // 각 카테고리 완료 비율
    val totalDone = importantCompleted + mediumCompleted + generalCompleted
    val totalCount = importantTotal + mediumTotal + generalTotal
    val percent = if (totalCount == 0) 0 else (totalDone * 100 / totalCount)

    // 각 카테고리별 비율 (0~1 사이)
    val impRatio = if (totalCount == 0) 0f else importantCompleted.toFloat() / totalCount
    val medRatio = if (totalCount == 0) 0f else mediumCompleted.toFloat() / totalCount
    val genRatio = if (totalCount == 0) 0f else generalCompleted.toFloat() / totalCount
    val unfilledRatio = 1f - (impRatio + medRatio + genRatio)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 날짜 (왼쪽)
        Text(
            text = today,
            style = MaterialTheme.typography.titleMedium
        )

        // 진행률 (오른쪽)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 3색 막대
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(UnfilledColor)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    if (impRatio > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(impRatio)
                                .background(ImportantColor)
                        )
                    }
                    if (medRatio > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(medRatio)
                                .background(MediumColor)
                        )
                    }
                    if (genRatio > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(genRatio)
                                .background(GeneralColor)
                        )
                    }
                    if (unfilledRatio > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(unfilledRatio)
                                .background(UnfilledColor)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            // 퍼센트 텍스트
            Text(
                text = "$percent%",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
