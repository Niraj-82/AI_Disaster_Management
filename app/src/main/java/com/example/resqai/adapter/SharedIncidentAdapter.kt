package com.example.resqai.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.resqai.R
import com.example.resqai.model.Incident
import java.text.SimpleDateFormat
import java.util.*

class SharedIncidentAdapter(private var incidents: List<Incident>) :
    RecyclerView.Adapter<SharedIncidentAdapter.IncidentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncidentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_incident, parent, false)
        return IncidentViewHolder(view)
    }

    override fun onBindViewHolder(holder: IncidentViewHolder, position: Int) {
        val incident = incidents[position]
        holder.bind(incident)
    }

    override fun getItemCount(): Int = incidents.size

    fun updateIncidents(newIncidents: List<Incident>) {
        incidents = newIncidents
        notifyDataSetChanged() // Consider using DiffUtil for better performance
    }

    class IncidentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val incidentTypeTextView: TextView = itemView.findViewById(R.id.tv_incident_type)
        private val incidentTimeTextView: TextView = itemView.findViewById(R.id.tv_incident_time)
        private val incidentDescriptionTextView: TextView = itemView.findViewById(R.id.tv_incident_description)
        private val incidentPhotoImageView: ImageView = itemView.findViewById(R.id.iv_incident_photo)
        private val incidentLocationTextView: TextView = itemView.findViewById(R.id.tv_incident_location)

        fun bind(incident: Incident) {
            incidentTypeTextView.text = incident.type
            incidentDescriptionTextView.text = incident.description

            val sdf = SimpleDateFormat("hh:mm a, MMM dd", Locale.getDefault())
            incidentTimeTextView.text = sdf.format(Date(incident.timestamp))

            // Use Glide to load the image from the URL
            if (incident.imageUrl != null) {
                incidentPhotoImageView.visibility = View.VISIBLE
                Glide.with(itemView.context)
                    .load(incident.imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.back) // Optional: a placeholder image
                    .error(R.drawable.back) // Optional: an error image
                    .into(incidentPhotoImageView)
            } else {
                incidentPhotoImageView.visibility = View.GONE
            }

            if (incident.latitude != null && incident.longitude != null) {
                incidentLocationTextView.text = String.format(itemView.context.getString(R.string.location_lat_lon_format),
                    incident.latitude, incident.longitude)
                incidentLocationTextView.visibility = View.VISIBLE
            } else {
                incidentLocationTextView.visibility = View.GONE
            }
        }
    }
}