package com.example.app_absensi.iu.attendance.absenmakan

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.app_absensi.R
import com.example.app_absensi.data.repository.AttendanceRepository
import com.example.app_absensi.databinding.FragmentAbsenMakanBinding
import com.example.app_absensi.viewmodel.AttendanceViewModel
import com.example.app_absensi.viewmodel.AttendanceViewModelFactory
import java.util.Calendar

/**
 * A simple [Fragment] subclass.
 * Use the [AbsenMakan.newInstance] factory method to
 * create an instance of this fragment.
 */
class AbsenMakan : Fragment() {

    private lateinit var binding: FragmentAbsenMakanBinding
    private lateinit var viewModel: AttendanceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAbsenMakanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = AttendanceRepository()
        val factory = AttendanceViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(AttendanceViewModel::class.java)

        binding.toolbar.setOnClickListener {
            findNavController().navigate(R.id.action_absenMakan_to_halamanUtama)
        }

        binding.inputTanggal.setOnClickListener {
            showDatePickerDialog()
        }

//        setupListeners()
//        observeViewModel()

        viewModel.attendanceResponse.observe(viewLifecycleOwner) { response ->
            if (response.status == "success") {
                Log.d("API Success", "Absen makan berhasil")
                Toast.makeText(requireContext(),response.message ?: "Absen berhasil", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_absenMakan_to_halamanUtama)
            } else {
                Log.d("API Error", "Absen gagal")
                Toast.makeText(requireContext(), response.message ?: "Absen gagal", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSubmit.setOnClickListener {
            val nama = binding.inputNama.text.toString()
            val departemen = binding.etDepartemen.text.toString()
            val tanggalKonfirmasi = binding.inputTanggal.text.toString()
            val kehadiran = if (binding.rbHadirYa.isChecked) "Ya" else "Tidak"
            val makanSiang = if (binding.rbMakanSiangYa.isChecked) "Ya" else "Tidak"
            val bawaTamu = if (binding.rbBawaTamuYa.isChecked) "Ya" else "Tidak"
            val jumlahTamu = binding.etTamu.text.toString().toIntOrNull() ?: 0

            if (nama.isEmpty() || departemen.isEmpty() || tanggalKonfirmasi.isEmpty()) {
                Toast.makeText(requireContext(), "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.absenMakan(nama, departemen, tanggalKonfirmasi, kehadiran, makanSiang, bawaTamu, jumlahTamu)
        }
    }


//    private fun setupListeners() {
//        binding.btnSubmit.setOnClickListener {
//            val nama = binding.inputNama.text.toString()
//            val departemen = binding.etDepartemen.text.toString()
//            val tanggal_Konfirmasi = binding.inputTanggal.text.toString()
//
//            // Mendapatkan nilai dari radio button untuk kehadiran, makan siang, dan bawa tamu
//            val kehadiran = if (binding.rbHadirYa.isChecked) "Ya" else "Tidak"
//            val makan_Siang = if (binding.rbMakanSiangYa.isChecked) "Ya" else "Tidak"
//            val bawa_Tamu = if (binding.rbBawaTamuYa.isChecked) "Ya" else "Tidak"
//
//            // Mendapatkan jumlah tamu, atau 0 jika kosong
//            val jumlahTamu = binding.etTamu.text.toString().toIntOrNull() ?: 0
//
//            if (nama.isNotEmpty() && departemen.isNotEmpty() && tanggal_Konfirmasi.isNotEmpty()) {
//                Log.d("Absen Data", "Nama: $nama, Departemen: $departemen, Tanggal: $tanggal_Konfirmasi, Kehadiran: $kehadiran, MakanSiang: $makan_Siang, BawaTamu: $bawa_Tamu, JumlahTamu: $jumlahTamu")
//                viewModel.absenMakan(
//                    nama = nama,
//                    departemen = departemen,
//                    tanggalKonfirmasi = tanggal_Konfirmasi,
//                    kehadiran = kehadiran,
//                    makanSiang = makan_Siang,
//                    bawaTamu = bawa_Tamu,
//                    jumlahTamu = jumlahTamu
//                )
//            } else {
//                Toast.makeText(context, "Mohon lengkapi semua data", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun observeViewModel() {
//        viewModel.attendanceResponse.observe(viewLifecycleOwner) { response ->
//            if (response != null) {
//                if (response.status == "success") {
//                    Toast.makeText(
//                        context,
//                        "Absen makan siang berhasil disimpan",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                } else {
//                    Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                Toast.makeText(context, "Gagal menyimpan absen makan siang", Toast.LENGTH_SHORT)
//                    .show()
//            }
//        }
//    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Format tanggal yang dipilih dan set ke EditText
                val formattedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                binding.inputTanggal.setText(formattedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
}


