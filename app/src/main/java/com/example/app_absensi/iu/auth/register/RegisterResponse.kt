package com.example.app_absensi.iu.auth.register

data class RegisterResponse (
    val status: String, // "success atau fail"
    val userId: String?, // Menyimpan userId yang dikembalikan dari server // jika registrasi berhasil
    val name: String?,
    val massage: String? = null // Pesan Kesalahan (opsional)
)
