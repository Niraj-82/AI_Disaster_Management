package com.example.resqai

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.resqai.model.Incident
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class IncidentAdapter(private var incidents: List<Incident>) : RecyclerView.Adapter<IncidentAdapter.IncidentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncidentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_incident, parent, false)
        return IncidentViewHolder(view)
    }

    override fun onBindViewHolder(holder: IncidentViewHolder, position: Int) {
        val incident = incidents[position]
        holder.bind(incident)
    }

    override fun getItemCount() = incidents.size

    fun updateData(newIncidents: List<Incident>) {
        this.incidents = newIncidents
        notifyDataSetChanged()
    }

    class IncidentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val typeTextView: TextView = itemView.findViewById(R.id.textViewIncidentType)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.textViewIncidentDescription)
        private val timestampTextView: TextView = itemView.findViewById(R.id.textViewIncidentTimestamp)
        private val locationTextView: TextView = itemView.findViewById(R.id.textViewIncidentLocation)
        private val incidentImageView: ImageView = itemView.findViewById(R.id.imageViewIncident)

        fun bind(incident: Incident) {
            typeTextView.text = incident.type
            descriptionTextView.text = incident.description
            timestampTextView.text = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(incident.timestamp))

            val location = when {
                incident.latitude != null && incident.longitude != null -> "Lat: ${incident.latitude}, Lon: ${incident.longitude}"
                !incident.locationString.isNullOrBlank() -> incident.locationString
                else -> "Location not provided"
            }
            locationTextView.text = location

            if (incident.imageUrl != null) {
                incidentImageView.visibility = View.VISIBLE
                Glide.with(itemView.context)
                    .load(incident.imageUrl)
                    .into(incidentImageView)
            } else {
                incidentImageView.visibility = View.GONE
            }
        }
    }
}
