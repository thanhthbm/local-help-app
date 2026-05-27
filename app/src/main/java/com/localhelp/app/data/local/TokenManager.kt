package com.localhelp.app.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("local_help_prefs", Context.MODE_PRIVATE)

    /** Lưu Firebase ID token để AuthInterceptor gắn vào request backend. */
    fun saveToken(token: String){
        prefs.edit().putString("JWT_TOKEN", token).apply()
    }

    /** Đọc token hiện tại từ SharedPreferences. */
    fun getToken(): String? {
        return prefs.getString("JWT_TOKEN", null)
    }

    /** Xóa token khi đăng xuất hoặc khi token không còn hợp lệ. */
    fun clearToken() {
        prefs.edit().remove("JWT_TOKEN").apply()
    }
}
