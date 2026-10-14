package com.example.personal_management_app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import com.example.personal_management_app.entites.NoteEntity
import com.example.personal_management_app.ui.screen.note_screen.NoteContentPreview
import com.example.personal_management_app.ui.screen.note_screen.NoteHeading
import com.example.personal_management_app.ui.screen.note_screen.NoteThemedSurface
import com.example.personal_management_app.ui.screen.note_screen.buildStyledText
import com.example.personal_management_app.ui.screen.note_screen.loadTitleSpans
import com.example.personal_management_app.ui.screen.note_screen.noteContentColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ArchiveCard(
    note: NoteEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    timeOrStatusText: String = note.archivedAt?.let {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
    } ?: "Đã lưu trữ"
) {
    val contentColor = noteContentColor(note.backgroundColor)

    NoteThemedSurface(
        background = note.backgroundColor,
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = buildStyledText(note.title, loadTitleSpans(note.content)),
                style = note.titleStyle.toCompose().copy(
                    color = contentColor,
                    fontSize = when {
                        note.titleStyle.fontSize >= NoteHeading.H1.fontSize -> 16.sp
                        note.titleStyle.fontSize >= NoteHeading.H2.fontSize -> 14.sp
                        else -> 13.sp
                    }
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            NoteContentPreview(
                content = note.content,
                contentColor = contentColor
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                if (!note.tag.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = note.tag,
                            fontSize = 10.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.6f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Archive,
                            contentDescription = null,
                            modifier = Modifier.size(11.dp),
                            tint = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = timeOrStatusText,
                            fontSize = 10.sp,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}