package com.example.app_absensi.iu.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.example.app_absensi.R
import com.example.app_absensi.data.repository.UserRepository
import com.example.app_absensi.databinding.FragmentProfileBinding
import com.example.app_absensi.viewmodel.ProfileViewModel
import com.example.app_absensi.viewmodel.ProfileViewModelFactory

/**
 * A simple [Fragment] subclass.
 * Use the [Profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class Profile : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private var imageUri: Uri? = null

    // Launcher untuk mengambil gambar dari kamera
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && imageUri != null) {
            val userId = getUserId()
            if (!userId.isNullOrEmpty()) {
                saveImageUri(userId, imageUri!!)
                cropImage(imageUri!!)
            }
        }
    }

    // Launcher untuk memilih gambar dari galeri
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            try {
                val userId = getUserId()
                if (!userId.isNullOrEmpty()) {
                    requireActivity().contentResolver.takePersistableUriPermission(
                        it,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                    saveImageUri(userId, it)
                    cropImage(it)
                }
            } catch (e: SecurityException) {
                Toast.makeText(requireContext(), "Izin akses gambar ditolak", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Launcher untuk cropping gambar
    private val cropLauncher = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uri = result.uriContent
            uri?.let {
                val userId = getUserId()
                if (!userId.isNullOrEmpty()) {
                    saveImageUri(userId, it)
                    binding.gambar.setImageURI(it)
                    Toast.makeText(requireContext(), "Gambar berhasil diperbarui", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "Pemilihan gambar dibatalkan", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, ProfileViewModelFactory(UserRepository()))
            .get(ProfileViewModel::class.java)

        val userId = getUserId()
        if (!userId.isNullOrEmpty()) {
            viewModel.loadUserProfile(userId)
            loadImage(userId)
        }

        binding.toolbar.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_halamanUtama)
        }

        binding.riwayat.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_listAbsen)
        }

        binding.gambar.setOnClickListener {
            showImagePickerDialog()
        }

        viewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            userProfile?.let {
                binding.tvNama.text = it.nama ?: "Nama tidak tersedia"
                binding.tvJabatan.text = it.jabatan ?: "Jabatan tidak tersedia"
                binding.tvEmail.text = it.email ?: "Email tidak tersedia"
            }
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Kamera", "Galeri")
        AlertDialog.Builder(requireContext())
            .setTitle("Pilih Gambar")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermission()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private fun checkCameraPermission() {
        val cameraPermission = android.Manifest.permission.CAMERA

        if (ContextCompat.checkSelfPermission(requireContext(), cameraPermission)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Izin kamera belum diberikan, minta izin kamera
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(cameraPermission),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            // Izin kamera sudah diberikan, buka kamera
            openCamera()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Izin kamera diberikan
                openCamera()
            } else {
                // Izin kamera ditolak
                Toast.makeText(requireContext(), "Izin kamera diperlukan untuk mengambil foto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
    }


    private fun openCamera() {
        imageUri = createImageUri()
        imageUri?.let { cameraLauncher.launch(it) }
    }

    private fun openGallery() {
        galleryLauncher.launch(arrayOf("image/jpeg", "image/png"))
    }

    private fun cropImage(uri: Uri) {
        cropLauncher.launch(
            CropImageContractOptions(
                uri,
                CropImageOptions().apply {
                    activityTitle = "Edit Gambar"
                    showCropOverlay = true
                    cropShape = CropImageView.CropShape.RECTANGLE
                    fixAspectRatio = true
                    aspectRatioX = 1
                    aspectRatioY = 1
                }
            )
        )
    }

    private fun createImageUri(): Uri? {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "temp_image.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        return requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }

    private fun saveImageUri(userId: String, uri: Uri) {
        val sharedPreferences = requireActivity().getSharedPreferences("UserProfileImages", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("imageUri_$userId", uri.toString()).apply()
    }

    private fun loadImage(userId: String) {
        val uriString = getImageUri(userId)
        uriString?.let {
            val uri = Uri.parse(it)
            if (isUriAvailable(uri)) {
                binding.gambar.setImageURI(uri)
            } else {
                clearImageUri(userId)
                Toast.makeText(requireContext(), "Gambar tidak dapat diakses", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getImageUri(userId: String): String? {
        return requireActivity()
            .getSharedPreferences("UserProfileImages", Context.MODE_PRIVATE)
            .getString("imageUri_$userId", null)
    }

    private fun isUriAvailable(uri: Uri): Boolean {
        return try {
            requireActivity().contentResolver.openInputStream(uri)?.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun clearImageUri(userId: String) {
        requireActivity().getSharedPreferences("UserProfileImages", Context.MODE_PRIVATE)
            .edit().remove("imageUri_$userId").apply()
    }

    private fun getUserId(): String? {
        return requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            .getString("userId", "")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }
}



