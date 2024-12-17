package com.example.app_absensi.network

import com.example.app_absensi.data.model.LunchAttendance
import com.example.app_absensi.iu.attendance.AbsenResponse
import com.example.app_absensi.iu.attendance.izin.IzinResponse
import com.example.app_absensi.iu.auth.login.LoginRequest
import com.example.app_absensi.iu.auth.login.LoginResponse
import com.example.app_absensi.iu.auth.register.RegisterRequest
import com.example.app_absensi.iu.auth.register.RegisterResponse
import com.example.app_absensi.iu.auth.reset.ApiResponse
import com.example.app_absensi.iu.history.HistoryResponse
import com.example.app_absensi.iu.profile.UserProfileResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


// Tambahkan log interceptor untuk mencetak data API
    val loggingInterceptor = HttpLoggingInterceptor().apply {
       level = HttpLoggingInterceptor.Level.BODY  // Cetak seluruh data API
}

    // Buat client dengan logging
//    val client = OkHttpClient.Builder()
//        .addInterceptor(loggingInterceptor)
//        .build()


val client = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)  // Default adalah 10 detik
    .writeTimeout(30, TimeUnit.SECONDS)    // Default adalah 10 detik
    .readTimeout(30, TimeUnit.SECONDS)     // Default adalah 10 detik
    .build()



object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://192.168.0.111/absensi/") // Sesuaikan dengan URL server lokal
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

interface ApiService {
    @POST("register.php")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("login.php")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>

    @FormUrlEncoded
    @POST("reset_password.php")
    fun resetPassword(@Field("email") email: String): Call<ApiResponse>

    @POST("absen_masuk.php")
    suspend fun absenMasuk(@Body attendance: Map<String, String>): Response<AbsenResponse>

    @POST("absen_keluar.php")
    suspend fun absenKeluar(@Body attendance: Map<String, String>): Response<AbsenResponse>

    @GET("history.php")
    suspend fun getAttendanceHistory(@Query ("user_id") userId: String): Response<HistoryResponse>

    @POST("izin.php")
    suspend fun izinTidakHadir(@Body izinData: Map<String, String>): Response<IzinResponse>

    @GET("profile.php")
    suspend fun getUserProfile(@Query ("userId") userId: String): Response<UserProfileResponse>

    @POST("confirm.php")
    suspend fun absenMakan(@Body attendance: LunchAttendance): Response<AbsenResponse>

}