package com.example.aidm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class FirstAidAdapter(private val topics: List<FirstAidTopic>) :
    RecyclerView.Adapter<FirstAidAdapter.FirstAidViewHolder>() {

    class FirstAidViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.textViewTitle)
        val descriptionTextView: TextView = view.findViewById(R.id.textViewDescription)
        val iconImageView: ImageView = view.findViewById(R.id.imageViewIcon)
        // val card: MaterialCardView = view.findViewById(R.id.card) // For click listeners
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FirstAidViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_first_aid, parent, false)
        return FirstAidViewHolder(view)
    }

    override fun onBindViewHolder(holder: FirstAidViewHolder, position: Int) {
        val topic = topics[position]
        holder.titleTextView.text = topic.title
        holder.descriptionTextView.text = topic.description
        // Here you could set a click listener on holder.card or holder.itemView
    }

    override fun getItemCount() = topics.size
}