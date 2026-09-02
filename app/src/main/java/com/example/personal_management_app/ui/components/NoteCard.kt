package com.example.personal_management_app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.NotificationAdd
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

fun click() {
    print("Clicked!")
}

@Composable
fun NoteCard(
    title: String,
    titleStyle: TextStyle = MaterialTheme.typography.titleMedium,
    content: String,
    contentStyle: TextStyle = MaterialTheme.typography.bodySmall,
    theme: String = "",
    imgSrc: String = "",
    modifier: Modifier
) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
        ) {
            NoteCardActionsTop(
                onBackClick = { click() },
                onPinClick = { click() },
                onSetNotificationClick = { click() },
                onSetArchived = { click() }
            )

            NoteCardMainContent(
                modifier = Modifier.weight(1f),
                title = title,
                titleStyle = titleStyle,
                content = content,
                contentStyle = contentStyle
            )

            NoteCardActionsBottom(
                onAddClick = { click() },
                onMenuClick = { click() },
                onThemeClick = { click() },
                onStyleClick = { click() }
            )
        }
    }
}

@Composable
fun NoteCardText(
    content: String,
    style: TextStyle = MaterialTheme.typography.bodySmall,
    overflow: TextOverflow = TextOverflow.Ellipsis
) {
    Text(
        text = content,
        style = style,
        overflow = overflow
    )
}

@Composable
fun NoteCardMainContent(
    modifier: Modifier = Modifier,
    title: String,
    titleStyle: TextStyle = MaterialTheme.typography.titleMedium,
    content: String,
    contentStyle: TextStyle = MaterialTheme.typography.bodySmall,
    theme: String = "",
    imgSrc: String = "",
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        NoteCardText(
            content = title,
            style = titleStyle,
        )

        NoteCardText(
            content = content,
            style = contentStyle,
        )
    }
}

//Utilities
@Composable
fun NoteCardActionsTop(
    modifier: Modifier = Modifier,
    onBackClick: ()-> Unit,
    onPinClick: ()-> Unit,
    onSetNotificationClick: () -> Unit,
    onSetArchived: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        NoteCardActionIconButton(
            onClick = onBackClick,
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            description = "Back"
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NoteCardActionIconButton(
                onClick = onPinClick,
                icon = Icons.Filled.Bookmark,
                description = "Pin"
            )

            NoteCardActionIconButton(
                onClick = onSetNotificationClick,
                icon = Icons.Filled.NotificationAdd,
                description = "Add notification"
            )

            NoteCardActionIconButton(
                onClick = onSetArchived,
                icon = Icons.Filled.Archive,
                description = "Archive"
            )
        }
    }
}

@Composable
fun NoteCardActionIconButton(
    onClick: ()-> Unit,
    icon: ImageVector,
    description: String,
    modifier: Modifier = Modifier
) {
    FilledIconButton(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description
        )
    }
}

//Edit content inside
@Composable
fun NoteCardActionsBottom(
    onAddClick: () -> Unit,
    onThemeClick: () -> Unit,
    onStyleClick: () -> Unit,
    onMenuClick: () -> Unit,
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
            icon = Icons.Filled.AddBox,
            iconDesc = "Add new content",
            dropdownMenu = ::NoteCardMenuAction
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
    setIsExpanded: (Boolean) -> Unit
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = { setIsExpanded(false) }
    ) {
        DropdownMenuItem(
            text = {
                Text("Delete")
            },
            onClick = {
                setIsExpanded(false)
            }
        )

        DropdownMenuItem(
            text = {
                Text("Copy")
            },
            onClick = {
                setIsExpanded(false)
            }
        )

        DropdownMenuItem(
            text = {
                Text("Share")
            },
            onClick = {
                setIsExpanded(false)
            }
        )
    }
}