package com.example.app_absensi.iu.profile


import com.example.app_absensi.data.model.UserProfile

data class UserProfileResponse(
    val status: String,
    val user: UserProfile?
)

