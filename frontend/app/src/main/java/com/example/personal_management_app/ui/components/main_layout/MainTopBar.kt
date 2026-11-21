package com.example.personal_management_app.ui.components.main_layout

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.personal_management_app.utils.getInitials
import com.example.personal_management_app.utils.loadAvatarBitmap
import com.example.personal_management_app.viewmodel.ProfileViewModel

@Composable
fun MainTopBar(
    navController: NavController,
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    placeholderText: String = "Tìm kiếm ghi chú",
    onMenuClick: () -> Unit = {},
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val profile by profileViewModel.profileFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val avatarBitmap = remember(profile?.avatar) {
        loadAvatarBitmap(context, profile?.avatar)
    }

    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        placeholder = { Text(placeholderText, color = Color.Gray, fontSize = 14.sp) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboardController?.hide()
                focusManager.clearFocus()
            }
        ),
        leadingIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Menu Drawer", tint = Color.DarkGray)
            }
        },
        trailingIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(end = 6.dp)
            ) {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { onSearchQueryChange("") },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Xóa tìm kiếm",
                            tint = Color.DarkGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = Color.DarkGray
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = Color(0xFFD7CCC8),
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable {
                            navController.navigate("profile_screen")
                        }
                ) {
                    val currentName = profile?.name
                    if (avatarBitmap != null) {
                        Image(
                            bitmap = avatarBitmap,
                            contentDescription = "Ảnh đại diện",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(50)),
                            contentScale = ContentScale.Crop
                        )
                    } else if (!currentName.isNullOrBlank()) {
                        Box(contentAlignment = Alignment.Center) {
                            val initials = getInitials(currentName)
                            Text(
                                text = initials,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray
                            )
                        }
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Tài khoản",
                                tint = Color.DarkGray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        },
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    )
}
