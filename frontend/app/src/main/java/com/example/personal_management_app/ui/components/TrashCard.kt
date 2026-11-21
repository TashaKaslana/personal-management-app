package com.example.personal_management_app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.RestoreFromTrash
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.personal_management_app.dtos.toCompose
import com.example.personal_management_app.entities.NoteEntity
import com.example.personal_management_app.ui.components.note.NoteContentPreview
import com.example.personal_management_app.ui.components.note.NoteHeading
import com.example.personal_management_app.ui.components.note.NoteThemedSurface
import com.example.personal_management_app.ui.components.note.buildStyledText
import com.example.personal_management_app.ui.components.note.loadTitleSpans
import com.example.personal_management_app.ui.components.note.noteContentColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TrashCard(
    note: NoteEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onRestore: () -> Unit = {},
    onDeleteForever: () -> Unit = {}
) {
    val contentColor = noteContentColor(note.backgroundColor)

    val deletedTimestamp = note.deletedAt ?: System.currentTimeMillis()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dateDeletedString = dateFormat.format(Date(deletedTimestamp))

    val retentionMillis = 30L * 24L * 60L * 60L * 1000L
    val oneDayMillis = 24L * 60L * 60L * 1000L
    val deadline = deletedTimestamp + retentionMillis
    val remainingMillis = deadline - System.currentTimeMillis()
    val daysRemaining = if (remainingMillis <= 0L) {
        0
    } else {
        ((remainingMillis + oneDayMillis - 1) / oneDayMillis).coerceIn(0L, 30L).toInt()
    }

    val countdownText = when {
        daysRemaining <= 0 -> "Sắp xóa vĩnh viễn hôm nay"
        daysRemaining == 1 -> "Còn 1 ngày nữa sẽ xóa"
        else -> "Còn $daysRemaining ngày nữa sẽ xóa"
    }

    NoteThemedSurface(
        background = note.backgroundColor,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
        onClick = onClick
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            if (note.title.isNotBlank()) {
                Text(
                    text = buildStyledText(note.title, loadTitleSpans(note.content)),
                    style = note.titleStyle.toCompose().copy(
                        color = contentColor,
                        fontSize = when {
                            note.titleStyle.fontSize >= NoteHeading.H1.fontSize -> 14.sp
                            note.titleStyle.fontSize >= NoteHeading.H2.fontSize -> 13.sp
                            else -> 12.sp
                        },
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            NoteContentPreview(
                content = note.content,
                contentColor = contentColor
            )

            HorizontalDivider(
                modifier = Modifier.padding(top = 2.dp),
                color = contentColor.copy(alpha = 0.12f)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Đã xóa: $dateDeletedString",
                    fontSize = 11.sp,
                    color = contentColor.copy(alpha = 0.65f),
                    fontWeight = FontWeight.Medium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = if (daysRemaining <= 3) MaterialTheme.colorScheme.error else contentColor.copy(alpha = 0.7f),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = countdownText,
                        fontSize = 11.sp,
                        color = if (daysRemaining <= 3) MaterialTheme.colorScheme.error else contentColor.copy(alpha = 0.75f),
                        fontWeight = if (daysRemaining <= 3) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onRestore,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.RestoreFromTrash,
                        contentDescription = "Khôi phục",
                        tint = contentColor.copy(alpha = 0.85f),
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(
                    onClick = onDeleteForever,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteForever,
                        contentDescription = "Xóa vĩnh viễn",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.85f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
