package com.localhelp.app.data.remote

import com.localhelp.app.model.constant.ApiConstants
import com.localhelp.app.model.response.TrackAsiaGeocodingResponse
import com.trackasia.navigation.android.navigation.v5.models.DirectionsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit service khai báo các API ngoài của TrackAsia dùng cho chức năng chỉ đường.
 *
 * Hai luồng chính:
 * - reverse geocoding: đổi tọa độ thành địa chỉ;
 * - directions: lấy lộ trình di chuyển giữa các điểm.
 */
interface TrackAsiaApiService {
    /**
     * Gọi API reverse geocoding để đổi tọa độ thành địa chỉ dễ đọc.
     */
    @GET("api/v1/reverse")
    suspend fun reverseGeocode(
        @Query("point.lat") latitude: Double,
        @Query("point.lon") longitude: Double,
        @Query("new_admin") newAdmin: Boolean = true,
        @Query("categories") categories: String = "street_address",
        @Query("size") size: Int = 1,
        @Query("lang") language: String = "vi",
        @Query("key") apiKey: String = ApiConstants.TRACK_ASIA_KEY
    ): Response<TrackAsiaGeocodingResponse>

//    @GET("api/v1/autocomplete")
//    suspend fun autocomplete(
//        @Query("text") query: String,
//        @Query("lang") language: String = "vi",
//        @Query("focus.point.lat") focusLat: Double? = null,
//        @Query("focus.point.lon") focusLon: Double? = null,
//        @Query("key") apiKey: String = ApiConstants.PUBLIC_KEY
//    ): Response<AutocompleteResponse>

    /**
     * Gọi API directions để lấy tuyến đường và các bước điều hướng.
     */
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
