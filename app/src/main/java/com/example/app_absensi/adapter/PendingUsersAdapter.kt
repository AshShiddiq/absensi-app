package com.example.app_absensi.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.app_absensi.R
import com.example.app_absensi.data.model.User
import com.example.app_absensi.databinding.ItemPendingUserBinding
import com.example.app_absensi.viewmodel.UserViewModel

class PendingUsersAdapter(
    private var userList: List<User>,
    private val viewModel: UserViewModel

) : RecyclerView.Adapter<PendingUsersAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemPendingUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPendingUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        Log.d("PendingUsersAdapter", "Menampilkan user: ${user.nama}")

        // Menampilkan data user
        holder.binding.tvNama.text = user.nama
        holder.binding.tvJabatan.text = user.jabatan
        holder.binding.tvEmail.text = user.email

        // Tombol verifikasi
        holder.binding.btnVerify.setOnClickListener {
            Log.d("Adapter", "Tombol verifikasi diklik untuk user ID: ${user.id}")

            if (user.id == null || user.id.toString().isEmpty()) {
                Log.e("Adapter", "Error: User ID kosong atau null!")
                return@setOnClickListener
            }

            viewModel.verifyUser(user.id)
        }
    }


    override fun getItemCount(): Int {
        return userList.size
    }

    fun updateData(newList: List<User>) {
        Log.d("PendingUsersAdapter", "Update data, jumlah user: ${newList.size}")
        newList.forEach { user ->
            Log.d("PendingUsersAdapter", "User: ID=${user.id}, Nama=${user.nama}")
        }
        userList = newList
        notifyDataSetChanged()
    }
}


