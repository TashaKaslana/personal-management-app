package com.example.personal_management_app.ui.screen.note_screen

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.personal_management_app.ui.components.TagChip
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
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val tags by tagViewModel.tags.collectAsStateWithLifecycle()
    val tagColors = remember(tags) { tags.associate { it.name to it.color } }
    LaunchedEffect(tags) { viewModel.loadNotes() }
    val notesList = remember(notes) { viewModel.filterNotes(archived = false) }

    MainLayout (navController = navController) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ĐÃ GHIM",
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
                items(notesList) { note ->
                    //list of notes backgrounds
                    val contentColor = noteContentColor(note.backgroundColor)
                    NoteThemedSurface(
                        background = note.backgroundColor,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            Log.d(TAG, "NoteScreen: ${note.id}")
                            navController.navigate("notes/${note.id}/edit")
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            //title
                            Text(
                                text = buildStyledText(note.title, loadTitleSpans(note.content)),
                                style = note.titleStyle.toCompose().copy(
                                    color = contentColor,
                                    fontSize = when {
                                        //backwards compatibility
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
                            if (!note.tag.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                TagChip(
                                    name = note.tag,
                                    color = tagColors[note.tag]
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
