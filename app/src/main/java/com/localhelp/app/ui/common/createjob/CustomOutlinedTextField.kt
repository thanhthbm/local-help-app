package com.localhelp.app.ui.common.createjob

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * TextField dùng chung cho các ô nhập trong form đăng/cập nhật công việc.
 *
 * @param value Giá trị hiện tại của ô nhập.
 * @param onValueChange Callback cập nhật giá trị khi người dùng nhập.
 * @param placeholder Gợi ý hiển thị khi ô nhập rỗng.
 * @param keyboardOptions Cấu hình bàn phím.
 * @param leadingIcon Icon đầu ô nhập nếu có.
 * @param trailingIcon Icon cuối ô nhập nếu có.
 */
@Composable
fun CustomOutlinedTextField(
    value: String, onValueChange: (String) -> Unit, placeholder: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    leadingIcon: @Composable (() -> Unit)? = null, trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.Gray) },
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Black, unfocusedBorderColor = Color.Black,
            focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
        ),
        singleLine = true, keyboardOptions = keyboardOptions,
        leadingIcon = leadingIcon, trailingIcon = trailingIcon
    )
}
