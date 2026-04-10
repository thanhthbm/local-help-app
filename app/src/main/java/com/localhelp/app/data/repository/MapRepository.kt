package com.localhelp.app.data.repository

import android.util.Log
import com.localhelp.app.data.remote.TrackAsiaApiService
import com.trackasia.navigation.android.navigation.v5.models.DirectionsResponse
import javax.inject.Inject


class MapRepository @Inject constructor(
    private val trackAsiaApiService: TrackAsiaApiService
){
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
}