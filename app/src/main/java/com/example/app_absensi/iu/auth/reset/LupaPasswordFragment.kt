package com.example.app_absensi.iu.auth.reset

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.app_absensi.R
import com.example.app_absensi.databinding.FragmentLupaPasswordBinding
import com.example.app_absensi.network.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

/**
 * A simple [Fragment] subclass.
 * Use the [LupaPasswordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LupaPasswordFragment : Fragment() {

    private var _binding: FragmentLupaPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using View Binding
        _binding = FragmentLupaPasswordBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

        binding.toolbar.setOnClickListener {
            findNavController().navigate(R.id.action_lupaPasswordFragment_to_halamanLogin)
        }
        // Set click listener for reset password button
        binding.resetPasswordButton.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            if (email.isNotEmpty()) {
                sendResetPasswordRequest(email)
            } else {
                Toast.makeText(requireContext(), "Masukkan email Anda!", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

    private fun sendResetPasswordRequest(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Cek Gmail anda", Toast.LENGTH_SHORT).show()
                } else {
                    val errorMessage = task.exception?.message ?: "Terjadi Kesalahan"
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}