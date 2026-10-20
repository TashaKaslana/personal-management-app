package com.example.personal_management_app.ui.screen.reminder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.personal_management_app.dtos.toCompose
import com.example.personal_management_app.ui.components.main_layout.MainTopBar
import com.example.personal_management_app.ui.components.tag.TagChip
import com.example.personal_management_app.ui.layouts.MainLayout
import com.example.personal_management_app.ui.components.note.NoteContentPreview
import com.example.personal_management_app.ui.components.note.NoteHeading
import com.example.personal_management_app.ui.components.note.NoteThemedSurface
import com.example.personal_management_app.ui.components.note.buildStyledText
import com.example.personal_management_app.ui.components.reminder.label
import com.example.personal_management_app.ui.components.note.loadTitleSpans
import com.example.personal_management_app.ui.components.note.noteContentColor
import com.example.personal_management_app.viewmodel.NoteViewModel
import com.example.personal_management_app.viewmodel.TagViewModel

@Composable
fun ReminderScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: NoteViewModel = hiltViewModel(),
    tagViewModel: TagViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val tags by tagViewModel.tags.collectAsStateWithLifecycle()
    val tagColors = remember(tags) { tags.associate { it.name to it.color } }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.loadNotes()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    val reminderList = remember(notes, searchQuery) {
        viewModel.filterReminders(searchQuery)
    }

    MainLayout(
        navController = navController,
        currentTab = 1,
        topBar = { onMenuClick ->
            MainTopBar(
                navController = navController,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                placeholderText = "Tìm kiếm lời nhắc",
                onMenuClick = onMenuClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "SẮP TỚI",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalItemSpacing = 10.dp,
                modifier = Modifier.fillMaxSize()
            ) {
                items(reminderList, key = { it.id }) { note ->
                    val contentColor = noteContentColor(note.backgroundColor)
                    NoteThemedSurface(
                        background = note.backgroundColor,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { navController.navigate("notes/${note.id}/edit") }
                    ) {
                        Column(
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

                            Spacer(modifier = Modifier.height(6.dp))
                            NoteContentPreview(
                                content = note.content,
                                contentColor = contentColor
                            )

                            if (note.tags.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    maxLines = 2
                                ) {
                                    note.tags.forEach { name ->
                                        TagChip(name = name, color = tagColors[name])
                                    }
                                }
                            }

                            note.reminder?.let { reminder ->
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White.copy(alpha = 0.6f)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.AccessTime,
                                            contentDescription = null,
                                            modifier = Modifier.size(11.dp),
                                            tint = Color.DarkGray
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = reminder.label(),
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
            }
        }
    }
}
