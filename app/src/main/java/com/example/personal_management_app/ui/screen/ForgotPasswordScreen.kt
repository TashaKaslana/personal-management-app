package com.example.personal_management_app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.personal_management_app.ui.theme.ArchiveBlue
import com.example.personal_management_app.ui.theme.FabContainer
import com.example.personal_management_app.ui.theme.Loginblue
import com.example.personal_management_app.ui.theme.NavBarBackground
import com.example.personal_management_app.ui.theme.ReminderBlue

@Composable
fun ForgotPasswordScreen(modifier: Modifier = Modifier, navController: NavController) {
    var emailInput by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isErrorState by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = NavBarBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Tiêu đề trang
            Text(
                text = "Khôi phục mật khẩu",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = "Nhập email của bạn để nhận hướng dẫn",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // Ô nhập Email
            OutlinedTextField(
                value = emailInput,
                onValueChange = { emailInput = it },
                placeholder = { Text("Email", color = Color.Gray) },
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            // Hiển thị thông báo phản hồi
            if (statusMessage != null) {
                Text(
                    text = statusMessage!!,
                    color = if (isErrorState) Color.Red else Color(0xFF2E7D32),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            // Nút Gửi yêu cầu
            Button(
                onClick = {
                    if (emailInput.isBlank()) {
                        statusMessage = "Vui lòng nhập địa chỉ email!"
                        isErrorState = true
                    } else {
                        statusMessage = "Liên kết khôi phục đã được gửi qua email!"
                        isErrorState = false
                    }
                },
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FabContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(top = 20.dp)
            ) {
                Text(
                    text = "Gửi yêu cầu",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // Nút quay lại trang Đăng nhập
            TextButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = "Quay lại Đăng nhập",
                    fontSize = 14.sp,
                    color = Loginblue,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}