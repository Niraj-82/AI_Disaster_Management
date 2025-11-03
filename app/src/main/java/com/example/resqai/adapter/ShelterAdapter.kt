package com.example.resqai.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.resqai.R
import com.example.resqai.model.Shelter

class ShelterAdapter(private val shelters: List<Shelter>, private val listener: (Shelter) -> Unit) :
    RecyclerView.Adapter<ShelterAdapter.ShelterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShelterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shelter, parent, false)
        return ShelterViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShelterViewHolder, position: Int) {
        val shelter = shelters[position]
        holder.bind(shelter)
        holder.itemView.setOnClickListener { listener(shelter) }
    }

    override fun getItemCount() = shelters.size

    class ShelterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.text_view_shelter_name)

        fun bind(shelter: Shelter) {
            nameTextView.text = shelter.name
        }
    }
}