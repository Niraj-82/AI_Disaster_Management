package com.example.aidm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AnnouncementAdapter(private val announcements: List<AnnouncementItem>) :
    RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder>() {

    class AnnouncementViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.textViewAnnouncementTitle)
        val dateTextView: TextView = view.findViewById(R.id.textViewAnnouncementDate)
        val bodyTextView: TextView = view.findViewById(R.id.textViewAnnouncementBody)
        val iconImageView: ImageView = view.findViewById(R.id.imageViewAnnouncementIcon)
        // val card: MaterialCardView = view.findViewById(R.id.card) // For click listeners
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_announcement, parent, false)
        return AnnouncementViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnnouncementViewHolder, position: Int) {
        val announcement = announcements[position]
        holder.titleTextView.text = announcement.title
        holder.dateTextView.text = announcement.date
        holder.bodyTextView.text = announcement.body
        // Optional: Set custom icon using holder.iconImageView.setImageResource(announcement.iconResId)
        // Add click listener if needed: holder.itemView.setOnClickListener { ... }
    }

    override fun getItemCount() = announcements.size
}
