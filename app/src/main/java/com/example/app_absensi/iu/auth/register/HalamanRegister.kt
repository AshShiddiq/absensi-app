package com.example.app_absensi.iu.auth.register

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.app_absensi.R
import com.example.app_absensi.data.model.User
import com.example.app_absensi.data.repository.AttendanceRepository
import com.example.app_absensi.databinding.FragmentHalamanRegisterBinding
import com.example.app_absensi.viewmodel.AttendanceViewModel
import com.example.app_absensi.viewmodel.AttendanceViewModelFactory

/**
 * A simple [Fragment] subclass.
 * Use the [HalamanRegister.newInstance] factory method to
 * create an instance of this fragment.
 */
class HalamanRegister : Fragment() {

private lateinit var viewModel: AttendanceViewModel
private lateinit var binding: FragmentHalamanRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHalamanRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = AttendanceRepository()
        val factory = AttendanceViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(AttendanceViewModel::class.java)

        binding.tvToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_halamanRegister_to_halamanLogin)
        }


        viewModel.registerResponse.observe(viewLifecycleOwner, Observer { response ->
            if (response.status == "success") {
                saveUserId(response.userId ?: "")
                Log.d("Response", "Status: ${response.status}, Message: ${response.massage}")
                Toast.makeText(requireContext(), response.massage ?: "Registrasi Berhasil", Toast.LENGTH_SHORT).show()
                // Navigasi ke Halaman utama setelah registrasi berhasil
                findNavController().navigate(R.id.action_halamanRegister_to_halamanLogin)
                response.userId?.let {
                    saveUserId(it)
                }
                response.name?.let {
                    saveUserName(it)
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    response.massage ?: "Registrasi Gagal",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        binding.btnRegister.setOnClickListener {
            val nama = binding.inputUsername.text.toString()
            val jabatan = binding.inputJabatan.text.toString()
            val email = binding.inputEmail2.text.toString()
            val password = binding.inputPassword2.text.toString()

            if (nama.isEmpty() || email.isEmpty() || password.isEmpty() || jabatan.isEmpty()) {
                Toast.makeText(requireContext(),"Semua field harus diisi!", Toast.LENGTH_SHORT).show()
//                val user = User(0, nama, email, password) // id akan ditentukan di backend
            } else {
                viewModel.registerUser(nama, jabatan, email, password)
            }
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
}