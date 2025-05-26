package com.haru.todo.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haru.todo.data.model.Task
import com.haru.todo.data.repository.TaskRepository
import com.haru.todo.utils.dataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import java.time.Duration
import java.util.Locale

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: TaskRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _remainingTime = MutableStateFlow("로딩 중…")
    val remainingTime: StateFlow<String> = _remainingTime
    var showDoneTasks by mutableStateOf(false)

    // Snackbar 관련 이벤트
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            // DataStore에서 초기화 시각 읽기 (시간, 분)
            context.dataStore.data
                .map { prefs ->
                    val hour = prefs[ResetPrefs.HOUR_KEY] ?: 0
                    val minute = prefs[ResetPrefs.MINUTE_KEY] ?: 0
                    hour to minute
                }
                .distinctUntilChanged()
                .collect { (hour, minute) ->
                    // 남은 시간 계산 타이머 시작
                    startTimer(hour, minute)
                }
        }
    }

    // 코루틴 내에서 1초마다 남은시간 계산
    private var timerJob: Job? = null

    // 1. progress 값 선언
    private val _progress = MutableStateFlow(1.0f)
    val progress: StateFlow<Float> = _progress

    private fun startTimer(hour: Int, minute: Int) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val now = LocalDateTime.now()
                var nextReset = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
                if (now.isAfter(nextReset)) {
                    nextReset = nextReset.plusDays(1)
                }
                val duration = Duration.between(now, nextReset)
                val hours = duration.toHours()
                val minutes = duration.toMinutes() % 60
                val seconds = duration.seconds % 60

                _remainingTime.value = String.format(
                    Locale.getDefault(),
                    "%02d시간 %02d분 %02d초 남음", hours, minutes, seconds
                )

                // 24시간 남은 비율
                val secondsToReset = duration.seconds
                val totalSeconds = 24 * 60 * 60
                val progress = secondsToReset.toFloat() / totalSeconds
                _progress.value = progress

                delay(1000)
            }
        }
    }

    // DataStore의 마지막 리셋 시각을 Flow로 구독
    private val lastResetTime: Flow<Long> = context.dataStore.data
        .map { prefs -> prefs[ResetPrefs.LAST_RESET_TIME] ?: 0L }

    // 오늘 날짜를 항상 새로 계산
    private val today: String
        get() = LocalDate.now().toString()

    // lastResetTime이 바뀔 때마다 Room을 다시 구독!
    val tasks: StateFlow<List<Task>> = lastResetTime.flatMapLatest {
        repository.getActiveTasksByDate(today)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // 할 일 추가
    fun addTask(task: Task) = viewModelScope.launch {
        repository.insertTask(task)
        repository.snapshotDailyStatFor(task.createdDate)
        _eventFlow.emit(UiEvent.ShowSnackbar("할 일이 추가되었습니다!"))
    }

    // 할 일 완료 체크/해제 등 수정
    fun updateTask(task: Task) = viewModelScope.launch {
        repository.updateTask(task)
        repository.snapshotDailyStatFor(task.createdDate)
        _eventFlow.emit(UiEvent.ShowSnackbar("할 일이 수정되었습니다."))
    }

    // 최근 삭제된 Task 임시 저장
    private var recentlyDeletedTask: Task? = null

    // 완전 삭제 + UNDO emit
    fun deleteTask(task: Task) = viewModelScope.launch {
        repository.deleteTask(task) // 완전 삭제
        repository.snapshotDailyStatFor(task.createdDate)
        recentlyDeletedTask = task // 임시 저장
        _eventFlow.emit(UiEvent.ShowSnackbar("할 일이 삭제되었습니다.", withUndo = true))
    }

    // UNDO 복원 함수
    fun restoreDeletedTask() = viewModelScope.launch {
        recentlyDeletedTask?.let {
            repository.insertTask(it)
            recentlyDeletedTask = null
        }
    }

    // UiEvent 정의
    sealed class UiEvent {
        data class ShowSnackbar(val message: String, val withUndo: Boolean = false): UiEvent()
    }

}
