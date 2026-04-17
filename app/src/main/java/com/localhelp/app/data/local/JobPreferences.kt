package com.localhelp.app.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("job_prefs", Context.MODE_PRIVATE)

    fun setJobApplied(jobId: Long, userId: Long) {
        prefs.edit().putBoolean("applied_${userId}_$jobId", true).apply()
    }

    fun isJobApplied(jobId: Long, userId: Long): Boolean {
        return prefs.getBoolean("applied_${userId}_$jobId", false)
    }
}