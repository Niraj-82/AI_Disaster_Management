package com.example.resqai.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.resqai.R
import com.example.resqai.model.Announcement
import java.text.SimpleDateFormat
import java.util.*

class AnnouncementAdapter(private val announcements: List<Announcement>) :
    RecyclerView.Adapter<AnnouncementAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_announcement, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val announcement = announcements[position]
        holder.title.text = announcement.title
        holder.message.text = announcement.message

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        holder.timestamp.text = sdf.format(Date(announcement.timestamp))
    }

    override fun getItemCount(): Int {
        return announcements.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_announcement_title)
        val message: TextView = itemView.findViewById(R.id.tv_announcement_message)
        val timestamp: TextView = itemView.findViewById(R.id.tv_announcement_timestamp)
    }
}