package com.example.personal_management_app.ui.components.note

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.personal_management_app.dtos.NoteEditDto
import com.example.personal_management_app.dtos.toCompose

@Composable
fun NoteCardTextEditor(
    modifier: Modifier = Modifier,
    note: NoteEditDto,
    onTitleUpdate: (String) -> Unit,
    onContentUpdate: (String) -> Unit,
    bodyContent: (@Composable () -> Unit)? = null,
) {
    val scrollState = rememberScrollState()
    var titleState by remember { mutableStateOf(note.title) }
    var bodyState by remember { mutableStateOf(note.content) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(scrollState),
    ) {
        //title
        BasicTextField(
            value = titleState,
            onValueChange = {
                titleState = it
                onTitleUpdate(it)
            },
            textStyle = note.titleStyle.toCompose(),
            modifier = Modifier
                .fillMaxWidth()
        )

        if (bodyContent != null) {
            bodyContent()
        } else {
            BasicTextField(
                value = bodyState,
                onValueChange = {
                    bodyState = it
                    onContentUpdate(it)
                },
                textStyle = note.contentStyle.toCompose(),
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
    onSetArchived: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
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
    onClick: () -> Unit,
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