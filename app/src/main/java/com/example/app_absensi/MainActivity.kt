package com.example.app_absensi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.app_absensi.databinding.ActivityMainBinding
import com.example.app_absensi.iu.auth.login.HalamanLogin

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Menginflate layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Mengatur mode malam mengikuti pengaturan sistem
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        // Mengatur NavHostFragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navhost_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        }

    // Menangani navigasi ke atas
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navhost_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()

    }
}