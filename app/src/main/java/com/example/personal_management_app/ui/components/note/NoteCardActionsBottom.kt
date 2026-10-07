package com.example.personal_management_app.ui.components.note

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatClear
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.example.personal_management_app.ui.screen.note_screen.NoteHeading
import com.example.personal_management_app.ui.screen.note_screen.NoteInlineStyle
import com.example.personal_management_app.ui.screen.note_screen.NotePanelType
import com.example.personal_management_app.ui.screen.note_screen.NoteThemeDialog
import com.example.personal_management_app.ui.screen.note_screen.notePanelTypes
import com.example.personal_management_app.viewmodel.NoteEditViewModel


@Composable
fun NoteCardActionsBottom(
    viewModel: NoteEditViewModel? = null,
) {
    var showThemeDialog by remember { mutableStateOf(false) }
    var showStyleMenu by remember { mutableStateOf(false) }
    val focusedPanelId = viewModel?.focusedPanelId()

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
                dropdownMenu = { expanded, setExpanded ->
                    NoteCardAddingAction(
                        isExpanded = expanded,
                        setIsExpanded = setExpanded,
                        viewModel = viewModel
                    )
                }
            )

            NoteCardActionIconButton(
                icon = Icons.Filled.Palette,
                onClick = {
                    showThemeDialog = true
                },
                description = "Change theme"
            )

            //text style dialog
            Box {
                NoteCardActionIconButton(
                    icon = Icons.Filled.Style,
                    onClick = {
                        showStyleMenu = true
                    },
                    description = "Change style",
                    modifier = Modifier.focusProperties { canFocus = false }
                )
                NoteStyleMenu(
                    expanded = showStyleMenu,
                    onDismiss = { showStyleMenu = false },
                    heading = viewModel?.focusedHeading(),
                    bold = viewModel?.selectionHas(NoteInlineStyle.Bold) == true,
                    italic = viewModel?.selectionHas(NoteInlineStyle.Italic) == true,
                    underline = viewModel?.selectionHas(NoteInlineStyle.Underline) == true,
                    onHeading = { viewModel?.applyHeading(it) },
                    onInline = { viewModel?.toggleInlineStyle(it) },
                    onRemoveFormat = { viewModel?.removeFormat() }
                )
            }
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

    if (showThemeDialog) {
        if (focusedPanelId != null) {
            NoteThemeDialog(
                selected = viewModel.panelBackground(focusedPanelId).orEmpty(),
                onSelect = { viewModel.updatePanelBackground(focusedPanelId, it) },
                onInherit = { viewModel.updatePanelBackground(focusedPanelId, null) },
                onDismiss = { showThemeDialog = false }
            )
        } else {
            NoteThemeDialog(
                selected = viewModel?.note?.backgroundColor.orEmpty(),
                onSelect = {
                    viewModel?.updateBackground(it)
//                showThemeDialog = false // it should be preview state instead close immediately
                },
                onDismiss = { showThemeDialog = false }
            )
        }
    }
}

@Composable
private fun NoteStyleMenu(
    expanded: Boolean,
    heading: NoteHeading?,
    bold: Boolean,
    italic: Boolean,
    underline: Boolean,
    onDismiss: () -> Unit,
    onHeading: (NoteHeading) -> Unit,
    onInline: (NoteInlineStyle) -> Unit,
    onRemoveFormat: () -> Unit
) {
    NoteDropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = false)
    ) {
        StyleMenuItem("H1", heading == NoteHeading.H1, leading = { HeadingBadge("H1") }) {
            onHeading(NoteHeading.H1)
        }
        StyleMenuItem("H2", heading == NoteHeading.H2, leading = { HeadingBadge("H2") }) {
            onHeading(NoteHeading.H2)
        }
        StyleMenuItem("Normal", heading == NoteHeading.Normal, leading = { HeadingBadge("¶") }) {
            onHeading(NoteHeading.Normal)
        }
        NoteMenuSeparator()
        StyleMenuItem("Bold", bold, leading = { Icon(Icons.Filled.FormatBold, contentDescription = null) }) {
            onInline(NoteInlineStyle.Bold)
        }
        StyleMenuItem("Italic", italic, leading = { Icon(Icons.Filled.FormatItalic, contentDescription = null) }) {
            onInline(NoteInlineStyle.Italic)
        }
        StyleMenuItem("Underline", underline, leading = { Icon(Icons.Filled.FormatUnderlined, contentDescription = null) }) {
            onInline(NoteInlineStyle.Underline)
        }
        NoteMenuSeparator()
        StyleMenuItem(
            "Remove format",
            false,
            leading = { Icon(Icons.Filled.FormatClear, contentDescription = null) },
            onClick = onRemoveFormat
        )
    }
}

