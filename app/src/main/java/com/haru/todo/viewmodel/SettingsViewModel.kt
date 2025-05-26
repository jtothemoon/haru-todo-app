package com.haru.todo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import com.haru.todo.utils.dataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    app: Application
) : AndroidViewModel(app) {

    private val dataStore = app.dataStore

    companion object {
        val HOUR_KEY = intPreferencesKey("reset_hour")
        val MINUTE_KEY = intPreferencesKey("reset_minute")
        val LAST_RESET_TIME_CHANGED_KEY = longPreferencesKey("last_reset_time_changed")
    }

    val resetHour: StateFlow<Int>
    val resetMinute: StateFlow<Int>
    val allowNotification: StateFlow<Boolean>
    val lastResetTimeChanged: StateFlow<Long>

    init {
        val prefsFlow = dataStore.data
            .map { prefs ->
                Pair(
                    prefs[HOUR_KEY] ?: 5,    // 기본 5시
                    prefs[MINUTE_KEY] ?: 0   // 기본 0분
                )
            }

        resetHour = prefsFlow.map { it.first }.stateIn(
            viewModelScope, SharingStarted.Eagerly, 5
        )
        resetMinute = prefsFlow.map { it.second }.stateIn(
            viewModelScope, SharingStarted.Eagerly, 0
        )

        // 알림 상태 Flow 추가
        allowNotification = dataStore.data
            .map { prefs -> prefs[ResetPrefs.ALLOW_NOTIFICATION] ?: true }
            .stateIn(viewModelScope, SharingStarted.Eagerly, true)

        // 마지막 변경 시각 Flow 추가 (기본 0)
        lastResetTimeChanged = dataStore.data
            .map { prefs -> prefs[LAST_RESET_TIME_CHANGED_KEY] ?: 0L }
            .stateIn(viewModelScope, SharingStarted.Eagerly, 0L)
    }

    // 시간 변경 가능 여부 체크 (suspend, UI에서 호출)
    suspend fun canChangeResetTime(): Boolean {
        val lastChanged = lastResetTimeChanged.value
        val now = System.currentTimeMillis()
        // 24시간(=86,400,000ms) 경과했으면 true
        return (now - lastChanged) >= 86_400_000L
    }

    fun setResetTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[HOUR_KEY] = hour
                prefs[MINUTE_KEY] = minute
                prefs[LAST_RESET_TIME_CHANGED_KEY] = System.currentTimeMillis()
            }
            // 알람 등록
            com.haru.todo.utils.AlarmScheduler.cancelResetAlarm(getApplication())
            com.haru.todo.utils.AlarmScheduler.scheduleResetAlarm(getApplication(), hour, minute)
        }
    }

    fun setAllowNotification(allow: Boolean) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[ResetPrefs.ALLOW_NOTIFICATION] = allow
            }
        }
    }

}
