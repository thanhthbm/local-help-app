package com.localhelp.app.data.remote

import com.localhelp.app.model.constant.ApiConstants
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.trackasia.navigation.android.navigation.v5.models.BannerInstructions
import com.trackasia.navigation.android.navigation.v5.models.DirectionsResponse
import com.trackasia.navigation.android.navigation.v5.models.VoiceInstructions
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TrackAsiaApiService {
    @GET("api/v1/reverse")
    suspend fun reverseGeocode(
        @Query("point.lat") latitude: Double,
        @Query("point.lon") longitude: Double,
        @Query("lang") language: String = "vi",
        @Query("key") apiKey: String = ApiConstants.TRACK_ASIA_KEY
    ): Response<GeocodingResponse>

//    @GET("api/v1/autocomplete")
//    suspend fun autocomplete(
//        @Query("text") query: String,
//        @Query("lang") language: String = "vi",
//        @Query("focus.point.lat") focusLat: Double? = null,
//        @Query("focus.point.lon") focusLon: Double? = null,
//        @Query("key") apiKey: String = ApiConstants.PUBLIC_KEY
//    ): Response<AutocompleteResponse>

    @GET("route/v1/{vehicle}/{coordinates}.json")
    suspend fun getDirections(
        @Path("vehicle") vehicles: String = "car",
        @Path("coordinates") coordinates: String,
        @Query("geometries") geometries: String = "polyline6",
        @Query("steps") steps: Boolean = true,
        @Query("overview") overview: String = "full",
        @Query("key") apiKey: String = ApiConstants.TRACK_ASIA_KEY
    ): Response<DirectionsResponse>
}