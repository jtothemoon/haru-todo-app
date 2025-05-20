package com.haru.todo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.haru.todo.data.model.Task
import com.haru.todo.data.model.TaskCategory
import com.haru.todo.utils.categoryLabel

@Composable
fun AddTaskDialog(
    onSubmit: (String, TaskCategory, Int?) -> Unit,
    onDismiss: () -> Unit,
    tasks: List<Task>,
    taskToEdit: Task? = null
) {
    var title by remember(taskToEdit) { mutableStateOf(taskToEdit?.title ?: "") }
    var category by remember(taskToEdit) { mutableStateOf(taskToEdit?.category ?: TaskCategory.IMPORTANT) }
    val categoryCounts = tasks.groupingBy { it.category }.eachCount()

    // 카테고리별 최대 개수 제한
    val maxCounts = mapOf(
        TaskCategory.IMPORTANT to 1,
        TaskCategory.MEDIUM to 3,
        TaskCategory.GENERAL to 5
    )

    val currentCount = categoryCounts[category] ?: 0
    val maxCount = maxCounts[category] ?: 0

    // 수정모드는 자기 자신은 카운트에서 제외해야 에러 안남!
    val isMax = if (taskToEdit == null) {
        currentCount >= maxCount
    } else {
        if (category == taskToEdit.category) {
            false
        } else {
            currentCount >= maxCount
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (taskToEdit == null) "할 일 추가" else "할 일 수정") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("할 일 내용") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                CategoryDropdown(category, onCategoryChange = { category = it })
                if (isMax) {
                    Text(
                        text = "해당 카테고리는 최대 개수(${
                            maxCounts[category]
                        })를 초과할 수 없습니다.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank() && !isMax) {
                        onSubmit(title, category, taskToEdit?.id)
                    }
                },
                enabled = title.isNotBlank() && !isMax
            ) {
                Text(if (taskToEdit == null) "추가" else "수정")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

@Composable
fun CategoryDropdown(
    category: TaskCategory,
    onCategoryChange: (TaskCategory) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val categories = TaskCategory.values()

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(text = categoryLabel(category))
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach {
                DropdownMenuItem(
                    text = { Text(categoryLabel(it)) },
                    onClick = {
                        onCategoryChange(it)
                        expanded = false
                    }
                )
            }
        }
    }
}
