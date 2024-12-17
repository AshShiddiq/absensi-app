package com.example.app_absensi.data.repository

import com.example.app_absensi.iu.profile.UserProfileResponse
import com.example.app_absensi.network.RetrofitInstance
import retrofit2.Response

class UserRepository {
    suspend fun getUserProfile(userId: String): Response<UserProfileResponse> {
        return RetrofitInstance.api.getUserProfile(userId)
    }
}