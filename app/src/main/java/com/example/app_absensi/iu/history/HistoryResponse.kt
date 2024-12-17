package com.example.app_absensi.iu.history

import com.example.app_absensi.data.model.Attendance

data class HistoryResponse (
    val status: String, // "success" atau "fail"
    val message: String?, // pesan kesalahan (opsional)
    val attendance: List<AttendanceRecord>?// Daftar riwayat absensi
)
