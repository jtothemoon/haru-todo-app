package com.haru.todo.ui.screen

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "알림 받기",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = allowNotification,
                    onCheckedChange = { checked ->
                        viewModel.setAllowNotification(checked)
                    }
                )
            }
            Spacer(Modifier.height(24.dp))
            Text("하루 초기화 시간", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    String.format("%02d:%02d", hour, minute),
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(Modifier.width(24.dp))
                Button(onClick = { showTimePicker = true }) {
                    Text("시간 변경")
                }
            }
            Spacer(Modifier.height(32.dp))
            // 추가로 저장 버튼 등 필요시 구현

        }
        if (showTimePicker) {
            TimePickerDialog(
                context,
                { _, h, m ->
                    viewModel.setResetTime(h, m)
                    showTimePicker = false
                },
                hour, minute, true
            ).show()
            // 다이얼로그가 닫히지 않으면 showTimePicker를 false로 직접 설정해 주세요.
        }
    }
}
