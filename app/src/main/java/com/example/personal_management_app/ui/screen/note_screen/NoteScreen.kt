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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.personal_management_app.ui.layouts.MainLayout
import com.example.personal_management_app.viewmodel.NoteViewModel

@Composable
fun NoteScreen(modifier: Modifier = Modifier, navController: NavController, viewModel: NoteViewModel = hiltViewModel(),) {
    val notesList = viewModel.notes.collectAsStateWithLifecycle().value

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
                            Text(
                                text = note.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = contentColor
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            NoteContentPreview(
                                content = note.content,
                                contentColor = contentColor
                            )
                            if (note.tag != null) {
                                Spacer(modifier = Modifier.height(10.dp))
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
                        }
                    }
                }
            }
        }
    }
}
