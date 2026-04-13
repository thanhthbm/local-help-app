package com.localhelp.app.ui.screens.map

import androidx.lifecycle.ViewModel
import com.trackasia.android.geometry.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class SelectLocationViewModel @Inject constructor() : ViewModel() {
    private val _selectedLocation = MutableStateFlow<LatLng?>(null)
    val selectedLocation = _selectedLocation.asStateFlow()

    private val _address = MutableStateFlow("")
    val address = _address.asStateFlow()

    fun onLocationSelected(latLng: LatLng, addressName: String) {
        _selectedLocation.value = latLng
        _address.value = addressName
    }
}
