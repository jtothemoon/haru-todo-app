package com.haru.todo.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.haru.todo.data.model.TaskCategory
import com.haru.todo.ui.components.AddTaskBar
import com.haru.todo.ui.components.AppBar
import com.haru.todo.ui.components.ProgressBarSection
import com.haru.todo.ui.components.StatusBar
import com.haru.todo.ui.components.TaskList
import com.haru.todo.viewmodel.MainViewModel
import java.time.LocalDate

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
    var showAddBar by remember { mutableStateOf(false) }
    var editingTaskId by remember { mutableStateOf<Int?>(null) }

    // 남은 시간 계산(프로그레스바)
    val progress by viewModel.progress.collectAsState()

    Scaffold(
        topBar = {
            AppBar(
                onNavigateToSettings = onNavigateToSettings,
                onClickAddTask = { showAddBar = true }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
        ) {
            ProgressBarSection(
                importantCompleted = importantCompleted,
                importantTotal = importantTotal,
                mediumCompleted = mediumCompleted,
                mediumTotal = mediumTotal,
                generalCompleted = generalCompleted,
                generalTotal = generalTotal,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            StatusBar(
                remainingTime = remainingTime,
                progress = progress, // ← Float, 0.0~1.0 (남은 시간 비율)
                showDoneTasks = showDoneTasks,
                onToggleShowDoneTasks = { viewModel.showDoneTasks = it },
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            if (showAddBar) {
                AddTaskBar(
                    tasks = tasks,
                    onAddTask = { title, category ->
                        // 실제 추가로직(성공시 true, 실패시 false)
                        viewModel.addTask(
                            com.haru.todo.data.model.Task(
                                title = title,
                                category = category,
                                createdDate = LocalDate.now()
                            )
                        )
                        true // 성공시 true 반환
                    },
                    onCancel = { showAddBar = false },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // 할 일 리스트 등 기존 UI
            TaskList(
                tasks = if (showDoneTasks) tasks else tasks.filter { !it.isDone },
                editingTaskId = editingTaskId,
                onCheckedChange = { task, checked -> viewModel.updateTask(task.copy(isDone = checked)) },
                onDeleteClick = { task -> viewModel.deleteTask(task) },
                onEditClick = { task -> editingTaskId = task.id }, // 클릭 시 수정모드 전환
                onUpdateTask = { updatedTask ->
                    viewModel.updateTask(updatedTask)
                    editingTaskId = null // 수정모드 종료
                },
                onCancelEdit = { editingTaskId = null }
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun AddTaskBarPreview() {
    AddTaskBar(
        tasks = emptyList(),
        onAddTask = { _, _ -> true },
        onCancel = {}
    )
}