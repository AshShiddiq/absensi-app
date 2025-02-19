package com.example.app_absensi.iu.auth.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.app_absensi.R
import com.example.app_absensi.data.repository.AttendanceRepository
import com.example.app_absensi.databinding.FragmentHalamanLoginBinding
import com.example.app_absensi.viewmodel.AttendanceViewModel
import com.example.app_absensi.viewmodel.AttendanceViewModelFactory

/**
 * A simple [Fragment] subclass.
 * Use the [HalamanLogin.newInstance] factory method to
 * create an instance of this fragment.
 */
class HalamanLogin : Fragment() {

    private lateinit var viewModel: AttendanceViewModel
    private lateinit var binding: FragmentHalamanLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHalamanLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //repository instance
        val repository = AttendanceRepository()
        val factory = AttendanceViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory )[AttendanceViewModel::class.java]

        // Observe perubahan pada LiveData loginResponse
        viewModel.loginResponse.observe(viewLifecycleOwner, Observer { response ->

            if (response.status == "success") {

                // log untuk memastikan bahwa kode ini dijalankan
                Log.d("LoginFragment", "Login berhasil, berpindah halaman.")
                Log.d("LoginFragment", "Role yang diterima dari response: ${response.role}")
//                Toast.makeText(requireContext(), response.message ?: "Login Berhasil", Toast.LENGTH_SHORT).show()
                // Navigasi ke halaman home jika login berhasil
//                findNavController().navigate(R.id.action_halamanLogin_to_halamanUtama)
                // Simpan userId di SharedPreferences
                 response.userId?.let {
                    saveUserId(it)
                }
                response.name?.let {
                    saveUserName(it)
                }
                response.role?.let {
                    saveUserRole(it)
                }

                val userEmail = binding.inputEmail.text.toString()
                val finalRole = if (userEmail == "admin@gmail.com") "admin" else response.role

                if (finalRole == "admin") {
                    findNavController().navigate(R.id.action_halamanLogin_to_pending_users)
                } else {
                    findNavController().navigate(R.id.action_halamanLogin_to_halamanUtama)
                }
            } else {
                // Tampilkan pesan error jika login gagal
                Toast.makeText(requireContext(), response.message ?: "Login gagal", Toast.LENGTH_SHORT
                ).show()
            }
        })

        // Ketika tombol login ditekan
        binding.btnLogin.setOnClickListener {
            val email = binding.inputEmail.text.toString().trim()
            val password = binding.inputPassword.text.toString().trim()

            // Validasi input, dan panggil API login
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Email dan Password harus diisi", Toast.LENGTH_SHORT).show()
            } else {
                // Panggil fungsi login
                viewModel.loginUser(email, password)
            }
        }


        binding.tvToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_halamanLogin_to_halamanRegister)
        }

        binding.forgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_halamanLogin_to_lupaPasswordFragment)
        }
    }

    private fun saveUserName(userName: String) {
        val sharedPref = requireContext().getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("user_name", userName) // Menyimpan nama user
        editor.apply() // Menyimpan perubahan ke SharedPreferences
    }

    private fun saveUserId(userId: String) {
        // Simpan userId menggunakan SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userId", userId)
        editor.apply()
    }

    fun saveUserRole(role: String) {
        context?.let { ctx ->
            val sharedPreferences = ctx.getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE)
            sharedPreferences.edit()
                .putString("user_role", role)
                .apply()
        }
    }
}