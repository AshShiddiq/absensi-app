package com.example.app_absensi.iu.attendance.izin

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.app_absensi.R
import com.example.app_absensi.data.repository.AttendanceRepository
import com.example.app_absensi.databinding.FragmentHalamanPerizinanBinding
import com.example.app_absensi.viewmodel.AttendanceViewModel
import com.example.app_absensi.viewmodel.AttendanceViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * A simple [Fragment] subclass.
 * Use the [HalamanPerizinan.newInstance] factory method to
 * create an instance of this fragment.
 */
class HalamanPerizinan : Fragment() {

    private lateinit var binding: FragmentHalamanPerizinanBinding
    private lateinit var viewModel: AttendanceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHalamanPerizinanBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = AttendanceRepository()
        val factory = AttendanceViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(AttendanceViewModel::class.java)

        // Set tanggal
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        binding.inputTanggal.setText(currentDate)

        binding.toolbar.setOnClickListener {
            it.findNavController().navigate(R.id.action_halamanPerizinan_to_halamanUtama)
        }


        // Mengamati hasil dari pengajuan izin
        viewModel.izinResponse.observe(viewLifecycleOwner) { response ->
            if (response.status == "success") {
                Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_halamanPerizinan_to_halamanUtama)
            } else {
                Toast.makeText(requireContext(), "Gagal: ${response.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.btnIzin.setOnClickListener {
            val nama = binding.inputNama.text.toString()
            val tanggal = binding.inputTanggal.text.toString()
            val alasan = binding.inputKeterangan.text.toString()

            // Validasi input
            if (nama.isEmpty() || tanggal.isEmpty() || alasan.isEmpty()) {
                Toast.makeText(requireContext(), "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.izinTidakHadir(nama, tanggal, alasan)
        }
        // Make the EditText clickable to show DatePicker
        binding.inputTanggal.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Format memilih hari
                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                binding.inputTanggal.setText(selectedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }
}