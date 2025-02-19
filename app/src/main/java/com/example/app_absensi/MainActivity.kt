package com.example.app_absensi

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.app_absensi.databinding.ActivityMainBinding
import com.example.app_absensi.iu.auth.login.HalamanLogin

class MainActivity : AppCompatActivity() {
    private var backPressedTime: Long = 0
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Menginflate layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Mengatur mode malam mengikuti pengaturan sistem
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        // Menambahkan callback untuk menangani tombol Back
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedTime + 2000 > System.currentTimeMillis()) {
                    finishAffinity() // Menutup aplikasi sepenuhnya
                } else {
                    Toast.makeText(this@MainActivity, "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show()
                }
                backPressedTime = System.currentTimeMillis()
            }
        })

        // Mengatur NavHostFragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navhost_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        }

    // Menangani navigasi ke atas
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navhost_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val sharedPreferences = getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE)
        val userRole = sharedPreferences.getString("user_role", "user")

        menu?.findItem(R.id.menu_pending_users)?.isVisible = userRole == "admin"

        return super.onCreateOptionsMenu(menu)
    }

}