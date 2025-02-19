package com.example.app_absensi.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_absensi.data.model.User
import com.example.app_absensi.data.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    val pendingUsers: MutableLiveData<List<User>> = MutableLiveData()
    val verificationStatus: MutableLiveData<String> = MutableLiveData()

    fun getPendingUsers() {
        viewModelScope.launch {
            val response = repository.getPendingUsers()
            if (response.isSuccessful) {
                pendingUsers.postValue(response.body()?.users ?: emptyList())
            }
        }
    }

    fun verifyUser(userId: Int) {
        viewModelScope.launch {
            val response = repository.verifyUser(userId)
            if (response.isSuccessful) {
                val responseBody = response.body()
                Log.d("VerifyUser", "Response: $responseBody")
                verificationStatus.postValue("success")
                getPendingUsers() // Refresh daftar user setelah verifikasi
            } else {
                Log.e("VerifyUser", "Failed: ${response.errorBody()?.string()}")
                verificationStatus.postValue("failed")
            }
        }
    }



}





