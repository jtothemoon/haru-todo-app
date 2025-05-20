package com.haru.todo.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.haru.todo.data.model.Task
import com.haru.todo.data.model.TaskCategory
import com.haru.todo.ui.components.AddTaskDialog
import com.haru.todo.ui.components.TaskList
import com.haru.todo.ui.components.TopBar
import com.haru.todo.viewmodel.MainViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToSettings: () -> Unit
) {
    val tasks by viewModel.tasks.collectAsState()
    val showDoneTasks = viewModel.showDoneTasks
    val remainingTime by viewModel.remainingTime.collectAsState()

    // 카테고리별 진행도 계산
    val importantCompleted = tasks.count { it.category == TaskCategory.IMPORTANT && it.isDone }
    val importantTotal = tasks.count { it.category == TaskCategory.IMPORTANT }

    val mediumCompleted = tasks.count { it.category == TaskCategory.MEDIUM && it.isDone }
    val mediumTotal = tasks.count { it.category == TaskCategory.MEDIUM }

    val generalCompleted = tasks.count { it.category == TaskCategory.GENERAL && it.isDone }
    val generalTotal = tasks.count { it.category == TaskCategory.GENERAL }

    // 할 일 추가 다이얼로그 노출 여부 상태
    var showDialog by remember { mutableStateOf(false) }
    var editTask by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("하루 TODO") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "설정")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "할 일 추가")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
        ) {
            TopBar(
                importantCompleted = importantCompleted,
                importantTotal = importantTotal,
                mediumCompleted = mediumCompleted,
                mediumTotal = mediumTotal,
                generalCompleted = generalCompleted,
                generalTotal = generalTotal
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 남은 시간 (더 짧은 텍스트)
                Text(
                    text = "남은시간: $remainingTime",
                    style = MaterialTheme.typography.bodyMedium
                )

                // 완료된 할 일 보기 토글 (텍스트 + 스위치)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "완료도 보기",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = showDoneTasks,
                        onCheckedChange = { viewModel.showDoneTasks = it }
                    )
                }
            }


            // 할 일 리스트 등 기존 UI
            TaskList(
                tasks = if (showDoneTasks) tasks else tasks.filter { !it.isDone },
                onCheckedChange = { task, checked ->
                    viewModel.updateTask(task.copy(isDone = checked))
                },
                onDeleteClick = { task ->
                    viewModel.deleteTask(task)
                },
                onEditClick = { task -> editTask = task },
                modifier = Modifier
            )
        }

        if (showDialog) {
            // "추가" 모드
            AddTaskDialog(
                onSubmit = { title, category, _ ->
                    viewModel.addTask(
                        Task(
                            title = title,
                            category = category,
                            // 생성 시 isDone은 기본값 false
                            // createdDate는 오늘 날짜로 (LocalDate.now() 등)
                            createdDate = LocalDate.now()
                        )
                    )
                    showDialog = false
                },
                onDismiss = { showDialog = false },
                tasks = tasks,
                taskToEdit = null // 추가 모드!
            )
        }

        if (editTask != null) {
            // "수정" 모드
            AddTaskDialog(
                onSubmit = { title, category, id ->
                    viewModel.updateTask(
                        editTask!!.copy(title = title, category = category)
                    )
                    editTask = null
                },
                onDismiss = { editTask = null },
                tasks = tasks,
                taskToEdit = editTask
            )
        }
    }
}
