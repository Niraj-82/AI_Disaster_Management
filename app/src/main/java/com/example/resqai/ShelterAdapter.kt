package com.example.resqai

import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.resqai.model.Shelter
import java.util.Locale

class ShelterAdapter(
    private val shelters: List<Shelter>,
    var userLocation: Location?,
    var isAdmin: Boolean,
    private val onItemClickListener: (Shelter) -> Unit
) : RecyclerView.Adapter<ShelterAdapter.ShelterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShelterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shelter, parent, false)
        return ShelterViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShelterViewHolder, position: Int) {
        val shelter = shelters[position]
        holder.bind(shelter)
    }

    override fun getItemCount() = shelters.size

    inner class ShelterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.text_view_shelter_name)
        private val distanceTextView: TextView = itemView.findViewById(R.id.text_view_shelter_distance)
        private val capacityTextView: TextView = itemView.findViewById(R.id.text_view_shelter_capacity)
        private val medicalStatusImageView: ImageView = itemView.findViewById(R.id.image_view_medical_status)

        fun bind(shelter: Shelter) {
            nameTextView.text = shelter.name

            if (userLocation != null && shelter.latitude != null && shelter.longitude != null) {
                val shelterLocation = Location("").apply {
                    latitude = shelter.latitude!!
                    longitude = shelter.longitude!!
                }
                val distance = userLocation!!.distanceTo(shelterLocation) / 1000 // in kilometers
                distanceTextView.text = String.format(Locale.getDefault(), "%.2f km away", distance)
            } else {
                distanceTextView.text = "Distance unknown"
            }

            capacityTextView.text = "Capacity: ${shelter.capacity}"

            medicalStatusImageView.visibility = if (shelter.medicalAvailable == true) View.VISIBLE else View.GONE

            if (isAdmin) {
                itemView.setOnClickListener {
                    onItemClickListener(shelter)
                }
            }
        }
    }
}
