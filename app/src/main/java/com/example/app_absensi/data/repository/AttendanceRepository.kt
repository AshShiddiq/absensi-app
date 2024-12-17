package com.example.app_absensi.data.repository


import com.example.app_absensi.data.model.LunchAttendance
import com.example.app_absensi.iu.attendance.AbsenResponse
import com.example.app_absensi.iu.attendance.izin.IzinResponse
import com.example.app_absensi.iu.auth.login.LoginRequest
import com.example.app_absensi.iu.auth.login.LoginResponse
import com.example.app_absensi.iu.auth.register.RegisterRequest
import com.example.app_absensi.iu.auth.register.RegisterResponse
import com.example.app_absensi.iu.history.HistoryResponse
import com.example.app_absensi.network.RetrofitInstance
import retrofit2.Response

class AttendanceRepository  {
   suspend fun registerUser(nama: String, jabatan: String, email: String, password: String): Response<RegisterResponse> {
       val request = RegisterRequest(nama, jabatan, email, password)
       return RetrofitInstance.api.registerUser(request)
   }
    suspend fun loginUser(email: String, password: String): Response<LoginResponse> {
        return RetrofitInstance.api.loginUser(LoginRequest(email, password))
    }
    suspend fun absenMasuk(data: Map<String, String>): Response<AbsenResponse> {
        return RetrofitInstance.api.absenMasuk(data)
    }
    suspend fun absenKeluar(data: Map<String, String>): Response<AbsenResponse> {
        return RetrofitInstance.api.absenKeluar(data)
    }
    suspend fun getAttendanceHistory(userId: String): Response<HistoryResponse> {
        return RetrofitInstance.api.getAttendanceHistory(userId)
    }
    suspend fun izinTidakHadir(izinData: Map<String, String>): Response<IzinResponse> {
        return RetrofitInstance.api.izinTidakHadir(izinData)
    }

    suspend fun absenMakan(attendance: LunchAttendance): Response<AbsenResponse> {
        return RetrofitInstance.api.absenMakan(attendance)
    }

}