@Composable
private fun StyleMenuItem(
    label: String,
    selected: Boolean,
    leading: @Composable () -> Unit,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Text(
                label,
                fontWeight = if (selected) FontWeight.SemiBold else null
            )
        },
        leadingIcon = leading,
        trailingIcon = if (selected) {
            {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        } else null,
        onClick = onClick,
        modifier = Modifier.focusProperties { canFocus = false }
    )
}

@Composable
private fun HeadingBadge(label: String) {
    Box(
        modifier = Modifier.size(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun NoteDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    properties: PopupProperties = PopupProperties(),
    content: @Composable ColumnScope.() -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        properties = properties,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 6.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        content = content
    )
}

@Composable
private fun NoteMenuSeparator() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

@Composable
private fun NoteDropdownItem(
    label: String,
    icon: ImageVector? = null,
    destructive: Boolean = false,
    onClick: () -> Unit
) {
    val contentColor = if (destructive) MaterialTheme.colorScheme.error
    else MaterialTheme.colorScheme.onSurface

    DropdownMenuItem(
        text = { Text(label, color = contentColor) },
        leadingIcon = if (icon != null) {
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor
                )
            }
        } else null,
        onClick = onClick
    )
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
    setIsExpanded: (Boolean) -> Unit,
    viewModel: NoteEditViewModel? = null,
) {
    var showImageDialog by remember { mutableStateOf(false) }
    var imageInput by remember { mutableStateOf("") }

    NoteDropdownMenu(
        expanded = isExpanded,
        onDismissRequest = { setIsExpanded(false) }
    ) {
        NoteDropdownItem(
            label = "CheckBox",
            icon = Icons.Filled.CheckBox,
            onClick = {
                viewModel?.addCheckbox()
                setIsExpanded(false)
            }
        )
        NoteMenuSeparator()
        NoteDropdownItem(
            label = "Add Image",
            icon = Icons.Filled.Image,
            onClick = {
                imageInput = ""
                showImageDialog = true
                setIsExpanded(false)
            }
        )
        NoteMenuSeparator()
        NoteDropdownItem(
            label = "Panel",
            icon = Icons.Filled.Dashboard,
            onClick = {
                viewModel?.addModelBox()
                setIsExpanded(false)
            }
        )
    }

    if (showImageDialog) {
        AlertDialog(
            onDismissRequest = { showImageDialog = false },
            title = { Text("Add Image") },
            text = {
                OutlinedTextField(
                    value = imageInput,
                    onValueChange = { imageInput = it },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel?.addImage(imageInput)
                        showImageDialog = false
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImageDialog = false }) {
                    Text("Cancel")
                }
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
    var showPanelDialog by remember { mutableStateOf(false) }
    var tagInput by remember { mutableStateOf(viewModel?.note?.tag.orEmpty()) }
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
                tagInput = viewModel?.note?.tag.orEmpty()
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
                        viewModel?.upsertNote()
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

@Composable
fun NotePanelTypeDialog(
    selected: NotePanelType,
    onSelect: (NotePanelType) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Panel type") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                notePanelTypes.forEach { type ->
                    NotePanelTypeRow(
                        label = type.label,
                        selected = type == selected,
                        onClick = {
                            onSelect(type)
                            onDismiss()
                        }
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun NotePanelTypeRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontWeight = if (selected) FontWeight.SemiBold else null
        )
        if (selected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}