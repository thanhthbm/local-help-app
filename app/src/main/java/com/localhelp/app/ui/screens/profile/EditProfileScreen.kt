package com.localhelp.app.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.localhelp.app.model.constant.GenderEnum

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onAvatarSelected(it) }
    }

    // Navigate back on success
    LaunchedEffect(uiState.isSaveSuccess) {
        if (uiState.isSaveSuccess) {
            onNavigateBack()
        }
    }

    // Show error snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chỉnh sửa hồ sơ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF7F7F7)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // ─── Avatar Picker ───────────────────────────────────────────
            Box(
                modifier = Modifier.size(110.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                val avatarModel: Any? = uiState.localAvatarUri ?: uiState.avatarUrl
                if (avatarModel != null) {
                    AsyncImage(
                        model = avatarModel,
                        contentDescription = "Ảnh đại diện",
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .border(3.dp, Color(0xFFF06A50), CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0E0E0))
                            .border(3.dp, Color(0xFFF06A50), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                // Camera badge
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF06A50))
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Chọn ảnh",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Đổi ảnh đại diện",
                color = Color(0xFFF06A50),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { imagePickerLauncher.launch("image/*") }
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ─── Form card ───────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Họ và tên
                ProfileTextField(
                    label = "Họ và tên",
                    value = uiState.fullName,
                    onValueChange = viewModel::onFullNameChange,
                    placeholder = "Nhập họ và tên"
                )

                HorizontalDivider(color = Color(0xFFF0F0F0))

                // Số điện thoại
                ProfileTextField(
                    label = "Số điện thoại",
                    value = uiState.phone,
                    onValueChange = viewModel::onPhoneChange,
                    placeholder = "Nhập số điện thoại",
                    keyboardType = KeyboardType.Phone
                )

                HorizontalDivider(color = Color(0xFFF0F0F0))

                // Giới tính (dropdown)
                GenderDropdown(
                    selected = uiState.gender,
                    onSelected = viewModel::onGenderChange
                )

                HorizontalDivider(color = Color(0xFFF0F0F0))

                // Giới thiệu bản thân (bio)
                ProfileTextField(
                    label = "Giới thiệu bản thân",
                    value = uiState.bio,
                    onValueChange = viewModel::onBioChange,
                    placeholder = "Viết vài dòng về bản thân…",
                    singleLine = false,
                    minLines = 3
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ─── Save Button ─────────────────────────────────────────────
            Button(
                onClick = { viewModel.saveProfile(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(54.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF06A50),
                    contentColor = Color.White
                ),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Lưu thay đổi",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ─── Reusable form field ──────────────────────────────────────────────────────
@Composable
private fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF444444)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder, color = Color(0xFFBBBBBB)) },
            singleLine = singleLine,
            minLines = minLines,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFF06A50),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}

// ─── Gender Dropdown ──────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GenderDropdown(
    selected: GenderEnum?,
    onSelected: (GenderEnum) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val displayMap = mapOf(
        GenderEnum.MALE to "Nam",
        GenderEnum.FEMALE to "Nữ",
        GenderEnum.OTHER to "Khác",
        GenderEnum.UNKNOWN to "Không muốn tiết lộ"
    )

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Giới tính",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF444444)
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = if (selected != null) displayMap[selected] ?: "Chọn giới tính" else "Chọn giới tính",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFF06A50),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                displayMap.forEach { (gender, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            onSelected(gender)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
