package com.example.personal_management_app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EmptySearchState(
    query: String,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    emptyMessage: String = "Không tìm thấy ghi chú phù hợp"
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F3F4)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(36.dp)
            )
        }
        Text(
            text = emptyMessage,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color(0xFF1F1F1F),
            textAlign = TextAlign.Center
        )
        if (query.isNotBlank()) {
            Text(
                text = "Không có kết quả nào khớp với \"$query\". Vui lòng thử từ khóa khác.",
                fontSize = 13.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        OutlinedButton(
            onClick = onClear,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text("Xóa tìm kiếm", fontSize = 13.sp)
        }
    }
}
