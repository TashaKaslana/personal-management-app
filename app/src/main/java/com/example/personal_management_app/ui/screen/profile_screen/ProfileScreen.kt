package com.example.personal_management_app.ui.screen.profile_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.personal_management_app.repositories.ProfileRepository
import com.example.personal_management_app.ui.theme.FabContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    profileRepository: ProfileRepository = remember { ProfileRepository() }
) {
    val profile = profileRepository.getProfile()

    // Trạng thái form chỉnh sửa thông tin
    var nameInput by remember { mutableStateOf(profile.name) }
    var usernameInput by remember { mutableStateOf(profile.username) }
    var avatarInput by remember { mutableStateOf(profile.avatar ?: "") }

    // Trạng thái đổi mật khẩu
    var currentPasswordInput by remember { mutableStateOf("") }
    var newPasswordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") } // Thêm ô nhập lại mật khẩu mới

    // Trạng thái thông báo phản hồi
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
            // Phần Avatar
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .size(76.dp)
                    .padding(top = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(FabContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        modifier = Modifier.size(38.dp),
                        tint = Color.DarkGray
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp),
                    onClick = {
                        avatarInput = "https://example.com/avatar_${System.currentTimeMillis()}.png"
                        statusMessage = "Đã cập nhật ảnh đại diện mới!"
                        isErrorState = false
                    }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Đổi ảnh",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            // Hiển thị vai trò hệ thống
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.LightGray.copy(alpha = 0.4f)
            ) {
                Text(
                    text = "Vai trò: ${profile.role}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            // Form thông tin cơ bản
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

            // Các ô nhập mật khẩu
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

            // Thông báo trạng thái
            if (statusMessage != null) {
                Text(
                    text = statusMessage!!,
                    color = if (isErrorState) Color.Red else Color(0xFF2E7D32),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Co giãn đẩy các nút xuống dưới cùng
            Box(modifier = Modifier.weight(1f))

            // Cụm nút thao tác
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        // Kiểm tra logic đổi mật khẩu
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

                        // Thực hiện lưu thay đổi thông tin
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