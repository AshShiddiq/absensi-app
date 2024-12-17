package com.example.app_absensi.data.model

data class Attendance(
    val userId: Int,
    val nama: String,
    val tanggalWaktu: Int,
    val alasan: String?
)
