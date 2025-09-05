package com.example.aidm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

// Listener for item clicks - implement this in your Activity/Fragment
interface OnShelterClickListener {
    fun onShelterClick(shelter: ShelterData)
}

class ShelterAdapter(private val clickListener: OnShelterClickListener? = null) :
    ListAdapter<ShelterData, ShelterAdapter.ShelterViewHolder>(ShelterDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShelterViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shelter, parent, false)
        return ShelterViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ShelterViewHolder, position: Int) {
        val currentShelter = getItem(position)
        holder.bind(currentShelter, clickListener)
    }

    class ShelterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val shelterNameTextView: TextView = itemView.findViewById(R.id.textViewShelterName)
        private val shelterAddressTextView: TextView = itemView.findViewById(R.id.textViewShelterAddress)
        private val shelterCapacityTextView: TextView = itemView.findViewById(R.id.textViewShelterCapacity)
        private val shelterIconImageView: ImageView = itemView.findViewById(R.id.imageViewShelterIcon) // Make sure this ID exists in item_shelter.xml

        fun bind(shelter: ShelterData, listener: OnShelterClickListener?) {
            shelterNameTextView.text = shelter.name
            shelterAddressTextView.text = shelter.address
            shelterCapacityTextView.text = "Capacity: ${shelter.getCapacityString()}" // Using the helper

            // TODO: Load image for shelterIconImageView using Glide/Coil if shelter.iconUrl is available
            // For now, it uses the placeholder from XML
            // if (!shelter.iconUrl.isNullOrEmpty()) {
            //    shelterIconImageView.load(shelter.iconUrl) { placeholder(R.drawable.ic_shelter_placeholder); error(R.drawable.ic_shelter_placeholder) }
            // } else {
            //    shelterIconImageView.setImageResource(R.drawable.ic_shelter_placeholder)
            // }


            itemView.setOnClickListener {
                listener?.onShelterClick(shelter)
            }
        }
    }

    class ShelterDiffCallback : DiffUtil.ItemCallback<ShelterData>() {
        override fun areItemsTheSame(oldItem: ShelterData, newItem: ShelterData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShelterData, newItem: ShelterData): Boolean {
            return oldItem == newItem
        }
    }
}
