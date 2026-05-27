package com.localhelp.app.data.repository

import android.util.Log
import com.localhelp.app.data.remote.TrackAsiaApiService
import com.localhelp.app.model.response.TrackAsiaGeocodingResponse
import com.trackasia.navigation.android.navigation.v5.models.DirectionsResponse
import javax.inject.Inject

/**
 * Repository trung gian cho các API bản đồ và chỉ đường của TrackAsia.
 */
class MapRepository @Inject constructor(
    private val trackAsiaApiService: TrackAsiaApiService
){
    /**
     * Gọi API lấy lộ trình chỉ đường theo chuỗi tọa độ.
     */
    suspend fun getDirections(coordinates : String) : Result<DirectionsResponse> {
        return try {
            val response = trackAsiaApiService.getDirections(coordinates = coordinates)
            if(response.isSuccessful){
                if(response.body() != null){
                    val direction : DirectionsResponse = response.body()!!
                    Result.success(direction)
                } else {
                    Result.failure(Exception("empty body"))
                }
            } else {
                Result.failure(Exception(response.message() ?: ""))
            }

        } catch(e : Exception){
            Log.e("Network", e.message ?: "Loi khong xac dinh")
            Result.failure(e)
        }
    }

    /**
     * Gọi API reverse geocoding để lấy địa chỉ từ tọa độ.
     */
    suspend fun reverseGeocode(latitude: Double, longitude: Double): Result<TrackAsiaGeocodingResponse> {
        return try {
            val response = trackAsiaApiService.reverseGeocode(latitude, longitude)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message() ?: "Reverse geocoding failed"))
            }
        } catch (e: Exception) {
            Log.e("MapRepository", "Error reverse geocoding", e)
            Result.failure(e)
        }
    }
}
