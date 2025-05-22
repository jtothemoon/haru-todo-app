package com.haru.todo.ui.components.calendar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.haru.todo.data.model.DailyTaskStat
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.core.CalendarDay
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun HaruCalendarView(
    state: CalendarState,
    statMap: Map<LocalDate, DailyTaskStat>,
    visibleMonth: YearMonth,
    selectedDate: LocalDate,
    onDayClick: (CalendarDay) -> Unit,
    modifier: Modifier = Modifier
) {
    HorizontalCalendar(
        state = state,
        dayContent = { day ->
            DayDotCell(
                day = day,
                isToday = day.date == LocalDate.now(),
                stat = statMap[day.date],
                visibleMonth = visibleMonth,
                isSelected = day.date == selectedDate,
                onClick = { onDayClick(day) }
            )
        },
        modifier = modifier
    )
}
