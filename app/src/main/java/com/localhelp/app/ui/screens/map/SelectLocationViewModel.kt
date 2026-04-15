package com.localhelp.app.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localhelp.app.data.repository.MapRepository
import com.trackasia.android.geometry.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class SelectLocationViewModel @Inject constructor(
    private val mapRepository: MapRepository
) : ViewModel() {
    private val _selectedLocation = MutableStateFlow<LatLng?>(null)
    val selectedLocation = _selectedLocation.asStateFlow()

    private val _address = MutableStateFlow("")
    val address = _address.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun onLocationSelected(latLng: LatLng) {
        _selectedLocation.value = latLng
        reverseGeocode(latLng.latitude, latLng.longitude)
    }

    private fun reverseGeocode(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            mapRepository.reverseGeocode(latitude, longitude)
                .onSuccess { response ->
                    val label = response.features.firstOrNull()?.properties?.label
                    _address.value = label ?: "Vị trí tại ${String.format("%.4f", latitude)}, ${String.format("%.4f", longitude)}"
                }
                .onFailure {
                    _address.value = "Vị trí tại ${String.format("%.4f", latitude)}, ${String.format("%.4f", longitude)}"
                }
            _isLoading.value = false
        }
    }

    fun setInitialAddress(address: String) {
        _address.value = address
    }
}
