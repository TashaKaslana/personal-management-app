package com.example.personal_management_app.ui.components.note

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.widget.Toast
import com.example.personal_management_app.viewmodel.NoteEditViewModel


@Composable
fun NoteCardActionsBottom(
    onAddClick: () -> Unit,
    onThemeClick: () -> Unit,
    onStyleClick: () -> Unit,
    onMenuClick: () -> Unit,
    viewModel: NoteEditViewModel? = null,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            NoteCardActionDropdown(
                icon = Icons.Filled.AddBox,
                iconDesc = "Add new content",
                dropdownMenu = ::NoteCardAddingAction
            )

            NoteCardActionIconButton(
                icon = Icons.Filled.Palette,
                onClick = onAddClick,
                description = "Add new theme"
            )

            NoteCardActionIconButton(
                icon = Icons.Filled.Palette,
                onClick = onAddClick,
                description = "Add new theme"
            )
        }

        NoteCardActionDropdown(
            icon = Icons.Filled.Menu,
            iconDesc = "Note menu",
            dropdownMenu = { expanded, setExpanded ->
                NoteCardMenuAction(
                    isExpanded = expanded,
                    setIsExpanded = setExpanded,
                    viewModel = viewModel
                )
            }
        )
    }
}

@Composable
fun NoteCardActionDropdown(
    icon: ImageVector,
    iconDesc: String,
    dropdownMenu: @Composable (
        Boolean,
        (Boolean) -> Unit
    ) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box {
        FilledIconButton(
            onClick = { isExpanded = true },
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = iconDesc
            )
        }

        dropdownMenu(isExpanded) { isExpanded = it }
    }
}

@Composable
fun NoteCardAddingAction(
    isExpanded: Boolean,
    setIsExpanded: (Boolean) -> Unit
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = { setIsExpanded(false) }
    ) {
        DropdownMenuItem(
            text = {
                Text("CheckBox")
            },
            onClick = {
                setIsExpanded(false)
            }
        )

        DropdownMenuItem(
            text = {
                Text("Add Image")
            },
            onClick = {
                setIsExpanded(false)
            }
        )
    }
}

@Composable
fun NoteCardMenuAction(
    isExpanded: Boolean,
    setIsExpanded: (Boolean) -> Unit,
    viewModel: NoteEditViewModel? = null,
) {
    val context = LocalContext.current
    var showTagDialog by remember { mutableStateOf(false) }
    var tagInput by remember { mutableStateOf(viewModel?.note?.tag.orEmpty()) }

    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = { setIsExpanded(false) }
    ) {
        DropdownMenuItem(
            text = {
                Text("Delete")
            },
            onClick = {
                viewModel?.delete()
                setIsExpanded(false)
            }
        )

        DropdownMenuItem(
            text = {
                Text("Add Tag")
            },
            onClick = {
                tagInput = viewModel?.note?.tag.orEmpty()
                showTagDialog = true
                setIsExpanded(false)
            }
        )

        DropdownMenuItem(
            text = {
                Text("Copy")
            },
            onClick = {
                viewModel?.copy()
                Toast.makeText(context, "Đã sao chép ghi chú", Toast.LENGTH_SHORT).show()
                setIsExpanded(false)
            }
        )

        DropdownMenuItem(
            text = {
                Text(if (viewModel?.showCheckbox == true) "Hide checkbox" else "Show checkbox")
            },
            onClick = {
                viewModel?.toggleShowCheckbox()
                setIsExpanded(false)
            }
        )
    }

    if (showTagDialog) {
        AlertDialog(
            onDismissRequest = { showTagDialog = false },
            title = { Text("Add Tag") },
            text = {
                OutlinedTextField(
                    value = tagInput,
                    onValueChange = { tagInput = it },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel?.updateTag(tagInput)
                        viewModel?.updateNote()
                        showTagDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTagDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}