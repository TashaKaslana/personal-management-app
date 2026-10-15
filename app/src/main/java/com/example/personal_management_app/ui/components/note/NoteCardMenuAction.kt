package com.example.personal_management_app.ui.components.note


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.personal_management_app.ui.screen.note_screen.NotePanelType
import com.example.personal_management_app.utils.toComposeColor
import com.example.personal_management_app.viewmodel.NoteEditViewModel
import com.example.personal_management_app.viewmodel.TagViewModel

@Composable
fun NoteCardMenuAction(
    isExpanded: Boolean,
    setIsExpanded: (Boolean) -> Unit,
    viewModel: NoteEditViewModel? = null,
    tagViewModel: TagViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var showTagDialog by remember { mutableStateOf(false) }
    var showPanelDialog by remember { mutableStateOf(false) }
    val tags by tagViewModel.tags.collectAsStateWithLifecycle()
    val focusedPanelId = viewModel?.focusedPanelId()

    NoteDropdownMenu(
        expanded = isExpanded,
        onDismissRequest = { setIsExpanded(false) }
    ) {
        NoteDropdownItem(
            label = "Delete",
            icon = Icons.Filled.Delete,
            destructive = true,
            onClick = {
                viewModel?.delete()
                setIsExpanded(false)
            }
        )
        NoteMenuSeparator()
        NoteDropdownItem(
            label = "Add Tag",
            icon = Icons.AutoMirrored.Filled.Label,
            onClick = {
                showTagDialog = true
                setIsExpanded(false)
            }
        )
        NoteDropdownItem(
            label = "Copy",
            icon = Icons.Filled.ContentCopy,
            onClick = {
                viewModel?.copy()
                Toast.makeText(context, "Đã sao chép ghi chú", Toast.LENGTH_SHORT).show()
                setIsExpanded(false)
            }
        )
        NoteDropdownItem(
            label = if (viewModel?.showCheckbox == true) "Hide checkbox" else "Show checkbox",
            icon = Icons.Filled.CheckBox,
            onClick = {
                viewModel?.toggleShowCheckbox()
                setIsExpanded(false)
            }
        )
        if (focusedPanelId != null) {
            NoteMenuSeparator()
            NoteDropdownItem(
                label = "Panel type",
                icon = Icons.Filled.Dashboard,
                onClick = {
                    showPanelDialog = true
                    setIsExpanded(false)
                }
            )
        }
    }

    if (showPanelDialog && focusedPanelId != null) {
        NotePanelTypeDialog(
            selected = viewModel.panelType(focusedPanelId) ?: NotePanelType.Inherit,
            onSelect = { viewModel.updatePanelType(focusedPanelId, it) },
            onDismiss = { showPanelDialog = false }
        )
    }

    if (showTagDialog) {
        val selected = viewModel?.note?.tag
        var query by remember { mutableStateOf("") }
        val visible = remember(tags, query) {
            tags.filter { it.name.contains(query.trim(), ignoreCase = true) }
        }
        AlertDialog(
            onDismissRequest = { showTagDialog = false },
            title = { Text("Nhãn") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        label = { Text("Tìm nhãn") },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Column(
                        modifier = Modifier
                            .heightIn(max = 280.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        if (tags.isEmpty()) {
                            Text("Chưa có nhãn. Tạo nhãn từ mục Nhãn.")
                        } else if (visible.isEmpty()) {
                            Text("Không tìm thấy nhãn", color = Color.Gray)
                        }
                        visible.forEach { tag ->
                            val isSelected = tag.name == selected
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel?.updateTag(tag.name)
                                        viewModel?.upsertNote()
                                        showTagDialog = false
                                    }
                                    .padding(vertical = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(tag.color.toComposeColor())
                                        .border(1.dp, Color.LightGray, CircleShape)
                                )
                                Text(
                                    text = tag.name,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else null,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel?.updateTag("")
                    viewModel?.upsertNote()
                    showTagDialog = false
                }) {
                    Text("Bỏ nhãn")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTagDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}