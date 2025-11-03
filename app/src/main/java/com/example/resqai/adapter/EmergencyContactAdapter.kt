package com.example.resqai.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.resqai.R
import com.example.resqai.model.EmergencyContact

class EmergencyContactAdapter(
    private val contacts: MutableList<EmergencyContact>,
    private val onDeleteClicked: (EmergencyContact) -> Unit
) : RecyclerView.Adapter<EmergencyContactAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_emergency_contact, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = contacts[position]
        holder.bind(contact)
        holder.deleteButton.setOnClickListener { onDeleteClicked(contact) }
    }

    override fun getItemCount() = contacts.size

    fun addContact(contact: EmergencyContact) {
        contacts.add(contact)
        notifyItemInserted(contacts.size - 1)
    }

    fun removeContact(contact: EmergencyContact) {
        val position = contacts.indexOf(contact)
        if (position > -1) {
            contacts.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameTextView: TextView = view.findViewById(R.id.contact_name_text_view)
        private val phoneTextView: TextView = view.findViewById(R.id.contact_phone_text_view)
        val deleteButton: ImageButton = view.findViewById(R.id.delete_contact_button)

        fun bind(contact: EmergencyContact) {
            nameTextView.text = contact.name
            phoneTextView.text = contact.phoneNumber
        }
    }
}
