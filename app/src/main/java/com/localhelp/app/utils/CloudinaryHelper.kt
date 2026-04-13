package com.localhelp.app.utils

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object CloudinaryHelper {
    private const val UPLOAD_PRESET = "localhelp_preset"

    suspend fun uploadMedia(uri: Uri): String = suspendCancellableCoroutine { continuation ->
        val requestId = MediaManager.get().upload(uri)
            .unsigned(UPLOAD_PRESET)
            .option("resource_type", "auto") // Tự động nhận diện image/video
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?){}
                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long){}
                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    val secureUrl = resultData?.get("secure_url") as? String
                    if (secureUrl != null) {
                        continuation.resume(secureUrl)
                    } else {
                        continuation.resumeWithException(Exception("Không lấy được URL"))
                    }
                }
                override fun onError(requestId: String?, error: ErrorInfo?) {
                    continuation.resumeWithException(Exception(error?.description ?: "Upload failed"))
                }
                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            })
            .dispatch()

        continuation.invokeOnCancellation {
            MediaManager.get().cancelRequest(requestId)
        }
    }

    suspend fun uploadMultipleMedia(uris: List<Uri>): List<String> {
        return uris.map { uploadMedia(it) }
    }
}