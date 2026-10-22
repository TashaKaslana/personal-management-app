package com.example.personal_management_app.ui.components.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.personal_management_app.ui.theme.FabContainer
import com.example.personal_management_app.ui.theme.Loginblue

@Composable
fun ProfileAvatarSection(
    name: String,
    username: String,
    avatarBitmap: ImageBitmap?,
    hasCustomAvatar: Boolean,
    onPickImage: (Uri) -> Unit,
    onRemoveImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showPickerDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onPickImage(uri)
        }
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(24.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.size(96.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(FabContainer)
                        .clickable { showPickerDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (avatarBitmap != null) {
                        Image(
                            bitmap = avatarBitmap,
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            modifier = Modifier.size(54.dp),
                            tint = Color(0xFF5F6368)
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = Loginblue,
                    modifier = Modifier
                        .size(32.dp)
                        .border(2.dp, Color.White, CircleShape),
                    onClick = { showPickerDialog = true }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (hasCustomAvatar) Icons.Default.Edit else Icons.Default.AddAPhoto,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = name.ifBlank { "Người dùng" },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F1F1F)
                )
                Text(
                    text = "@${username.ifBlank { "username" }}",
                    fontSize = 13.sp,
                    color = Color(0xFF5F6368)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { showPickerDialog = true },
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(
                        imageVector = if (hasCustomAvatar) Icons.Default.Edit else Icons.Default.AddAPhoto,
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(end = 4.dp),
                        tint = Loginblue
                    )
                    Text(
                        text = if (hasCustomAvatar) "Sửa ảnh" else "Thêm ảnh",
                        fontSize = 13.sp,
                        color = Loginblue
                    )
                }

                if (hasCustomAvatar) {
                    OutlinedButton(
                        onClick = { showDeleteConfirmDialog = true },
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(end = 4.dp),
                            tint = Color(0xFFD93025)
                        )
                        Text(
                            text = "Xóa ảnh",
                            fontSize = 13.sp,
                            color = Color(0xFFD93025)
                        )
                    }
                }
            }
        }
    }

    if (showPickerDialog) {
        AlertDialog(
            onDismissRequest = { showPickerDialog = false },
            title = {
                Text(
                    text = if (hasCustomAvatar) "Cập nhật ảnh đại diện" else "Thêm ảnh đại diện",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(text = "Chọn ảnh mới từ thư viện ảnh trên thiết bị của bạn.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPickerDialog = false
                        imagePickerLauncher.launch("image/*")
                    }
                ) {
                    Text(
                        text = if (hasCustomAvatar) "Chọn ảnh khác" else "Mở thư viện ảnh",
                        color = Loginblue,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showPickerDialog = false }) {
                    Text(text = "Hủy", color = Color.Gray)
                }
            }
        )
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = {
                Text(
                    text = "Xóa ảnh đại diện?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(text = "Bạn có chắc chắn muốn gỡ ảnh đại diện này và quay về biểu tượng mặc định?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRemoveImage()
                        showDeleteConfirmDialog = false
                    }
                ) {
                    Text(text = "Xóa", color = Color(0xFFD93025), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text(text = "Hủy", color = Color.Gray)
                }
            }
        )
    }
}

