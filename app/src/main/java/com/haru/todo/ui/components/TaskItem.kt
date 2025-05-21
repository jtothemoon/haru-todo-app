package com.haru.todo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.haru.todo.data.model.Task
import com.haru.todo.data.model.TaskCategory
import com.haru.todo.utils.categoryColor
import java.time.LocalDate

enum class TaskRowMode { VIEW, EDIT }

@Composable
fun TaskRowItem(
    mode: TaskRowMode = TaskRowMode.VIEW,
    title: String = "",
    onTitleChange: (String) -> Unit = {},
    category: TaskCategory = TaskCategory.IMPORTANT,
    onCategoryChange: (TaskCategory) -> Unit = {},
    task: Task? = null,
    onCheckedChange: (Boolean) -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onClick: () -> Unit = {},
    onUpdate: () -> Unit = {},
    onCancel: () -> Unit = {},
    modifier: Modifier = Modifier // 추가!
) {
    val itemHeight = 48.dp
    val rightIconsWidth = 120.dp
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(itemHeight)
            .let { if (mode == TaskRowMode.VIEW) it.clickable { onClick() } else it }
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. 컬러바
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(
                    color = categoryColor(
                        if (mode == TaskRowMode.VIEW && task != null) task.category else category
                    ),
                    shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp) // 위/아래만 둥글게!
                )
        )
        Spacer(modifier = Modifier.width(10.dp))

        // 2. 체크박스 (VIEW)
        if (mode == TaskRowMode.VIEW && task != null) {
            Checkbox(
                checked = task.isDone,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.size(itemHeight),
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,           // 체크 박스(채워질 때)
                    uncheckedColor = MaterialTheme.colorScheme.outline,         // 체크 안 됐을 때 테두리
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary,       // 체크 표시(V) 색상
                    disabledCheckedColor = MaterialTheme.colorScheme.surfaceVariant,    // 비활성화 체크
                    disabledUncheckedColor = MaterialTheme.colorScheme.surfaceVariant   // 비활성화 언체크
                )
            )
            Spacer(modifier = Modifier.width(10.dp))
        }

        // 3. 텍스트 or 입력란
        if (mode == TaskRowMode.EDIT) {
            BasicTextField(
                value = title,
                onValueChange = onTitleChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary), // 커서 색상
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(end = 8.dp),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 4.dp, vertical = 0.dp), // 내부 여백
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (title.isBlank()) {
                            Text(
                                "할 일을 입력하세요",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        innerTextField()
                    }
                }
            )
        } else {
            Text(
                text = task?.title.orEmpty(),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                maxLines = 1
            )
        }

        // 4. 오른쪽 컨트롤
        Box(
            modifier = Modifier
                .width(rightIconsWidth)
                .fillMaxHeight(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (mode == TaskRowMode.EDIT) {
                    TextButton(
                        onClick = { expanded = true },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.height(itemHeight)
                    ) {
                        Text(category.displayName(),
                            style = MaterialTheme.typography.bodyLarge,
                            )
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = null, modifier = Modifier.size(24.dp))
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        TaskCategory.values().forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.displayName()) },
                                onClick = {
                                    onCategoryChange(cat)
                                    expanded = false
                                }
                            )
                        }
                    }
                    IconButton(
                        onClick = { if (title.isNotBlank()) onUpdate() },
                        enabled = title.isNotBlank(),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "완료",
                            tint = MaterialTheme.colorScheme.primary // 포인트 컬러(성공)
                        )
                    }
                    IconButton(
                        onClick = onCancel,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "취소",
                            tint = MaterialTheme.colorScheme.outline // 연한 그레이(중립)
                        )
                    }
                } else {
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "삭제",
                            tint = MaterialTheme.colorScheme.error // 빨강(경고)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskRowItemPreview() {
    TaskRowItem(
        mode = TaskRowMode.VIEW,
        task = Task(1, "예시 할 일", false, TaskCategory.MEDIUM, LocalDate.now()),
        onCheckedChange = {},
        onDeleteClick = {},
        onClick = {}
    )
}