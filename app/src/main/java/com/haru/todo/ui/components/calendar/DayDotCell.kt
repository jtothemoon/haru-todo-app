package com.haru.todo.ui.components.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.haru.todo.data.model.DailyTaskStat
import com.haru.todo.ui.theme.GeneralColor
import com.haru.todo.ui.theme.HaruOnBackground
import com.haru.todo.ui.theme.ImportantColor
import com.haru.todo.ui.theme.MediumColor
import com.haru.todo.ui.theme.UnfilledColor
import com.kizitonwose.calendar.core.CalendarDay
import java.time.YearMonth

@Composable
fun DayDotCell(
    day: CalendarDay,
    isToday: Boolean,
    stat: DailyTaskStat?,
    visibleMonth: YearMonth,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    // 현재 월에 속하는 날짜만 진하게
    val isInCurrentMonth =
        day.date.year == visibleMonth.year && day.date.month == visibleMonth.month

    val importantDone = stat?.importantDone ?: false
    val mediumDoneCount = stat?.mediumDoneCount ?: 0
    val generalDoneCount = stat?.generalDoneCount ?: 0

    // 오늘이면 흰색, 아니면 원래 색
    val textColor = when {
        isToday -> Color.White
        !isInCurrentMonth -> MaterialTheme.colorScheme.outline
        else -> MaterialTheme.colorScheme.onBackground
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            )
            .then(
                if (isSelected) Modifier
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape) // 선택 효과
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        // 오늘 날짜: 빨간 원(꽉 찬)
        if (isToday) {
            Box(
                Modifier
                    .fillMaxSize(0.85f)
                    .clip(CircleShape)
                    .background(HaruOnBackground, CircleShape)
                    .align(Alignment.Center)
            )
        }

        // 점 세 개(아래 정렬)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 3.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 날짜 숫자 (위에)
            Text(
                text = day.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // 중요(빨강)
                Dot(
                    color = if (importantDone) ImportantColor else UnfilledColor,
                    size = 7.dp
                )
                Spacer(Modifier.width(3.dp))
                // 보통(파랑) - 완료 개수별 점 크기 변화
                Dot(
                    color = if (mediumDoneCount > 0) MediumColor else UnfilledColor,
                    size = (5 + mediumDoneCount * 2).dp // 5~11dp
                )
                Spacer(Modifier.width(3.dp))
                // 일반(초록) - 완료 개수별 점 크기 변화
                Dot(
                    color = if (generalDoneCount > 0) GeneralColor else UnfilledColor,
                    size = (4 + generalDoneCount * 1.5f).dp // 4~11.5dp
                )
            }
        }
    }
}

@Composable
fun Dot(color: Color, size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}