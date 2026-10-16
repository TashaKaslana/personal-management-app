package com.example.personal_management_app.ui.components.note

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.NotificationAdd
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.personal_management_app.dtos.NoteEditDto
import com.example.personal_management_app.dtos.toCompose
import com.example.personal_management_app.ui.screen.note_screen.NoteStyledField
import com.example.personal_management_app.ui.screen.note_screen.NoteTextSpan

@Composable
fun NoteCardTextEditor(
    modifier: Modifier = Modifier,
    note: NoteEditDto,
    onTitleUpdate: (String, Int, Int) -> Unit,
    onContentUpdate: (String) -> Unit,
    titleSpans: List<NoteTextSpan> = emptyList(),
    contentColor: Color = Color.Black,
    bodyContent: (@Composable () -> Unit)? = null,
    readOnly: Boolean = false,
) {
    val scrollState = rememberScrollState()
    var bodyState by remember { mutableStateOf(note.content) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .verticalScroll(scrollState),
    ) {
        //title
        NoteStyledField(
            text = note.title,
            spans = titleSpans,
            style = note.titleStyle.toCompose().copy(color = contentColor),
            onEdit = onTitleUpdate,
            fieldKey = note.id,
            readOnly = readOnly,
            modifier = Modifier.fillMaxWidth()
        )

        if (bodyContent != null) {
            bodyContent()
        } else {
            BasicTextField(
                value = bodyState,
                readOnly = readOnly,
                onValueChange = {
                    if (readOnly) return@BasicTextField
                    bodyState = it
                    onContentUpdate(it)
                },
                textStyle = note.contentStyle.toCompose().copy(color = contentColor),
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

//Utilities
@Composable
fun NoteCardActionsTop(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onPinClick: () -> Unit,
    onSetNotificationClick: () -> Unit,
    reminderSet: Boolean = false,
    onSetArchived: () -> Unit,
    showActions: Boolean = true
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        NoteCardActionIconButton(
            onClick = onBackClick,
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            description = "Back"
        )

        if (showActions) {
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
                    description = "Add reminder",
                    selected = reminderSet
                )

                NoteCardActionIconButton(
                    onClick = onSetArchived,
                    icon = Icons.Filled.Archive,
                    description = "Archive"
                )
            }
        }
    }
}

@Composable
fun NoteCardActionIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    description: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false
) {
    FilledIconButton(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = if (selected) {
            IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        } else {
            IconButtonDefaults.filledIconButtonColors()
        },
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description
        )
    }
}