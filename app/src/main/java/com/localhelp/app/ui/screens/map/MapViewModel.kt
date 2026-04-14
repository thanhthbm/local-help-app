package com.localhelp.app.ui.screens.map

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localhelp.app.data.repository.LocationRepository
import com.localhelp.app.data.repository.MapRepository
import com.trackasia.android.geometry.LatLng
import com.trackasia.navigation.android.navigation.v5.location.replay.ReplayRouteLocationEngine
import com.trackasia.navigation.android.navigation.v5.models.DirectionsRoute
import com.trackasia.navigation.android.navigation.v5.navigation.TrackAsiaNavigation
import com.trackasia.navigation.android.navigation.v5.navigation.TrackAsiaNavigationOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.text.split

data class MapUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val routes: List<DirectionsRoute> = emptyList(),
    val startPoint: LatLng? = null,
    val destination: LatLng? = null,
    val totalDistanceKm: Double = 0.0,
    val totalDurationMin: Double = 0.0,
    val stepDistanceMeters: Double = 0.0,
    val instruction: String = "Đang tính toán...",
    val currLocation: Location? = null,
    val autoCenter: Boolean = true,
    val currPath: String = ""
)

@HiltViewModel
class MapViewModel @Inject constructor(
    val mapRepository: MapRepository,
    val locationRepository: LocationRepository,
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context : Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState = _uiState.asStateFlow()

    var trackAsiaNavigation: TrackAsiaNavigation? = null
    private val destination : LatLng by lazy {
        val destinationFromPath : String? = savedStateHandle.get<String>("destination")
        var destination = LatLng(0.0, 0.0)
        try {
            destinationFromPath?.let {
                val parts = it.split(",")
                if (parts.size >= 2) {
                    val lat = parts[0].trim().toDouble()
                    val lng = parts[1].trim().toDouble()
                    destination = LatLng(lat, lng)
                }
            }
        } catch (e: Exception) {
            Log.e("MapViewModel", "Error parsing destination: $destinationFromPath", e)
        }
        destination
    }

    init {
        try {
            val options = TrackAsiaNavigationOptions.Builder()
                .withIsDebugLoggingEnabled(true)
                .withDefaultMilestonesEnabled(false)
                .build()
            trackAsiaNavigation = TrackAsiaNavigation(context, options)
            setupListeners()
        } catch (e: Exception) {
            Log.e("MapViewModel", "Error initializing navigation", e)
        }
//        onGetDirection()
    }

    fun onGetDirection() {
        viewModelScope.launch {
            val currentPosition = locationRepository.getCurrentLocation() ?: destination

            val waypoints: List<LatLng> = listOf(
                currentPosition,
                destination
            )
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val coordinates = waypoints.joinToString(";") {
                    "${it.longitude},${it.latitude}"
                }
                val response = mapRepository.getDirections(coordinates)

                response.onFailure {
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = "Không tìm được đường."
                    )}
                }.onSuccess { direction ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        routes = direction.routes,
                        startPoint = currentPosition
                    )}
                    delay(500)
                    startTrip(route = direction.routes[0])
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    errorMessage = e.message
                )}
            }
        }
    }

    private fun setupListeners() {
        trackAsiaNavigation?.let {
            it.addProgressChangeListener { location, routeProgress ->
                val currentStepProgress = routeProgress.currentLegProgress.currentStepProgress
                val currentStep = currentStepProgress.step
                val nextStep = routeProgress.currentLegProgress.upComingStep?.name ?: ""
                val maneuverInstruction = getFullInstruction(currentStep.maneuver.type?.text, currentStep.maneuver.modifier?.text, nextStep)
                val name = currentStep.name?.takeIf {name -> name.isNotBlank() } ?: "Con đường không tên"

                val stepDist = currentStepProgress.distanceRemaining

                _uiState.update { state ->
                    state.copy(
                        totalDistanceKm = routeProgress.distanceRemaining / 1000.0,
                        totalDurationMin = routeProgress.durationRemaining / 60.0,
                        instruction = maneuverInstruction,
                        stepDistanceMeters = stepDist,
                        currLocation = location,
                        currPath = name
                    )
                }
            }

            it.addOffRouteListener { location ->
                Log.d("Navigation", "Bạn đã đi chệch đường tại: ${location.latitude}")
                onGetDirection()
            }

            it.addNavigationEventListener{ isRunning ->
                Log.i("NavigationManager", "Trạng thái Engine: $isRunning")
            }
        }
    }

    fun startTrip(route: DirectionsRoute) {
        try {
            val mockLocationEngine = ReplayRouteLocationEngine()
            mockLocationEngine.assign(route)

            trackAsiaNavigation?.locationEngine = mockLocationEngine
            trackAsiaNavigation?.startNavigation(route)
        } catch (e: Exception) {
            Log.d("TRACK ASIA", e.message.toString())
        }
    }

    fun stopTrip() {
        trackAsiaNavigation?.stopNavigation()
        trackAsiaNavigation?.onDestroy()
    }

    override fun onCleared() {
        super.onCleared()
        stopTrip()
    }

    fun onChangeMode(){
        _uiState.update{state ->
            state.copy(
                autoCenter = !state.autoCenter
            )
        }
    }

    private fun getModifierText(modifier: String?): String {
        return when (modifier) {
            "uturn" -> "Quay đầu"
            "sharp right" -> "Rẽ ngoặt phải"
            "right" -> "Rẽ phải"
            "slight right" -> "Chếch sang phải"
            "straight" -> "Đi thẳng"
            "slight left" -> "Chếch sang trái"
            "left" -> "Rẽ trái"
            "sharp left" -> "Rẽ ngoặt trái"
            else -> "Đi tiếp"
        }
    }

    fun getFullInstruction(type: String?, modifier: String?, streetName: String?): String {
        val name = streetName?.takeIf { it.isNotBlank() } ?: "đường phía trước"
        val toStreet = "vào $name"

        return when (type) {
            "depart" -> "Xuất phát, ${getModifierText(modifier).lowercase()} $toStreet"
            "arrive" -> "Đến đích"

            "roundabout", "rotary", "roundabout turn" -> "Vào vòng xuyến, $toStreet"
            "exit roundabout", "exit rotary" -> "Đi ra khỏi vòng xuyến $toStreet"

            "merge" -> "Nhập làn $toStreet"
            "on ramp" -> "Đi vào đường nhánh $toStreet"
            "off ramp" -> "Đi ra lối thoát $toStreet"

            "fork" -> "Tại ngã ba, ${getModifierText(modifier).lowercase()} $toStreet"
            "end of road" -> "Đến cuối đường, ${getModifierText(modifier).lowercase()} $toStreet"

            "use lane" -> "Đi đúng làn đường để $toStreet"

            "notification" -> "Chú ý đoạn đường phía trước" + name

            "turn", "new name", "continue" -> "${getModifierText(modifier)} $toStreet"

            else -> "${getModifierText(modifier)} $toStreet"
        }
    }
}