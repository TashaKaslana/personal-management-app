package com.example.personal_management_app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    timeDeletedText: String = note.deletedAt?.let {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
    }?.let { "Đã xóa $it" } ?: "Đã xóa"
) {
    val contentColor = noteContentColor(note.backgroundColor)

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

            Text(
                text = timeDeletedText,
                fontSize = 11.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
