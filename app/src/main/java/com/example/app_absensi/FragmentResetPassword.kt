package com.example.app_absensi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.app_absensi.network.RetrofitInstance

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentResetPassword.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentResetPassword : Fragment() {

    private lateinit var newPasswordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var savePasswordButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reset_password, container, false)
//        newPasswordInput = view.findViewById(R.id.newPassword)
//        confirmPasswordInput = view.findViewById(R.id.confirmPassword)
//        savePasswordButton = view.findViewById(R.id.savePasswordButton)
//
//        savePasswordButton.setOnClickListener {
//            val newPassword = newPasswordInput.text.toString()
//            val confirmPassword = confirmPasswordInput.text.toString()
//
//            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
//                Toast.makeText(requireContext(), "Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
//            } else if (newPassword != confirmPassword) {
//                Toast.makeText(requireContext(), "Password tidak sama", Toast.LENGTH_SHORT).show()
//            } else {
//
//            }
//        }
        return view
    }

//    private fun changePassword(password: String) {
//        val token = "TOKEN_DARI_BACKEND"
//        val apiService = RetrofitInstance.api
//        apiService.changePasswo
//    }

}