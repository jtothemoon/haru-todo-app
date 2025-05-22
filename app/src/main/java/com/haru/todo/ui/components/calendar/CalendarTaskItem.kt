package com.haru.todo.ui.components.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.haru.todo.data.model.Task
import com.haru.todo.utils.categoryColor

@Composable
fun CalendarTaskItem(
    task: Task,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 8.dp), // 넉넉한 간격
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. 카테고리 컬러바 (기존 스타일과 동일)
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(28.dp)
                .background(
                    color = categoryColor(task.category),
                    shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp)
                )
        )
        Spacer(modifier = Modifier.width(10.dp))
        // 2. 할 일 텍스트 (완료 시 흐린색/취소선)
        Text(
            text = task.title,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = if (task.isDone)
                    MaterialTheme.colorScheme.outline
                else
                    MaterialTheme.colorScheme.onBackground,
                textDecoration = if (task.isDone)
                    TextDecoration.LineThrough
                else
                    TextDecoration.None
            ),
            modifier = Modifier.weight(1f),
            maxLines = 1
        )
    }
}