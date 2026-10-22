package com.example.personal_management_app.ui.screen.tag

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.personal_management_app.entities.TagEntity
import com.example.personal_management_app.entities.tagColors
import com.example.personal_management_app.ui.components.main_layout.MainTopBar
import com.example.personal_management_app.ui.components.tag.TagCard
import com.example.personal_management_app.ui.components.tag.TagEditorDialog
import com.example.personal_management_app.ui.layouts.MainLayout
import com.example.personal_management_app.viewmodel.TagViewModel

@Composable
fun TagScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: TagViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var editing by remember { mutableStateOf<TagEntity?>(null) }
    var showEditor by remember { mutableStateOf(false) }
    var editorError by remember { mutableStateOf<String?>(null) }
    var pendingDelete by remember { mutableStateOf<TagEntity?>(null) }

    val tags by viewModel.tags.collectAsStateWithLifecycle()
    val visible = remember(tags, searchQuery) { viewModel.filter(searchQuery) }

    MainLayout(
        navController = navController,
        currentTab = -1,
        topBar = { onMenuClick ->
            MainTopBar(
                navController = navController,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                placeholderText = "Tìm kiếm nhãn",
                onMenuClick = onMenuClick
            )
        }
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            FilledTonalButton(
                onClick = {
                    editing = null
                    editorError = null
                    showEditor = true
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Text("Thêm nhãn", modifier = Modifier.padding(start = 8.dp))
            }

            if (visible.isEmpty()) {
                Text(
                    text = if (searchQuery.isBlank()) "Chưa có nhãn" else "Không tìm thấy nhãn",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(visible, key = { it.id }) { tag ->
                        TagCard(
                            tag = tag,
                            noteCount = viewModel.noteCount(tag.name),
                            onEdit = {
                                editing = tag
                                editorError = null
                                showEditor = true
                            },
                            onDelete = { pendingDelete = tag }
                        )
                    }
                }
            }
        }
    }

    if (showEditor) {
        TagEditorDialog(
            title = if (editing == null) "Thêm nhãn" else "Sửa nhãn",
            initialName = editing?.name.orEmpty(),
            initialColor = editing?.color ?: tagColors.first(),
            error = editorError,
            onDismiss = {
                showEditor = false
                editorError = null
            },
            onSave = { name, color ->
                val error = viewModel.save(editing?.id, name, color)
                if (error == null) {
                    showEditor = false
                    editorError = null
                } else {
                    editorError = error
                }
            }
        )
    }

    pendingDelete?.let { tag ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Xóa nhãn") },
            text = { Text("Nhãn \"${tag.name}\" sẽ được gỡ khỏi các ghi chú đang dùng.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(tag.id)
                    pendingDelete = null
                }) {
                    Text("Xóa")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text("Hủy")
                }
            }
        )
    }
}
