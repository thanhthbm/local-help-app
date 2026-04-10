package com.localhelp.app.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.trackasia.android.geometry.LatLng
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class LocationRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LatLng? {
        return try {
            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).await()

            if (location != null) {
                return LatLng(location.latitude, location.longitude)
            }

            val lastLocation = fusedLocationClient.lastLocation.await()

            if (lastLocation != null) {
                LatLng(lastLocation.latitude, lastLocation.longitude)
            } else {
                null
            }

        } catch (e: Exception) {
            Log.e("LocationRepository", "Lỗi khi lấy vị trí: ${e.message}")
            null
        }
    }
}