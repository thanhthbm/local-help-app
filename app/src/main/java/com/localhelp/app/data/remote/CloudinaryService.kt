package com.localhelp.app.data.remote

import com.localhelp.app.model.response.CloudinaryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

/**
 * Retrofit service dùng để upload ảnh công việc trực tiếp lên Cloudinary.
 */
interface CloudinaryService {
    /**
     * Upload một ảnh minh họa công việc lên Cloudinary bằng unsigned preset.
     *
     * @param cloudName Tên cloud Cloudinary.
     * @param uploadPreset Preset upload đã cấu hình trên Cloudinary.
     * @param file Multipart ảnh được chọn từ thiết bị.
     * @return Response chứa URL bảo mật của ảnh sau khi upload.
     */
    @Multipart
    @POST("https://api.cloudinary.com/v1_1/{cloudName}/image/upload")
    suspend fun uploadImage(
        @Path("cloudName") cloudName: String,
        @Part("upload_preset") uploadPreset: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<CloudinaryResponse>
}

