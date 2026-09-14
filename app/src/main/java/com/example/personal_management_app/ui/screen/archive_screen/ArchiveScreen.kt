package com.example.personal_management_app.ui.screen.archive_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.personal_management_app.repositories.ArchiveRepository
import com.example.personal_management_app.ui.components.ArchiveCard
import com.example.personal_management_app.ui.components.MainTopBar
import com.example.personal_management_app.ui.layouts.MainLayout

@Composable
fun ArchiveScreen(modifier: Modifier = Modifier, navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }

    val archiveRepository = remember { ArchiveRepository() }
    val archiveList = archiveRepository.getArchives()

    MainLayout(
        navController = navController,
        currentTab = 2,
        topBar = { onMenuClick ->
            MainTopBar(
                navController = navController,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                placeholderText = "Tìm kiếm trong mục lưu trữ",
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
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalItemSpacing = 10.dp,
                modifier = Modifier.fillMaxSize()
            ) {
                items(archiveList) { archive ->
                    ArchiveCard(
                        title = archive.title,
                        content = archive.content,
                        tag = archive.tag,
                        timeOrStatusText = archive.timeOrStatusText,
                        backgroundColor = archive.backgroundColor,
                        isCheckList = archive.isCheckList,
                        onClick = {}
                    )
                }
            }
        }
    }
}