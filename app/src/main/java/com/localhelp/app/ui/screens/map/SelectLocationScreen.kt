package com.localhelp.app.ui.screens.map

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.localhelp.app.model.constant.ApiConstants
import com.trackasia.android.TrackAsia
import com.trackasia.android.camera.CameraUpdateFactory
import com.trackasia.android.geometry.LatLng
import com.trackasia.android.maps.MapView
import com.trackasia.android.maps.Style
import com.trackasia.android.maps.TrackAsiaMap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectLocationScreen(
    onBack: () -> Unit,
    onLocationConfirmed: (Double, Double, String) -> Unit,
    initialLat: Double = 20.9800,
    initialLng: Double = 105.7950,
    viewModel: SelectLocationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var selectedLatLng by remember { mutableStateOf(LatLng(initialLat, initialLng)) }
    var addressName by remember { mutableStateOf("Đang xác định vị trí...") }

    val trackasiaMap = remember { mutableStateOf<TrackAsiaMap?>(null) }

    val mapView = remember {
        TrackAsia.getInstance(context)
        MapView(context).apply {
            getMapAsync { map ->
                trackasiaMap.value = map
                map.setStyle(
                    Style.Builder().fromUri(
                        ApiConstants.BASE_URL_MAP_VN + "styles/v1/streets.json?key=" + ApiConstants.TRACK_ASIA_KEY
                    )
                ) {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 15.0))
                }

                map.addOnMapClickListener { point ->
                    selectedLatLng = point
                    // In a real app, you would use reverse geocoding here
                    addressName = "Vị trí tại ${String.format("%.4f", point.latitude)}, ${String.format("%.4f", point.longitude)}"
                    true
                }
            }
        }
    }

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chọn vị trí", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())

            // Marker in the center of the map (or representing the selected point)
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier.size(40.dp).align(Alignment.Center).offset(y = (-20).dp)
            )

            // Bottom Info Card
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Địa chỉ đã chọn", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(addressName, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onLocationConfirmed(selectedLatLng.latitude, selectedLatLng.longitude, addressName) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFED7D68)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Xác nhận vị trí")
                    }
                }
            }
        }
    }
}
