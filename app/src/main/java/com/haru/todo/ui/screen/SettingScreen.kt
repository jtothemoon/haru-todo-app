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
import kotlinx.coroutines.launch

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
    var showResetTimeLockDialog by remember { mutableStateOf(false) }

    var pickedHour by remember { mutableStateOf(hour) }
    var pickedMinute by remember { mutableStateOf(minute) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

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
                onClickChange = {
                    // 개발 환경용
                    showTimePicker = true
//                    scope.launch {
//                        val canChange = viewModel.canChangeResetTime()
//                        if (canChange) {
//                            showTimePicker = true
//                        } else {
//                            showResetTimeLockDialog = true
//                        }
//                    }
                }
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
                        pickedHour = h
                        pickedMinute = m
                        showTimePicker = false
                        showConfirmDialog = true
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

        // 확인/취소 다이얼로그
        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text("초기화 시간 설정") },
                text = {
                    Text(
                        "정말 ${"%02d:%02d".format(pickedHour, pickedMinute)}로 초기화 시간을 설정하시겠습니까?\n\n" +
                                "이후 24시간 이내에는 다시 변경할 수 없습니다."
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.setResetTime(pickedHour, pickedMinute)
                        showConfirmDialog = false
                    }) { Text("확인") }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) {
                        Text("취소")
                    }
                }
            )
        }

        // 24시간 제한 안내 다이얼로그
        if (showResetTimeLockDialog) {
            AlertDialog(
                onDismissRequest = { showResetTimeLockDialog = false },
                title = { Text("변경 제한") },
                text = { Text("초기화 시간은 하루에 한 번만 변경할 수 있습니다.\n\n24시간 후 다시 시도해주세요.") },
                confirmButton = {
                    TextButton(onClick = { showResetTimeLockDialog = false }) {
                        Text("확인")
                    }
                }
            )
        }

    }
}
