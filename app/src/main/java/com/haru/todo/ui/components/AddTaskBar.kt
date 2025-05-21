package com.haru.todo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.haru.todo.data.model.TaskCategory

@Composable
fun AddTaskBar(
    tasks: List<com.haru.todo.data.model.Task>,
    onAddTask: (String, TaskCategory) -> Boolean,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(TaskCategory.IMPORTANT) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TaskRowItem(
                mode = TaskRowMode.EDIT,
                title = title,
                onTitleChange = { title = it },
                category = category,
                onCategoryChange = { category = it },
                onUpdate = {
                    // 카테고리별 최대 개수 체크 (메인화면과 동일 로직)
                    val categoryCounts = tasks.groupingBy { it.category }.eachCount()
                    val maxCounts = mapOf(
                        TaskCategory.IMPORTANT to 1,
                        TaskCategory.MEDIUM to 3,
                        TaskCategory.GENERAL to 5
                    )
                    val currentCount = categoryCounts[category] ?: 0
                    val maxCount = maxCounts[category] ?: 0
                    if (currentCount >= maxCount) {
                        errorMessage = when (category) {
                            TaskCategory.IMPORTANT -> "중요 할 일은 1개만 추가할 수 있어요!"
                            TaskCategory.MEDIUM -> "보통 할 일은 3개까지 추가할 수 있어요!"
                            TaskCategory.GENERAL -> "일반 할 일은 5개까지 추가할 수 있어요!"
                        }
                        return@TaskRowItem
                    }
                    if (title.isBlank()) {
                        errorMessage = "할 일을 입력하세요."
                        return@TaskRowItem
                    }
                    // 실제 할 일 추가 시도
                    val result = onAddTask(title, category)
                    if (result) {
                        errorMessage = null
                        title = ""
                        onCancel() // 성공 시 입력바 닫기
                    } else {
                        errorMessage = "할 일 추가 실패(중복?)"
                    }
                },
                onCancel = {
                    onCancel()
                    errorMessage = null
                },
                onCheckedChange = {},
                onDeleteClick = {},
                onClick = {},
            )
        }
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}
