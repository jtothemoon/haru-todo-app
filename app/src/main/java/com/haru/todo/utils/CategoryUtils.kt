package com.haru.todo.utils

import androidx.compose.ui.graphics.Color
import com.haru.todo.data.model.TaskCategory
import com.haru.todo.ui.theme.GeneralColor
import com.haru.todo.ui.theme.ImportantColor
import com.haru.todo.ui.theme.MediumColor

fun categoryColor(category: TaskCategory): Color = when (category) {
    TaskCategory.IMPORTANT -> ImportantColor
    TaskCategory.MEDIUM -> MediumColor
    TaskCategory.GENERAL -> GeneralColor
}
