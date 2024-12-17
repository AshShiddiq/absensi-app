package com.example.app_absensi.iu.attendance.absenmasuk

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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.app_absensi.R
import com.example.app_absensi.data.repository.AttendanceRepository
import com.example.app_absensi.databinding.FragmentHalamanAbsenBinding
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
 * Use the [HalamanAbsen.newInstance] factory method to
 * create an instance of this fragment.
 */
class
HalamanAbsen : Fragment() {

    private lateinit var viewModel: AttendanceViewModel
    private lateinit var binding: FragmentHalamanAbsenBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       binding = FragmentHalamanAbsenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val repository = AttendanceRepository()
        val factory = AttendanceViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(AttendanceViewModel::class.java)

        // Langsung set tanggal dan waktu saat fragmen di-load
        setCurrentDateTime()

        // Set listener for EditText to show date and time picker
        binding.inputTanggalWaktu.setOnClickListener {
            showDateTimePicker()
        }

//         Set listener untuk lokasi
        binding.inputLokasi.setOnClickListener {
            requestLocationPermission()
        }

        binding.toolbar.setOnClickListener {
            findNavController().navigate(R.id.action_halamanAbsen_to_halamanUtama)
        }

            viewModel.attendanceResponse.observe(viewLifecycleOwner) { response ->
                if (response.status == "success") {
                    Log.d("API Success", "Absen masuk berhasil")
                    Toast.makeText(requireContext(), response.message ?: "Absen masuk berhasil", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_halamanAbsen_to_halamanUtama)
                } else {
                    Log.d("API Error", "Absen gagal")
                    Toast.makeText(requireContext(), response.message ?: "Absen gagal", Toast.LENGTH_SHORT).show()
                }
            }

        binding.btnAbsenMasuk.setOnClickListener {
            val nama = binding.inputNama.text.toString()
            val jam_masuk = binding.inputTanggalWaktu.text.toString()
            val lokasi = binding.inputLokasi.text.toString()

            if (nama.isEmpty() || jam_masuk.isEmpty() || lokasi.isEmpty()) {
                Toast.makeText(requireContext(), "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener  // Hentikan eksekusi lebih lanjut
            }
            viewModel.absenMasuk(nama, jam_masuk, lokasi)
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
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
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
                            binding.inputLokasi.setText(address)
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
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Izin diberikan
                getLastKnowLocation()
            } else {
                // Izin ditolak
                Toast.makeText(context, "Izin akses lokasi diperlukan!", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun setCurrentDateTime() {
        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        binding.inputTanggalWaktu.setText(currentDate)
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
                binding.inputTanggalWaktu.setText(dateTime)
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show()

        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH)).show()
    }
}