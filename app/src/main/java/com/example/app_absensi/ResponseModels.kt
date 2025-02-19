package com.example.app_absensi

import com.example.app_absensi.data.model.User

data class ResponseModels (
    val status: String,
    val users: List<User>?
)

data class ResponseData(
    val status: String,
    val message: String
)

