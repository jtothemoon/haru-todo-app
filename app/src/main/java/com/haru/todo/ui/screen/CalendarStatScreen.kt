package com.haru.todo.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.haru.todo.ui.components.calendar.HaruCalendarView
import com.haru.todo.viewmodel.CalendarStatViewModel
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import kotlinx.coroutines.launch
import java.time.YearMonth

@Composable
fun CalendarStatScreen(
    viewModel: CalendarStatViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    // ViewModel에서 월별 통계 맵 받아오기
    val statMap by viewModel.statMap.collectAsState()
    val currentMonth = remember { YearMonth.now() }
    val state = rememberCalendarState(
        startMonth = currentMonth.minusMonths(100),
        endMonth = currentMonth.plusMonths(100),
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeekFromLocale()
    )

    // 상태: 현재 월 관리 (state의 firstVisibleMonth가 변경될 때 recomposition)
    val visibleMonth = state.firstVisibleMonth.yearMonth
    val coroutineScope = rememberCoroutineScope()

    // visibleMonth가 바뀌면 ViewModel에 setMonth로 알리기
    LaunchedEffect(visibleMonth) {
        viewModel.setMonth(visibleMonth)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        // 1. 상단 월/연도 + 네비게이션
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        state.scrollToMonth(visibleMonth.minusMonths(1))
                    }
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack, // ← Icons.Filled.ChevronLeft가 없을 경우 대체
                    contentDescription = "이전 달"
                )
            }
            Text(
                text = "${visibleMonth.year}년 ${visibleMonth.monthValue}월",
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        state.scrollToMonth(visibleMonth.plusMonths(1))
                    }
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward, // ← Icons.Filled.ChevronRight가 없을 경우 대체
                    contentDescription = "다음 달"
                )
            }
        }

        // 2. 요일 라벨
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 일~토, 컬러는 필요시 조정
            listOf("일", "월", "화", "수", "목", "금", "토").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center
                )
            }
        }

        // 3. 달력
        HaruCalendarView(
            state = state,
            statMap = statMap,
            visibleMonth = visibleMonth
        )
    }
}