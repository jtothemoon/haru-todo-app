package com.haru.todo.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    onNavigateToSettings: () -> Unit,
    onClickAddTask: (() -> Unit)? = null
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "하루 TODO",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground // 컬러 지정!
            )
        },
        actions = {
            if (onClickAddTask != null) {
                IconButton(onClick = onClickAddTask) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "할 일 추가",
                        tint = MaterialTheme.colorScheme.primary // 포인트컬러
                    )
                }
            }
            IconButton(onClick = onNavigateToSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "설정",
                    tint = MaterialTheme.colorScheme.primary // 포인트컬러
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,      // AppBar 배경(흰색)
            actionIconContentColor = MaterialTheme.colorScheme.primary, // 아이콘
            titleContentColor = MaterialTheme.colorScheme.onBackground  // 타이틀
        ),
        // 그림자 효과로 헤더 분리감 강화
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    )
}
