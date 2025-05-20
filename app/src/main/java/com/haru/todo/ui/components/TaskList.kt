package com.haru.todo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.haru.todo.data.model.Task

@Composable
fun TaskList(
    tasks: List<Task>,
    onCheckedChange: (Task, Boolean) -> Unit,
    onDeleteClick: (Task) -> Unit,
    onEditClick: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
    ) {
        items(tasks.size) { index ->
            val task = tasks[index]
            TaskItem(
                task = task,
                onCheckedChange = { checked -> onCheckedChange(task, checked) },
                onDeleteClick = { onDeleteClick(task) },
                onClick = { onEditClick(task) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
