package com.example.personal_management_app.ui.screen.note_screen

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.personal_management_app.entities.NoteEntity
import com.example.personal_management_app.ui.components.EmptySearchState
import com.example.personal_management_app.ui.components.main_layout.MainTopBar
import com.example.personal_management_app.ui.components.note.NoteContentPreview
import com.example.personal_management_app.ui.components.note.NoteHeading
import com.example.personal_management_app.ui.components.note.NoteThemedSurface
import com.example.personal_management_app.ui.components.note.buildStyledText
import com.example.personal_management_app.ui.components.note.loadTitleSpans
import com.example.personal_management_app.ui.components.note.noteContentColor
import com.example.personal_management_app.ui.components.tag.TagChip
import com.example.personal_management_app.ui.layouts.MainLayout
import com.example.personal_management_app.viewmodel.NoteViewModel
import com.example.personal_management_app.viewmodel.TagViewModel

@Composable
fun NoteScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: NoteViewModel = hiltViewModel(),
    tagViewModel: TagViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf<String?>(null) }

    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val tags by tagViewModel.tags.collectAsStateWithLifecycle()
    val tagColors = remember(tags) { tags.associate { it.name to it.color } }

    LaunchedEffect(tags) { viewModel.loadNotes() }

    val filteredNotes = remember(notes, searchQuery, selectedTag) {
        viewModel.filterNotes(archived = false, query = searchQuery, selectedTag = selectedTag)
    }

    val isSearchOrFilterActive = searchQuery.isNotBlank() || selectedTag != null
    val pinnedNotes = remember(filteredNotes) { filteredNotes.filter { it.isPinned } }
    val otherNotes = remember(filteredNotes) { filteredNotes.filter { !it.isPinned } }

    MainLayout(
        navController = navController,
        currentTab = 0,
        topBar = { onMenuClick ->
            MainTopBar(
                navController = navController,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                placeholderText = "Tìm kiếm ghi chú",
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
            Spacer(modifier = Modifier.height(8.dp))

            // Quick filter chips by tag
            if (tags.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedTag == null,
                            onClick = { selectedTag = null },
                            label = { Text("Tất cả", fontSize = 12.sp) },
                            shape = RoundedCornerShape(16.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFE8F0FE),
                                selectedLabelColor = Color(0xFF1A73E8)
                            )
                        )
                    }
                    items(tags, key = { it.id }) { tag ->
                        val isSelected = selectedTag == tag.name
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedTag = if (isSelected) null else tag.name
                            },
                            label = { Text(tag.name, fontSize = 12.sp) },
                            shape = RoundedCornerShape(16.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFE8F0FE),
                                selectedLabelColor = Color(0xFF1A73E8)
                            )
                        )
                    }
                }
            }

            if (filteredNotes.isEmpty()) {
                EmptySearchState(
                    query = searchQuery,
                    onClear = {
                        searchQuery = ""
                        selectedTag = null
                    },
                    emptyMessage = if (isSearchOrFilterActive) {
                        "Không tìm thấy ghi chú phù hợp"
                    } else {
                        "Chưa có ghi chú nào"
                    }
                )
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalItemSpacing = 10.dp,
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (isSearchOrFilterActive) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            Text(
                                text = "KẾT QUẢ TÌM KIẾM (${filteredNotes.size})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                            )
                        }
                        items(filteredNotes, key = { it.id }) { note ->
                            NoteItemCard(
                                note = note,
                                tagColors = tagColors,
                                onClick = {
                                    Log.d(TAG, "NoteScreen: ${note.id}")
                                    navController.navigate("notes/${note.id}/edit")
                                }
                            )
                        }
                    } else {
                        if (pinnedNotes.isNotEmpty()) {
                            item(span = StaggeredGridItemSpan.FullLine) {
                                Text(
                                    text = "ĐÃ GHIM (${pinnedNotes.size})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                                )
                            }
                            items(pinnedNotes, key = { it.id }) { note ->
                                NoteItemCard(
                                    note = note,
                                    tagColors = tagColors,
                                    onClick = {
                                        Log.d(TAG, "NoteScreen: ${note.id}")
                                        navController.navigate("notes/${note.id}/edit")
                                    }
                                )
                            }
                        }

                        if (otherNotes.isNotEmpty()) {
                            if (pinnedNotes.isNotEmpty()) {
                                item(span = StaggeredGridItemSpan.FullLine) {
                                    Text(
                                        text = "KHÁC (${otherNotes.size})",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                                    )
                                }
                            }
                            items(otherNotes, key = { it.id }) { note ->
                                NoteItemCard(
                                    note = note,
                                    tagColors = tagColors,
                                    onClick = {
                                        Log.d(TAG, "NoteScreen: ${note.id}")
                                        navController.navigate("notes/${note.id}/edit")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NoteItemCard(
    note: NoteEntity,
    tagColors: Map<String, String>,
    onClick: () -> Unit
) {
    val contentColor = noteContentColor(note.backgroundColor)
    NoteThemedSurface(
        background = note.backgroundColor,
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = buildStyledText(
                    note.title,
                    loadTitleSpans(note.content)
                ),
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
                        TagChip(
                            name = name,
                            color = tagColors[name]
                        )
                    }
                }
            }
        }
    }
}
