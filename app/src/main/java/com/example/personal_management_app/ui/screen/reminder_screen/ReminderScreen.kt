package com.example.personal_management_app.ui.screen.reminder_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.personal_management_app.repositories.ReminderRepository
import com.example.personal_management_app.ui.components.ReminderCard
import com.example.personal_management_app.ui.components.ReminderSearchBar
import com.example.personal_management_app.ui.layouts.MainLayout

@Composable
fun ReminderScreen(modifier: Modifier = Modifier, navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }

    val reminderRepository = remember { ReminderRepository() }
    val reminderList = reminderRepository.getReminders()


    MainLayout(
        navController = navController,
        currentTab = 1,
        topBar = {}
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            ReminderSearchBar(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth()
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    text = "SẮP TỚI LỜI NHẮC",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalItemSpacing = 10.dp,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(reminderList) { reminder ->
                        ReminderCard(
                            title = reminder.title,
                            content = reminder.content,
                            timeText = reminder.timeText,
                            backgroundColor = reminder.backgroundColor,
                            isCheckList = reminder.isCheckList,
                            onClick = {}
                        )
                    }
                }
            }
        }
    }
}