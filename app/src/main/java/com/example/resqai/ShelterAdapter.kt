package com.example.resqai

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.resqai.model.Shelter

class ShelterAdapter(private val shelters: List<Shelter>) : RecyclerView.Adapter<ShelterAdapter.ShelterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShelterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_shelter, parent, false)
        return ShelterViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShelterViewHolder, position: Int) {
        val shelter = shelters[position]
        holder.bind(shelter)
    }

    override fun getItemCount(): Int = shelters.size

    class ShelterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.tv_shelter_name)
        private val addressTextView: TextView = itemView.findViewById(R.id.tv_shelter_address)
        private val capacityTextView: TextView = itemView.findViewById(R.id.tv_shelter_capacity)
        private val suppliesTextView: TextView = itemView.findViewById(R.id.tv_shelter_supplies)

        fun bind(shelter: Shelter) {
            nameTextView.text = shelter.name
            addressTextView.text = shelter.address
            capacityTextView.text = itemView.context.getString(R.string.shelter_capacity_format, shelter.currentOccupancy, shelter.capacity)
            suppliesTextView.text = itemView.context.getString(R.string.shelter_supplies_format, shelter.supplies.joinToString(", "))
        }
    }
}