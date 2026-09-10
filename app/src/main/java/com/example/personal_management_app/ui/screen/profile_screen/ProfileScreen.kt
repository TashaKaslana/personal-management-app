package com.example.personal_management_app.ui.screen.profile_screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.personal_management_app.repositories.ProfileRepository
import com.example.personal_management_app.ui.components.ProfileCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    profileRepository: ProfileRepository = remember { ProfileRepository() }
) {
    val profile = profileRepository.getProfile()


    var nameInput by remember { mutableStateOf(profile.name) }
    var usernameInput by remember { mutableStateOf(profile.username) }
    var avatarInput by remember { mutableStateOf(profile.avatar ?: "") }


    var currentPasswordInput by remember { mutableStateOf("") }
    var newPasswordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }


    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isErrorState by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trang cá nhân", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFCF8F2))
            )
        },
        containerColor = Color(0xFFFCF8F2)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            ProfileCard(
                profile = profile,
                onEditAvatarClick = {
                    avatarInput = "https://example.com/avatar_${System.currentTimeMillis()}.png"
                    statusMessage = "Đã cập nhật ảnh đại diện mới!"
                    isErrorState = false
                }
            )


            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = { Text("Họ và tên") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = usernameInput,
                onValueChange = { usernameInput = it },
                label = { Text("Tên đăng nhập (Username)") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )


            OutlinedTextField(
                value = currentPasswordInput,
                onValueChange = { currentPasswordInput = it },
                label = { Text("Mật khẩu hiện tại") },
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = newPasswordInput,
                onValueChange = { newPasswordInput = it },
                label = { Text("Mật khẩu mới") },
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = confirmPasswordInput,
                onValueChange = { confirmPasswordInput = it },
                label = { Text("Nhập lại mật khẩu mới") },
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )


            if (statusMessage != null) {
                Text(
                    text = statusMessage!!,
                    color = if (isErrorState) Color.Red else Color(0xFF2E7D32),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }


            Box(modifier = Modifier.weight(1f))


            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (newPasswordInput.isNotEmpty() || confirmPasswordInput.isNotEmpty()) {
                            if (currentPasswordInput.isEmpty()) {
                                statusMessage = "Vui lòng nhập mật khẩu hiện tại!"
                                isErrorState = true
                                return@Button
                            }
                            if (newPasswordInput != confirmPasswordInput) {
                                statusMessage = "Mật khẩu mới không khớp nhau!"
                                isErrorState = true
                                return@Button
                            }
                        }

                        profileRepository.updateProfile(nameInput, usernameInput)
                        statusMessage = "Cập nhật thông tin thành công!"
                        isErrorState = false
                        currentPasswordInput = ""
                        newPasswordInput = ""
                        confirmPasswordInput = ""
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(text = "Lưu thay đổi", fontSize = 15.sp, color = Color.White)
                }

                OutlinedButton(
                    onClick = {
                        navController.navigate("login_screen") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(text = "Đăng xuất", fontSize = 15.sp, color = Color.Red)
                }
            }
        }
    }
}