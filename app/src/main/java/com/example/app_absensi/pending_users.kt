package com.example.app_absensi

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app_absensi.adapter.PendingUsersAdapter
import com.example.app_absensi.data.repository.UserRepository
import com.example.app_absensi.databinding.FragmentPendingUsersBinding
import com.example.app_absensi.viewmodel.UserViewModel
import com.example.app_absensi.viewmodel.UserViewModelFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [pending_users.newInstance] factory method to
 * create an instance of this fragment.
 */
class pending_users : Fragment() {

    private lateinit var binding: FragmentPendingUsersBinding
    private lateinit var viewModel: UserViewModel
    private lateinit var pendingUsersAdapter: PendingUsersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPendingUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireContext().getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE)
        val userRole = sharedPreferences.getString("user_role", "user")

        if (userRole != "admin") {
            Toast.makeText(requireContext(), "Anda tidak memiliki akses ke halaman ini", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_pending_users_to_halamanUtama)
        }

        val repository = UserRepository()
        val factory = UserViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)

        // Inisialisasi Adapter
        pendingUsersAdapter = PendingUsersAdapter(mutableListOf(), viewModel)
        binding.recyclerViewPendingUsers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pendingUsersAdapter
        }


        binding.recyclerViewPendingUsers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pendingUsersAdapter
        }


        // Observasi data user yang belum diverifikasi
        viewModel.pendingUsers.observe(viewLifecycleOwner) { userList ->
            Log.d("PendingUsers", "Data diterima di Fragment: ${userList.size}")
            pendingUsersAdapter.updateData(userList)
        }

        binding.toolbarHistory.setOnClickListener {
            findNavController().navigate(R.id.action_pending_users_to_halamanLogin)
        }
        // Observasi status verifikasi
        viewModel.verificationStatus.observe(viewLifecycleOwner) { status ->
            if (status == "success") {
                Toast.makeText(requireContext(), "User berhasil diverifikasi!", Toast.LENGTH_SHORT).show()
                viewModel.getPendingUsers() // Ambil ulang daftar user
            } else {
                Toast.makeText(requireContext(), "Gagal memverifikasi user", Toast.LENGTH_SHORT).show()
            }
        }


        // Ambil daftar user yang belum diverifikasi
        viewModel.getPendingUsers()
    }
}
