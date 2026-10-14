package com.example.personal_management_app.ui.screen.trash_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.personal_management_app.ui.components.MainTopBar
import com.example.personal_management_app.ui.components.TrashCard
import com.example.personal_management_app.ui.layouts.MainLayout
import com.example.personal_management_app.viewmodel.NoteViewModel

@Composable
fun TrashScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: NoteViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }

    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val trashList = remember(notes, searchQuery) {
        viewModel.filterTrash(query = searchQuery)
    }

    MainLayout(
        navController = navController,
        currentTab = -1,
        topBar = { onMenuClick ->
            MainTopBar(
                navController = navController,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                placeholderText = "Tìm kiếm trong thùng rác",
                onMenuClick = onMenuClick
            )
        }
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Các mục trong thùng rác sẽ bị xóa vĩnh viễn sau 30 ngày.",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalItemSpacing = 10.dp,
                modifier = Modifier.fillMaxSize()
            ) {
                items(trashList, key = { it.id }) { item ->
                    TrashCard(
                        note = item,
                        onClick = {
                            navController.navigate("notes/${item.id}/edit")
                        }
                    )
                }
            }
        }
    }
}