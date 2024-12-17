package com.example.app_absensi.iu.history

class AttendanceRecord (
    val id: Int,
    val nama: String,
    val lokasi: String?,
    val jam_masuk: String?,  // nullable jika absen masuk belum dilakukan
    val jam_keluar: String?   // nullable jika absen keluar belum dilakukan
)
