package com.example.app_absensi.data.model

data class LunchAttendance(
    val id: Int? = null,
    val nama: String,
    val departemen: String,
    val tanggal_konfirmasi: String,
    val kehadiran: String,
    val makan_siang: String,
    val bawa_tamu: String,
    val jumlah_Tamu: Int? = 0
)

