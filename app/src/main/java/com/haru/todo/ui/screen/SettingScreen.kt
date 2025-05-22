package com.haru.todo.ui.screen

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.haru.todo.ui.components.settings.SettingNotificationSwitch
import com.haru.todo.ui.components.settings.SettingTimePicker
import com.haru.todo.ui.theme.HaruDivider
import com.haru.todo.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val context = LocalContext.current
    val hour by viewModel.resetHour.collectAsState()
    val minute by viewModel.resetMinute.collectAsState()
    val allowNotification by viewModel.allowNotification.collectAsState()
    var showTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("설정") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            // 1. 알림 스위치
            SettingNotificationSwitch(
                checked = allowNotification,
                onCheckedChange = { checked ->
                    viewModel.setAllowNotification(checked)
                }
            )
            HaruDivider()

            Spacer(Modifier.height(24.dp))

            // 2. 시간 설정
            SettingTimePicker(
                hour = hour,
                minute = minute,
                onClickChange = { showTimePicker = true }
            )
            HaruDivider()

            Spacer(Modifier.height(32.dp))

        }
        // 시간 다이얼로그
        if (showTimePicker) {
            DisposableEffect(Unit) {
                val dialog = TimePickerDialog(
                    context,
                    { _, h, m ->
                        viewModel.setResetTime(h, m)
                        showTimePicker = false
                    },
                    hour, minute, true
                )
                dialog.setOnDismissListener {
                    showTimePicker = false  // 다이얼로그가 취소/닫혀도 반드시 false!
                }
                dialog.show()

                onDispose { dialog.dismiss() }
            }
        }

    }
}
