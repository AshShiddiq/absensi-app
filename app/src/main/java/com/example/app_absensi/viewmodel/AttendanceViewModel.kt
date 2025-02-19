package com.example.app_absensi.viewmodel


import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_absensi.data.model.LunchAttendance
import com.example.app_absensi.data.repository.AttendanceRepository
import com.example.app_absensi.iu.attendance.AbsenResponse
import com.example.app_absensi.iu.attendance.izin.IzinResponse
import com.example.app_absensi.iu.auth.login.LoginResponse
import com.example.app_absensi.iu.auth.register.RegisterResponse
import com.example.app_absensi.iu.history.AttendanceRecord
import com.example.app_absensi.iu.history.HistoryResponse
import kotlinx.coroutines.launch


class AttendanceViewModel(private val repository: AttendanceRepository): ViewModel() {

    val loginResponse: MutableLiveData<LoginResponse> = MutableLiveData()
    val registerResponse: MutableLiveData<RegisterResponse> = MutableLiveData()
    val attendanceResponse: MutableLiveData<AbsenResponse> = MutableLiveData()
    val historyResponse = MutableLiveData<HistoryResponse>()
    val izinResponse: MutableLiveData<IzinResponse> = MutableLiveData()



    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
                val response = repository.loginUser(email, password)
                if (response.isSuccessful) {
                    loginResponse.postValue(response.body())
                } else {
                    loginResponse.postValue(LoginResponse("fail", null,"Login gagal", role = ""))
                }
        }
    }


    fun registerUser(name: String, jabatan: String, email: String, password: String) {
        viewModelScope.launch {
            val response = repository.registerUser(name, jabatan, email, password)
            if (response.isSuccessful) {
                registerResponse.postValue(response.body())
            } else {
                registerResponse.postValue(RegisterResponse("fail", null, "Register gagal"))
            }
        }
    }

    fun absenMasuk(nama: String, jam_masuk: String, lokasi: String) {
        viewModelScope.launch {
            val attendance = mapOf("nama" to nama, "jam_masuk" to jam_masuk, "lokasi" to lokasi)
            val response =repository.absenMasuk(attendance)
            if (response.isSuccessful) {
                attendanceResponse.postValue(response.body())
            } else {
                attendanceResponse.postValue(AbsenResponse("fail", "absen masuk gagal"))
            }
        }
    }

    fun absenKeluar(nama: String, jam_keluar: String, lokasi: String) {
        viewModelScope.launch {
            val attendance = mapOf("nama" to nama, "jam_keluar" to jam_keluar, "lokasi" to lokasi)
            val response = repository.absenKeluar(attendance)
            if (response.isSuccessful) {
                attendanceResponse.postValue(response.body())
            } else {
                attendanceResponse.postValue(AbsenResponse("fail", "Absen Keluar gagal"))
            }
        }
    }

    fun absenMakan(nama: String, departemen: String, tanggalKonfirmasi: String, kehadiran: String, makanSiang: String, bawaTamu: String, jumlahTamu: Int?) {
        viewModelScope.launch {
            try {
                val attendance = LunchAttendance(
                    id = null,
                    nama = nama,
                    departemen = departemen,
                    tanggal_konfirmasi = tanggalKonfirmasi,
                    kehadiran = if (kehadiran == "Ya") "Ya" else "Tidak",
                    makan_siang = if (makanSiang == "Ya") "Ya" else "Tidak",
                    bawa_tamu = if (bawaTamu == "Ya") "Ya" else "Tidak",
                    jumlah_Tamu = jumlahTamu
                )
                val response = repository.absenMakan(attendance)
                if (response.isSuccessful) {
                    Log.d("API Success Response", response.body().toString())
                    attendanceResponse.postValue(response.body())
                } else {
                    Log.e("API Error Response", response.errorBody()?.string() ?: "Unknown error")
                    attendanceResponse.postValue(AbsenResponse("fail", "Absen makan gagal"))
                }
            } catch (e: Exception) {
                Log.e("AbsenMakanError", "Error submitting lunch attendance", e)
                attendanceResponse.postValue(AbsenResponse("fail", "Terjadi kesalahan"))
            }
        }
    }

    fun getAttendanceHistory(userId: String) {
        viewModelScope.launch {
            val response = repository.getAttendanceHistory(userId)
            if (response.isSuccessful) {
                response.body()?.let { historyResponse ->
                    // Mengambil daftar attendance dari historyResponse
                    val attendanceList = historyResponse.attendance ?: emptyList()
                    // Memisahkan data
                    val splitAttendanceList = splitAttendanceData(attendanceList)
                    // Memposting hasil
                    this@AttendanceViewModel.historyResponse.postValue(HistoryResponse("success", null, splitAttendanceList))
                } ?: run {
                    this@AttendanceViewModel.historyResponse.postValue(HistoryResponse("fail", "Data tidak tersedia", null))
                }
            } else {
                this@AttendanceViewModel.historyResponse.postValue(HistoryResponse("fail", "Gagal mendapatkan riwayat", null))
            }
        }
    }

    private fun splitAttendanceData(attendanceList: List<AttendanceRecord>): List<AttendanceRecord> {
        val splitList = mutableListOf<AttendanceRecord>()

        for (attendance in attendanceList) {
            val lokasi = attendance.lokasi ?: "Lokasi tidak diketahui" // Beri nilai default jika lokasi null

            // Jika ada jam masuk, tambahkan entri absen masuk
            if (attendance.jam_masuk != null) {
                splitList.add(
                    AttendanceRecord(
                        id = attendance.id,
                        nama = attendance.nama,
                        lokasi = attendance.lokasi,
                        jam_masuk = attendance.jam_masuk,
                        jam_keluar = null // Kosongkan jam Keluar untuk absen masuk
                    )
                )
            }

            if (attendance.jam_keluar != null) {
                splitList.add(
                    AttendanceRecord(
                        id = attendance.id,
                        nama = attendance.nama,
                        lokasi = attendance.lokasi,
                        jam_masuk = null, // Kosongkan jam masuk untuk absen keluar
                        jam_keluar = attendance.jam_keluar
                    )
                )
            }
        }
        return splitList
    }


    fun izinTidakHadir(nama: String, tanggal: String, alasan: String) {
        viewModelScope.launch {
            val izinData = mapOf("nama" to nama, "tanggal" to tanggal, "alasan" to alasan)
            val response = repository.izinTidakHadir(izinData)
            if (response.isSuccessful) {
                izinResponse.postValue(response.body())
            } else {
                izinResponse.postValue(IzinResponse("fail", "Izin tidak hadir gagal"))
            }
        }
    }

}