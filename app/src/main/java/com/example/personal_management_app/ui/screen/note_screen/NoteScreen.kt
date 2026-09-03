package com.example.personal_management_app.ui.screen.note_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.personal_management_app.ui.components.NoteCardPreview
import com.example.personal_management_app.ui.layouts.MainLayout

@Composable
fun NoteScreen(modifier: Modifier = Modifier, navController: NavController) {
    val content =
        "I am going to do it. I have made up my mind. These are the first few words of the new… the best … the Longest Text In The Entire History Of The Known Universe! This Has To Have Over 35,000 words the beat the current world record set by that person who made that flaming chicken handbooky thingy. I might just be saying random things the whole time I type in this so you might get confused a lot. I just discovered something terrible. autocorrect is on!! no!!!"
    val list = listOf(
        listOf("title 1", content),
        listOf("title 2", content),
        listOf("title 3", content),
        listOf("title 4", content),
        listOf("title 5", content)
    )

    MainLayout (navController = navController) { innerPadding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier
                .padding(innerPadding)
                .padding(12.dp)
        ) {
            items(list) {
                NoteCardPreview(
                    title = it[0],
                    content = it[1],
                    modifier = Modifier.clickable(
                        onClick = {
                            navController.navigate("note_edit_screen")
                        }
                    )
                )
            }
        }
    }
}
