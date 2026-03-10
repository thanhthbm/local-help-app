package com.localhelp.app

import android.app.Application
import com.cloudinary.android.MediaManager
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LocalHelpApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        val config = mapOf(
            "cloud_name" to "dwtpcdjhe"
        )
        MediaManager.init(this, config)
    }
}