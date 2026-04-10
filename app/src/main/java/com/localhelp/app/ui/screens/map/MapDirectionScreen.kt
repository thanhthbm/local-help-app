package com.localhelp.app.ui.screens.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.material3.CircularProgressIndicator
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.localhelp.app.model.constant.ApiConstants
import com.trackasia.android.TrackAsia
import com.trackasia.android.camera.CameraPosition
import com.trackasia.android.camera.CameraUpdateFactory
import com.trackasia.android.geometry.LatLng
import com.trackasia.android.location.LocationComponentActivationOptions
import com.trackasia.android.location.modes.CameraMode
import com.trackasia.android.location.modes.RenderMode
import com.trackasia.android.maps.MapView
import com.trackasia.android.maps.Style
import com.trackasia.android.maps.TrackAsiaMap
import com.trackasia.navigation.android.navigation.ui.v5.route.NavigationMapRoute
import com.trackasia.navigation.android.navigation.v5.navigation.TrackAsiaNavigation
import androidx.compose.ui.Alignment
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import kotlin.math.roundToInt
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority

@Composable
fun MapRoute(
    viewModel: MapViewModel,
    onBackClick: () -> Unit
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as Activity

    val enableGPSLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.onGetDirection()
        } else {
            onBackClick()
        }
    }
    val checkGpsAndFetchLocation: () -> Unit = {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L).build()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        LocationServices.getSettingsClient(context)
            .checkLocationSettings(builder.build())
            .addOnSuccessListener {
                viewModel.onGetDirection()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    enableGPSLauncher.launch(IntentSenderRequest.Builder(exception.resolution).build())
                } else {
                    onBackClick()
                }
            }
    }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (granted) {
            checkGpsAndFetchLocation()
        } else {
            val shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(
                activity, Manifest.permission.ACCESS_FINE_LOCATION
            )
            if (!shouldShow) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
        }
    }

    LaunchedEffect(Unit) {
        val fineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)

        if (fineLocation == PackageManager.PERMISSION_GRANTED) {
            checkGpsAndFetchLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }


    MapScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onChangeMode = viewModel::onChangeMode,
        trackAsiaNavigation = viewModel.trackAsiaNavigation
    )
}


@SuppressLint("DefaultLocale")
@Composable
fun MapScreen(
    uiState: MapUiState,
    onBackClick: () -> Unit,
    onChangeMode: () -> Unit,
    trackAsiaNavigation: TrackAsiaNavigation?
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val trackasiaMap = remember {mutableStateOf<TrackAsiaMap?>(null)}
    val navigationMapRoute = remember { mutableStateOf<NavigationMapRoute?>(null) }


    val mapView = remember {
        TrackAsia.getInstance(context)
        MapView(context).apply {
            getMapAsync { map ->
                trackasiaMap.value = map

                map.setStyle(
                    Style.Builder().fromUri(
                        ApiConstants.BASE_URL_MAP_VN + "styles/v1/streets.json?key=" + ApiConstants.TRACK_ASIA_KEY
                    )
                ) { style ->
                    navigationMapRoute.value = NavigationMapRoute(null, this, map)
                    navigationMapRoute.value?.addProgressChangeListener(trackAsiaNavigation)

                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        map.locationComponent.apply {
                            activateLocationComponent(LocationComponentActivationOptions.builder(context, style).build())
                            isLocationComponentEnabled = true
                            cameraMode = CameraMode.TRACKING_GPS
                            renderMode = RenderMode.GPS
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(uiState.startPoint) {
        uiState.startPoint?.let{location ->
            trackasiaMap.value?.easeCamera(
                CameraUpdateFactory.newLatLngZoom(
                    location,
                    15.0
                ), 500
            )
        }
    }

    LaunchedEffect(uiState.currLocation) {
        uiState.currLocation?.let{
            if(!uiState.autoCenter) return@LaunchedEffect
            val cameraPosition = CameraPosition.Builder()
                .target(LatLng(it.latitude, it.longitude))
                .bearing(it.bearing.toDouble())
                .tilt(50.0)
                .build()
            trackasiaMap.value.let{map ->
                map?.easeCamera(
                    CameraUpdateFactory.newCameraPosition(cameraPosition),
                    1500
                )
            }
        }
    }

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE  -> mapView.onCreate(Bundle())
                Lifecycle.Event.ON_START   -> mapView.onStart()
                Lifecycle.Event.ON_RESUME  -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE   -> mapView.onPause()
                Lifecycle.Event.ON_STOP    -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }

        lifecycle.addObserver(observer)
        navigationMapRoute.value?.let{
            lifecycle.addObserver(it)
        }

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        )

        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }

        uiState.errorMessage?.let { error ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp)
            ) {
                Text(
                    text = error,

                )
            }
        }

        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = CircleShape
                )
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
        Column(
            modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)

        ){
            IconButton(
                onClick = {
                    onChangeMode()
                },
                modifier = Modifier
                    .align(Alignment.End )
                    .padding(16.dp)
                    .background(
                        color =
                            if (uiState.autoCenter)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    )
            ) {
                Text(
                    text = "A",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 25.dp)
                    .align (Alignment.CenterHorizontally),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                tonalElevation = 8.dp,
                shadowElevation = 4.dp
            ) {

                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = uiState.currPath,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Left
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(){
                        Text(
                            text = "${ uiState.stepDistanceMeters.roundToInt() } m",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Left
                        )
                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = uiState.instruction,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Left
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(){
                        Text(
                            text = "${String.format("%.1f", uiState.totalDistanceKm)} km",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Thin,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Left
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${uiState.totalDurationMin.roundToInt()} Phút",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Thin,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Left
                        )
                    }

                }
            }


        }
    }
}
