package com.example.resqai.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.resqai.R
import com.example.resqai.model.IncidentReport
import java.text.SimpleDateFormat
import java.util.Locale

class IncidentAdapter(private val incidentList: List<IncidentReport>) :
    RecyclerView.Adapter<IncidentAdapter.IncidentViewHolder>() {

    class IncidentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val incidentTypeTextView: TextView = itemView.findViewById(R.id.incidentTypeTextView)
        val severityTextView: TextView = itemView.findViewById(R.id.severityTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
        val reporterTextView: TextView = itemView.findViewById(R.id.reporterTextView)
        val timestampTextView: TextView = itemView.findViewById(R.id.timestampTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncidentViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_incident, parent, false)
        return IncidentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: IncidentViewHolder, position: Int) {
        val currentItem = incidentList[position]

        holder.incidentTypeTextView.text = currentItem.incidentType
        holder.severityTextView.text = currentItem.severity
        holder.descriptionTextView.text = currentItem.description
        holder.locationTextView.text = currentItem.location
        holder.reporterTextView.text = "Reported by: ${currentItem.reporterName}"
        
        currentItem.timestamp?.let {
            val sdf = SimpleDateFormat("dd/MM/yy, hh:mm a", Locale.getDefault())
            holder.timestampTextView.text = sdf.format(it)
        } ?: run {
            holder.timestampTextView.text = "No date"
        }
    }

    override fun getItemCount() = incidentList.size
}