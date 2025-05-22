package com.haru.todo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.haru.todo.data.model.Task
import com.haru.todo.data.model.TaskCategory

@Composable
fun TaskList(
    tasks: List<Task>,
    editingTaskId: Int?,
    isAddingTask: Boolean = false,
    onCheckedChange: (Task, Boolean) -> Unit,
    onDeleteClick: (Task) -> Unit,
    onEditClick: (Task) -> Unit,
    onUpdateTask: (Task) -> Unit,
    onCancelEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 1. 현재 수정중인 Task의 상태를 remember
    var editTitle by remember(editingTaskId) {
        mutableStateOf(tasks.find { it.id == editingTaskId }?.title ?: "")
    }
    var editCategory by remember(editingTaskId) {
        mutableStateOf(tasks.find { it.id == editingTaskId }?.category ?: TaskCategory.IMPORTANT)
    }

    // "입력/수정 중" 상태 플래그 계산
    val isEditing = editingTaskId != null

    if (tasks.isEmpty() && !isEditing && !isAddingTask) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "오늘의 할 일이 없습니다!",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
        ) {
            items(tasks, key = { it.id }) { task ->
                val isEdit = editingTaskId == task.id

                if (isEdit) {
                    TaskRowItem(
                        mode = TaskRowMode.EDIT,
                        title = editTitle,
                        onTitleChange = { editTitle = it },
                        category = editCategory,
                        onCategoryChange = { editCategory = it },
                        onUpdate = {
                            onUpdateTask(task.copy(title = editTitle, category = editCategory))
                        },
                        onCancel = onCancelEdit,
                    )
                } else {
                    TaskRowItem(
                        mode = TaskRowMode.VIEW,
                        task = task,
                        onCheckedChange = { checked -> onCheckedChange(task, checked) },
                        onDeleteClick = { onDeleteClick(task) },
                        onClick = { onEditClick(task) }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

}