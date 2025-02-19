package com.example.app_absensi.data.repository

import com.example.app_absensi.VerifyUserRequest
import com.example.app_absensi.data.model.DefaultResponse
import com.example.app_absensi.data.model.UserListResponse
import com.example.app_absensi.iu.profile.UserProfileResponse
import com.example.app_absensi.network.RetrofitInstance
import retrofit2.Response

class UserRepository {

    private val apiService = RetrofitInstance.api

    suspend fun getUserProfile(userId: String): Response<UserProfileResponse> {
        return RetrofitInstance.api.getUserProfile(userId)
    }

    suspend fun getPendingUsers(): Response<UserListResponse> {
        return apiService.getPendingUsers()
    }

    suspend fun verifyUser(userId: Int): Response<DefaultResponse> {
        return apiService.verifyUser(VerifyUserRequest(userId)) // BENAR
    }




}