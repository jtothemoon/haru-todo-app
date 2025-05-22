package com.haru.todo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haru.todo.data.model.DailyTaskStat
import com.haru.todo.data.model.Task
import com.haru.todo.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CalendarStatViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    // 현재 캘린더에 보이는 월
    private val _calendarMonth = MutableStateFlow(YearMonth.now())
    val calendarMonth: StateFlow<YearMonth> = _calendarMonth

    // 월 범위별 통계 Map<LocalDate, DailyTaskStat>
    @OptIn(ExperimentalCoroutinesApi::class)
    val statMap: StateFlow<Map<LocalDate, DailyTaskStat>> =
        _calendarMonth.flatMapLatest { month ->
            val start = month.atDay(1)
            val end = month.atEndOfMonth()
            repository.getStatsBetween(start.toString(), end.toString())
        }.map { stats ->
            stats.associateBy { LocalDate.parse(it.date) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun setMonth(month: YearMonth) {
        _calendarMonth.value = month
    }

    // 선택된 날짜 상태
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    // 선택 날짜별 할 일 리스트 (자동 갱신)
    val tasksOfSelectedDate: StateFlow<List<Task>> =
        _selectedDate.flatMapLatest { date ->
            repository.getAllTasksByDate(date.toString())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
