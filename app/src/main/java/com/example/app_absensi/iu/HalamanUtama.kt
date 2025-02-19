package com.example.app_absensi.iu

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.app_absensi.R
import com.example.app_absensi.databinding.FragmentHalamanUtamaBinding

/**
 * A simple [Fragment] subclass.
 * Use the [HalamanUtama.newInstance] factory method to
 * create an instance of this fragment.
 */
class HalamanUtama : Fragment() {

    private lateinit var binding: FragmentHalamanUtamaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       binding = FragmentHalamanUtamaBinding.inflate(inflater, container, false)

        binding.cvAbsenMasuk.setOnClickListener {
            it.findNavController().navigate(R.id.action_halamanUtama_to_halamanAbsen)
        }
        binding.cvAbsenKeluar.setOnClickListener {
            it.findNavController().navigate(R.id.action_halamanUtama_to_halamanAbsen2)
        }
        binding.btnLogout.setOnClickListener {
            logout()
        }
        binding.cvPerizinan.setOnClickListener {
            it.findNavController().navigate(R.id.action_halamanUtama_to_halamanPerizinan)
        }
        binding.profile.setOnClickListener {
            it.findNavController().navigate(R.id.action_halamanUtama_to_profile)
        }
        binding.cvAbsenMakan.setOnClickListener {
            it.findNavController().navigate(R.id.action_halamanUtama_to_absenMakan)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ambil nama user dari SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)
        val userName = sharedPref.getString("user_name", "User") // "User" adalah nilai default jika tidak ada nama user

        // Set text....
        binding.tvWelcome.text = "Selamat Datang, $userName\n di Aplikasi Absensi Digital"
    }

    fun logout() {
        val sharedPref = requireContext().getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.nav_graph, true)
            .build()

        findNavController().navigate(R.id.action_halamanUtama_to_halamanLogin, null, navOptions)

        // Tutup semua aktivitas agar tidak bisa kembali ke halaman sebelumnya
//        requireActivity().finishAffinity()
    }

}