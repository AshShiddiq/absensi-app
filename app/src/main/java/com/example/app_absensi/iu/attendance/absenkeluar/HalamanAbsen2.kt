package com.example.app_absensi.iu.attendance.absenkeluar

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.R
import androidx.navigation.fragment.findNavController
import com.example.app_absensi.data.repository.AttendanceRepository
import com.example.app_absensi.databinding.FragmentHalamanAbsen2Binding
import com.example.app_absensi.iu.attendance.absenmasuk.HalamanAbsen
import com.example.app_absensi.viewmodel.AttendanceViewModel
import com.example.app_absensi.viewmodel.AttendanceViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * A simple [Fragment] subclass.
 * Use the [HalamanAbsen2.newInstance] factory method to
 * create an instance of this fragment.
 */
class HalamanAbsen2 : Fragment() {

    private lateinit var viewModel: AttendanceViewModel
    private lateinit var binding: FragmentHalamanAbsen2Binding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHalamanAbsen2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val repository = AttendanceRepository()
        val factory = AttendanceViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(AttendanceViewModel::class.java)

        // set tanggal dan waktu saat fragment di-load
        setCurrentDateTime()

        // Set listener for EditText to show date and time picker
        binding.inputTanggalwaktu.setOnClickListener {
            showDateTimePicker()
        }

        binding.inputLokasiKeluar.setOnClickListener {
            requestLocationPermission()
        }

        binding.toolbar2.setOnClickListener {
            val action = HalamanAbsen2Directions.actionHalamanAbsen2ToHalamanUtama()
            findNavController().navigate(action)
        }



        viewModel.attendanceResponse.observe(viewLifecycleOwner) { response ->

            if (response.status == "success") {
                Toast.makeText(requireContext(), "Absen Keluar berhasil", Toast.LENGTH_SHORT).show()
                val action = HalamanAbsen2Directions.actionHalamanAbsen2ToHalamanUtama()
                findNavController().navigate(action)

            } else {
                Toast.makeText(requireContext(), response.message ?: "Absen gagal", Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnAbsenKeluar.setOnClickListener {
            val nama = binding.inputNama2.text.toString()
            val jam_keluar = binding.inputTanggalwaktu.text.toString()
            val lokasi = binding.inputLokasiKeluar.text.toString()

            if (nama.isEmpty() || jam_keluar.isEmpty() || lokasi.isEmpty()) {
                Toast.makeText(requireContext(), "Semua field harus diisi", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            viewModel.absenKeluar(nama, jam_keluar, lokasi)
        }

    }

    private fun openMaps() {
        val gmmIntentUri = Uri.parse("geo:0,0?q=")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(requireActivity().packageManager) == null) {
            Toast.makeText(requireContext(), "Aplikasi Google Maps tidak tersedia, silakan instal terlebih dahulu.", Toast.LENGTH_LONG).show()
        } else {
            startActivity(mapIntent)
        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "Meminta izin lokasi", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                HalamanAbsen.LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            getLastKnowLocation()
        }
    }

    private fun getLastKnowLocation() {
        try {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude

                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        try {
                            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

                            // Gunakan safe call dan elvis operator
                            val address = addresses?.get(0)?.getAddressLine(0) ?: "Alamat tidak ditemukan"
                            binding.inputLokasiKeluar.setText(address)
                        } catch (e: IOException) {
                            Toast.makeText(requireContext(), "Gagal mengonversi lokasi menjadi alamat", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Tidak dapat menemukan lokasi. Pastikan GPS aktif.", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Gagal mendapatkan lokasi.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Izin lokasi belum diberikan.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: SecurityException) {
            Toast.makeText(requireContext(), "Akses lokasi tidak diizinkan", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == HalamanAbsen.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Izin diberikan
                getLastKnowLocation()
            } else {
                // Izin ditolak
                Toast.makeText(context, "Izin akses lokasi diperlukan!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setCurrentDateTime(){
        val curretDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault()).format(Date())
        binding.inputTanggalwaktu.setText(curretDate)
    }

    private fun showDateTimePicker() {
        val currentDate = Calendar.getInstance()

        // DatePickerDialog to select the date
        DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            val selectedDate = "$year-${month + 1}-$dayOfMonth"

            // TimePickerDialog to select the time after the date is selected
            TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                // Combine date and time and set to EditText
                val dateTime = "$selectedDate $selectedTime"
                binding.inputTanggalwaktu.setText(dateTime)
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show()

        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH)).show()
    }
}
