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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.personal_management_app.repositories.TrashRepository
import com.example.personal_management_app.ui.components.MainTopBar
import com.example.personal_management_app.ui.components.TrashCard
import com.example.personal_management_app.ui.layouts.MainLayout

@Composable
fun TrashScreen(modifier: Modifier = Modifier, navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }

    val trashRepository = remember { TrashRepository() }
    val trashList = trashRepository.getTrashItems()

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
                text = "Các mục trong thùng rác sẽ bị xóa vĩnh viễn sau 7 ngày.",
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
                items(trashList) { item ->
                    TrashCard(
                        title = item.title,
                        content = item.content,
                        timeDeleted = item.timeDeleted,
                        backgroundColor = item.backgroundColor,
                        onClick = {}
                    )
                }
            }
        }
    }
}