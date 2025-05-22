package com.haru.todo.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.haru.todo.ui.components.AppBar
import com.haru.todo.viewmodel.MainViewModel

@Composable
fun HomeRootScreen(
    viewModel: MainViewModel,
    onNavigateToSettings: () -> Unit,
) {
    // 탭 상태 (0: 오늘, 1: 캘린더)
    var selectedTab by rememberSaveable { mutableStateOf(0) }
    val tabs = listOf("오늘", "캘린더")

    // 스낵바 상태 (필요시 하위로 전달)
    val snackbarHostState = remember { SnackbarHostState() }

    // "오늘" 탭에서만 +버튼 및 AddBar 동작
    var showAddBar by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column {
                AppBar(
                    onNavigateToSettings = onNavigateToSettings,
                    onClickAddTask = if (selectedTab == 0) { { showAddBar = true } } else null
                )

            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        // 필요시 floatingActionButton, bottomBar 등 slot도 여기에!
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ){
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
            // 여기에 메인 컨텐츠(오늘, 캘린더) → innerPadding 적용!
            when (selectedTab) {
                0 -> MainScreen(
                    viewModel = viewModel,
                    onNavigateToSettings = onNavigateToSettings,
                    snackbarHostState = snackbarHostState,
                    showAddBar = showAddBar,
                    onShowAddBarChange = { showAddBar = it },
                    modifier = Modifier.padding(innerPadding)
                )
                1 -> CalendarStatScreen()
            }
        }

    }
}