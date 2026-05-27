package com.localhelp.app.data.remote

import com.localhelp.app.model.response.CloudinaryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface CloudinaryService {
    /**
     * Gọi API ngoài của Cloudinary để upload ảnh.
     *
     * API này không đi qua backend; app gửi file và upload_preset trực tiếp
     * đến Cloudinary, sau đó dùng secure_url để cập nhật hồ sơ.
     */
    @Multipart
    @POST("https://api.cloudinary.com/v1_1/{cloudName}/image/upload")
    suspend fun uploadImage(
        @Path("cloudName") cloudName: String,
        @Part("upload_preset") uploadPreset: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<CloudinaryResponse>
}

