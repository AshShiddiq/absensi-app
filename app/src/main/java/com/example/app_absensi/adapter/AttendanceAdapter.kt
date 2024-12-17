package com.example.app_absensi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.drawerlayout.R
import androidx.recyclerview.widget.RecyclerView
import com.example.app_absensi.databinding.ItemAttendanceBinding
import com.example.app_absensi.iu.history.AttendanceRecord


class AttendanceAdapter(private var attendanceList: List<AttendanceRecord>) : RecyclerView.Adapter<AttendanceAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemAttendanceBinding ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAttendanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val attendance = attendanceList[position]
       // Set nomor dan nama
        holder.binding.tvNomor.text = attendance.id.toString()
        holder.binding.tvNama.text = attendance.nama
       // Lokasi absen (jika ada)
        holder.binding.tvLokasi.text = attendance.lokasi ?: "Lokasi tidak tersedia"
       // Waktu absen sesuai dengan status absen
       if (attendance.jam_masuk != null) {
       // Jika hanya ada jam masuk
           holder.binding.tvWaktuAbsen.text = attendance.jam_masuk
           holder.binding.tvStatusAbsen.text = "Absen Masuk"
       } else if ( attendance.jam_keluar != null) {
           holder.binding.tvWaktuAbsen.text = attendance.jam_keluar
           holder.binding.tvStatusAbsen.text = "Absen Keluar"
       }

    }

    fun updateData(newList: List<AttendanceRecord>) {
        attendanceList = newList
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return attendanceList.size
    }

}