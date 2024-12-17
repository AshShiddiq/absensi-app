package com.example.app_absensi.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.app_absensi.data.model.UserProfile
import com.example.app_absensi.data.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: UserRepository): ViewModel() {
    val userProfile = MutableLiveData<UserProfile?>()

    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            val response = repository.getUserProfile(userId)
            if (response.isSuccessful) {
                val responseData = response.body()
                Log.d("ProfileViewModel",  "Response data: $responseData")
                userProfile.postValue(responseData?.user)
            } else {
                // handle error
                Log.e("ProfileViewModel", "Error fetching profile data")
            }
        }
    }
}