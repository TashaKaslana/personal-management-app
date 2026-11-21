package com.example.personal_management_app.ui.screen.trash

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.personal_management_app.entities.NoteEntity
import com.example.personal_management_app.ui.components.TrashCard
import com.example.personal_management_app.ui.components.main_layout.MainTopBar
import com.example.personal_management_app.ui.layouts.MainLayout
import com.example.personal_management_app.viewmodel.NoteViewModel

@Composable
fun TrashScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: NoteViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var noteToDeleteForever by remember { mutableStateOf<NoteEntity?>(null) }
    var showEmptyTrashDialog by remember { mutableStateOf(false) }

    val trashNotes by viewModel.trashNotes.collectAsStateWithLifecycle()
    val trashList = remember(trashNotes, searchQuery) {
        viewModel.filterTrash(query = searchQuery)
    }

    MainLayout(
        navController = navController,
        currentTab = -1,
        topBar = { onMenuClick ->
            MainTopBar(
                navController = navController,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                placeholderText = "Tìm kiếm trong thùng rác",
                onMenuClick = onMenuClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            if (trashNotes.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Các mục trong thùng rác sẽ bị xóa sau 30 ngày.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = { showEmptyTrashDialog = true },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteSweep,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Dọn sạch",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            if (trashList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 80.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = null,
                            tint = Color.Gray.copy(alpha = 0.5f),
                            modifier = Modifier.size(80.dp)
                        )
                        Text(
                            text = if (searchQuery.isBlank()) "Thùng rác trống" else "Không tìm thấy ghi chú phù hợp",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalItemSpacing = 10.dp,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(trashList, key = { it.id }) { item ->
                        TrashCard(
                            note = item,
                            onClick = {
                                navController.navigate("notes/${item.id}/edit")
                            },
                            onRestore = {
                                viewModel.restoreNote(item.id)
                                Toast.makeText(context, "Đã khôi phục ghi chú", Toast.LENGTH_SHORT).show()
                            },
                            onDeleteForever = {
                                noteToDeleteForever = item
                            }
                        )
                    }
                }
            }
        }
    }

    if (noteToDeleteForever != null) {
        AlertDialog(
            onDismissRequest = { noteToDeleteForever = null },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.DeleteForever,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(text = "Xóa vĩnh viễn ghi chú?")
            },
            text = {
                Text(text = "Ghi chú này sẽ bị xóa hoàn toàn khỏi thiết bị và không thể khôi phục lại.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val id = noteToDeleteForever?.id
                        if (id != null) {
                            viewModel.deleteNoteForever(id)
                            Toast.makeText(context, "Đã xóa vĩnh viễn ghi chú", Toast.LENGTH_SHORT).show()
                        }
                        noteToDeleteForever = null
                    }
                ) {
                    Text("Xóa vĩnh viễn", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { noteToDeleteForever = null }) {
                    Text("Hủy")
                }
            }
        )
    }

    if (showEmptyTrashDialog) {
        AlertDialog(
            onDismissRequest = { showEmptyTrashDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.DeleteSweep,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(text = "Dọn sạch thùng rác?")
            },
            text = {
                Text(text = "Tất cả ${trashNotes.size} ghi chú trong thùng rác sẽ bị xóa vĩnh viễn. Thao tác này không thể hoàn tác.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.emptyTrash()
                        showEmptyTrashDialog = false
                        Toast.makeText(context, "Đã dọn sạch thùng rác", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Dọn sạch", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEmptyTrashDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}