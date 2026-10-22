package com.example.personal_management_app.ui.screen.profile

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.personal_management_app.ui.components.profile.ProfileActionButtons
import com.example.personal_management_app.ui.components.profile.ProfileAvatarSection
import com.example.personal_management_app.ui.components.profile.ProfileInfoForm
import com.example.personal_management_app.ui.components.profile.ProfileSecurityForm
import com.example.personal_management_app.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val profile = viewModel.getProfile()

    if (profile == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Chưa đăng nhập", color = Color.Gray)
        }
        return
    }

    var nameInput by remember { mutableStateOf(profile.name) }
    var usernameInput by remember { mutableStateOf(profile.username) }
    var currentPasswordInput by remember { mutableStateOf("") }
    var newPasswordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isErrorState by remember { mutableStateOf(false) }

    val avatarBitmap = remember(selectedImageUri) {
        selectedImageUri?.let { uri ->
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream)?.asImageBitmap()
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Trang cá nhân",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1F1F1F)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color(0xFF444746)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileAvatarSection(
                name = nameInput,
                username = usernameInput,
                avatarBitmap = avatarBitmap,
                hasCustomAvatar = selectedImageUri != null,
                onPickImage = { uri ->
                    selectedImageUri = uri
                    statusMessage = "Đã chọn ảnh đại diện mới thành công"
                    isErrorState = false
                },
                onRemoveImage = {
                    selectedImageUri = null
                    statusMessage = "Đã xóa ảnh đại diện, trở về mặc định."
                    isErrorState = false
                }
            )



            ProfileInfoForm(
                name = nameInput,
                onNameChange = { nameInput = it },
                username = usernameInput,
                onUsernameChange = { usernameInput = it }
            )

            ProfileSecurityForm(
                currentPassword = currentPasswordInput,
                onCurrentPasswordChange = { currentPasswordInput = it },
                newPassword = newPasswordInput,
                onNewPasswordChange = { newPasswordInput = it },
                confirmPassword = confirmPasswordInput,
                onConfirmPasswordChange = { confirmPasswordInput = it }
            )

            if (statusMessage != null) {
                Text(
                    text = statusMessage!!,
                    color = if (isErrorState) Color(0xFFD93025) else Color(0xFF1E8E3E),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            ProfileActionButtons(
                onSaveClick = {
                    val error = viewModel.updateProfile(
                        name = nameInput,
                        username = usernameInput,
                        currentPassword = currentPasswordInput,
                        newPassword = newPasswordInput,
                        confirmPassword = confirmPasswordInput
                    )

                    if (error != null) {
                        statusMessage = error
                        isErrorState = true
                    } else {
                        statusMessage = "Cập nhật hồ sơ thành công!"
                        isErrorState = false
                        currentPasswordInput = ""
                        newPasswordInput = ""
                        confirmPasswordInput = ""
                    }
                },
                onLogoutClick = {
                    viewModel.logout()
                    navController.navigate("login_screen") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )
        }
    }
}
