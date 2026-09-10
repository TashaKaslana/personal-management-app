package com.example.personal_management_app.ui.screen.setting_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.personal_management_app.ui.theme.BackgroundSetting
import com.example.personal_management_app.ui.theme.IconBlue
import com.example.personal_management_app.ui.theme.TextBlack
import com.example.personal_management_app.ui.theme.TextDark
import com.example.personal_management_app.ui.theme.TextGray
import com.example.personal_management_app.ui.theme.SwitchGreen
import com.example.personal_management_app.ui.theme.SwitchThumb


@Composable
fun Settingscreenseeting(
    onBackClick: () -> Unit = {}
) {

    // TRẠNG THÁI CỦA CÁC SWITCH

    var switch1 by remember {
        mutableStateOf(true)
    }

    var switch2 by remember {
        mutableStateOf(true)
    }

    var switch3 by remember {
        mutableStateOf(true)
    }

    var switch4 by remember {
        mutableStateOf(true)
    }

    var switch5 by remember {
        mutableStateOf(true)
    }


    // TOÀN BỘ MÀN HÌNH

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSetting)
            .padding(
                start = 26.dp,
                end = 26.dp,
                top = 20.dp
            )
    ) {


        // THANH TRÊN CÙNG

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            // Nút quay lại
            IconButton(
                onClick = onBackClick
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,

                    contentDescription = "Quay lại",

                    tint = IconBlue
                )
            }


            // Chữ Cài đặt
            Text(
                text = "Cài đặt",

                fontSize = 14.sp,

                fontWeight = FontWeight.Bold,

                color = TextBlack
            )
        }


        // TIÊU ĐỀ

        Text(
            text = "Chế độ hiển thị",

            modifier = Modifier.padding(
                top = 17.dp
            ),

            fontSize = 20.sp,

            fontWeight = FontWeight.Bold,

            color = Color.Black
        )


        // Khoảng cách
        Spacer(
            modifier = Modifier.height(18.dp)
        )


        // PHẦN CHẾ ĐỘ HIỂN THỊ


        // SWITCH 1

        SettingSwitchRow(
            text = "Thêm các mục mới vào cuối",

            checked = switch1,

            onCheckedChange = {
                switch1 = it
            }
        )


        // SWITCH 2

        SettingSwitchRow(
            text = "Chuyển mục đã chọn xuống cuối",

            checked = switch2,

            onCheckedChange = {
                switch2 = it
            }
        )


        // SWITCH 3

        SettingSwitchRow(
            text = "Hiển thị xem trước liên kết đa phương tiện",

            checked = switch3,

            onCheckedChange = {
                switch3 = it
            }
        )


        // TIÊU ĐỀ TẠO GHI CHÚ

        Text(
            text = "Tạo ghi chú",

            modifier = Modifier.padding(
                top = 25.dp
            ),

            fontSize = 11.sp,

            fontWeight = FontWeight.Bold,

            color = TextDark
        )


        // SWITCH 4

        SettingSwitchRow(
            text = "Tạo ghi chú dạng văn bản theo mặc định",

            checked = switch4,

            onCheckedChange = {
                switch4 = it
            }
        )


        // TIÊU ĐỀ CHIA SẺ

        Text(
            text = "Chia sẻ",

            modifier = Modifier.padding(
                top = 18.dp
            ),

            fontSize = 11.sp,

            fontWeight = FontWeight.Bold,

            color = TextDark
        )


        // SWITCH 5

        SettingSwitchRow(
            text = "Bật chia sẻ",

            checked = switch5,

            onCheckedChange = {
                switch5 = it
            }
        )
    }
}


// COMPONENT: MỘT DÒNG CÀI ĐẶT

@Composable
fun SettingSwitchRow(
    text: String,

    checked: Boolean,

    onCheckedChange: (Boolean) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp),

        verticalAlignment = Alignment.CenterVertically,

        horizontalArrangement = Arrangement.SpaceBetween
    ) {


        // TEXT

        Text(
            text = text,

            modifier = Modifier.weight(1f),

            fontSize = 10.sp,

            color = TextGray
        )


        // SWITCH

        Switch(

            checked = checked,

            onCheckedChange = onCheckedChange,

            colors = SwitchDefaults.colors(

                // Khi bật
                checkedThumbColor = SwitchThumb,

                checkedTrackColor = SwitchGreen,

                // Khi tắt
                uncheckedThumbColor = SwitchThumb,

                uncheckedTrackColor = Color(0xFFD0D0D0)
            )
        )
    }
}