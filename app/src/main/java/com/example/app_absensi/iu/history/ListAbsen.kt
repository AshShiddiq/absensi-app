package com.example.app_absensi.iu.history

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
import androidx.navigation.findNavController
import androidx.navigation.fragment.R
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app_absensi.adapter.AttendanceAdapter
import com.example.app_absensi.data.repository.AttendanceRepository
import com.example.app_absensi.databinding.FragmentListAbsenBinding
import com.example.app_absensi.viewmodel.AttendanceViewModel
import com.example.app_absensi.viewmodel.AttendanceViewModelFactory
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass.
 * Use the [ListAbsen.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListAbsen : Fragment() {

    private lateinit var binding: FragmentListAbsenBinding
    private lateinit var viewModel: AttendanceViewModel
    private lateinit var attendanceAdapter: AttendanceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentListAbsenBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Buat instance repository
        val repository = AttendanceRepository()
        // Buat instance ViewModelFactory
        val factory = AttendanceViewModelFactory(repository)
        viewModel = ViewModelProvider(requireActivity(), factory).get(AttendanceViewModel::class.java)

        // Ambil userId dari SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null) // Pastikan "user_id" sudah disimpan
        userId?.let {
            viewModel.getAttendanceHistory(it)
        } ?: run {
            Log.d("ListAbsenFragment", "User ID is null")
        }

        binding.toolbarHistory.setOnClickListener {
           val action = ListAbsenDirections.actionListAbsenToProfile()
            findNavController().navigate(action)
        }

        binding.progressBar.visibility = View.VISIBLE

        attendanceAdapter = AttendanceAdapter(emptyList())
        binding.recyclerViewHistory.adapter = attendanceAdapter
        binding.recyclerViewHistory.layoutManager= LinearLayoutManager(context)

        viewModel.historyResponse.observe(viewLifecycleOwner) { response ->
            binding.progressBar.visibility = View.GONE
            Log.d("ListAbsenFragment", "Response: ${response.status}")
            if (response.status == "success") {
                response.attendance?.let { attendanceList ->
//                    Log.d("ListAbsenFragment", "Attendance List: ${splitAttendanceData(it)}")
                    attendanceAdapter.updateData(attendanceList)// Mengupdate data pada adapter
                }
            } else {
                Log.d("ListAbsenFragment", "Failed to fetch attendance history: ${response.message}")
                Snackbar.make(binding.root,"Gagal mendapatkan riwayat. Coba lagi", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry") {
                        userId?.let { viewModel.getAttendanceHistory(it) }
                    }.show()
            }
        }

    }


    private fun setupRecyclerView(attendanceList: List<AttendanceRecord>) {
        Log.d("ListAbsenFragment", "Attendance List Size: ${attendanceList.size}")
        val adapter = AttendanceAdapter(attendanceList)
        binding.recyclerViewHistory.adapter = adapter
        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(context)

    }
}
