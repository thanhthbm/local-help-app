package com.localhelp.app.ui.screens.createjob

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.localhelp.app.ui.common.createjob.CategoryRow
import com.localhelp.app.ui.common.createjob.CustomOutlinedTextField
import com.localhelp.app.ui.common.createjob.InputSection

val PrimaryOrange = Color(0xFFED7D68)
val LightBlueAI = Color(0xFFD3E3FD)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateJobScreen(
    onBackClick: () -> Unit,
    onJobCreated: () -> Unit,
    onSelectLocation: (Double, Double) -> Unit,
    viewModel: CreateJobViewModel = hiltViewModel()
) {
    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()
    val price by viewModel.price.collectAsState()
    val address by viewModel.address.collectAsState()
    val latitude by viewModel.latitude.collectAsState()
    val longitude by viewModel.longitude.collectAsState()
    val imageUris by viewModel.selectedImageUris.collectAsState()
    val existingImageUrls by viewModel.existingImageUrls.collectAsState()
    val isEditMode = viewModel.isEditMode

    // Thu thập danh sách categories động từ API
    val categories by viewModel.categories.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()
    val createSuccess by viewModel.createSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5),
        onResult = { uris -> 
            if (uris.isNotEmpty()) {
                viewModel.updateImages(imageUris + uris)
            }
        }
    )

    LaunchedEffect(createSuccess) {
        if (createSuccess) onJobCreated()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Chỉnh sửa công việc" else "Đăng việc mới", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Quay lại")
                    }
                },
                actions = { Spacer(modifier = Modifier.width(48.dp)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Hãy mô tả công việc bạn cần giúp đỡ. Cộng đồng quanh đây luôn sẵn sàng!",
                color = Color.Gray,
                fontSize = 15.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // --- TÊN CÔNG VIỆC ---
            InputSection("Tên công việc cần giúp") {
                CustomOutlinedTextField(
                    value = title, 
                    onValueChange = { viewModel.title.value = it }, 
                    placeholder = "Mua giúp thuốc"
                )
            }

            // --- THÙ LAO ---
            InputSection("Thù lao dự kiến") {
                CustomOutlinedTextField(
                    value = price, onValueChange = { viewModel.updatePrice(it) }, placeholder = "250.000",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    trailingIcon = { Text("VNĐ", color = Color.Gray, modifier = Modifier.padding(end = 16.dp)) }
                )
            }

            // --- ĐỊA CHỈ ---
            InputSection("Địa chỉ") {
                CustomOutlinedTextField(
                    value = address, onValueChange = { viewModel.address.value = it }, placeholder = "101 Yên Xá",
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Black) },
                    trailingIcon = {
                        IconButton(onClick = { onSelectLocation(latitude, longitude) }) {
                            Icon(Icons.Default.Map, contentDescription = "Chọn trên bản đồ", tint = PrimaryOrange)
                        }
                    }
                )
            }

            // --- MÔ TẢ CHI TIẾT ---
            InputSection("Mô tả chi tiết") {
                OutlinedTextField(
                    value = description, onValueChange = { viewModel.description.value = it },
                    placeholder = { Text("Mô tả chi tiết công việc và những lưu ý đặc biệt...", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            }

            // --- ẢNH MINH HỌA ---
            Column {
                Text("Ảnh minh họa (nếu cần)", fontWeight = FontWeight.Medium, fontSize = 16.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { multiplePhotoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Tải ảnh lên", color = Color.White, fontSize = 14.sp)
                }

                if (imageUris.isNotEmpty() || existingImageUrls.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Ảnh đã có trên server
                        items(existingImageUrls) { url ->
                            Box(modifier = Modifier.size(80.dp)) {
                                AsyncImage(
                                    model = url,
                                    contentDescription = "Existing Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(8.dp))
                                )
                                IconButton(
                                    onClick = { viewModel.removeExistingImage(url) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(24.dp)
                                        .padding(4.dp),
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = Color.Black.copy(alpha = 0.5f),
                                        contentColor = Color.White
                                    )
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(12.dp))
                                }
                            }
                        }

                        // Ảnh mới chọn từ máy
                        items(imageUris) { uri ->
                            Box(modifier = Modifier.size(80.dp)) {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = "New Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(8.dp))
                                )
                                IconButton(
                                    onClick = { viewModel.removeSelectedImage(uri) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(24.dp)
                                        .padding(4.dp),
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = Color.Black.copy(alpha = 0.5f),
                                        contentColor = Color.White
                                    )
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(12.dp))
                                }
                            }
                        }
                    }
                }
            }

            // --- DANH MỤC (Đã lấy từ API) ---
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Danh mục", fontWeight = FontWeight.Medium, fontSize = 16.sp, color = Color.Black)
                    Text("Xem tất cả", color = PrimaryOrange, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Truyền mảng categories động vào đây
                CategoryRow(
                    categories = categories,
                    selectedId = selectedCategoryId,
                    onSelect = { viewModel.selectedCategoryId.value = it }
                )
            }

            if (errorMessage != null) {
                Text(errorMessage!!, color = Color.Red, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- NÚT ĐĂNG YÊU CẦU ---
            Button(
                onClick = { viewModel.createJob() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryOrange,
                    contentColor = Color.White
                ),
                enabled = !isLoading, shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Text(if (isEditMode) "Cập nhật yêu cầu" else "Đăng yêu cầu", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(if (isEditMode) Icons.Default.Save else Icons.AutoMirrored.Filled.Send, null, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}






