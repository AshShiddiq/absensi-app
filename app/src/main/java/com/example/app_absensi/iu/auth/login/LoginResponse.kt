package com.example.app_absensi.iu.auth.login

import com.example.app_absensi.data.model.User

data class LoginResponse(
    val status: String, // "success" atau "fail"
    val userId: String?,
    val name: String?,
    val message: String? = null // pesan kesalahan (opsional)
    // Objek pengguna, jika berhasil login
)

