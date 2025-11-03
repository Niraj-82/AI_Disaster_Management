package com.example.resqai

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.example.resqai.adapter.EmergencyContactAdapter
import com.example.resqai.db.DatabaseHelper
import com.example.resqai.model.EmergencyContact

class EmergencyContactsActivity : AppCompatActivity() {

    private lateinit var contactsRecyclerView: RecyclerView
    private lateinit var contactNameEditText: EditText
    private lateinit var contactPhoneEditText: EditText
    private lateinit var addContactButton: Button
    private lateinit var emptyView: TextView

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var adapter: EmergencyContactAdapter
    private val contactsList = mutableListOf<EmergencyContact>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_contacts)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        databaseHelper = DatabaseHelper(this)

        contactsRecyclerView = findViewById(R.id.contacts_recycler_view)
        contactNameEditText = findViewById(R.id.contact_name_edit_text)
        contactPhoneEditText = findViewById(R.id.contact_phone_edit_text)
        addContactButton = findViewById(R.id.add_contact_button)
        emptyView = findViewById(R.id.empty_view)

        setupRecyclerView()
        loadContacts()

        addContactButton.setOnClickListener {
            addContact()
        }
    }

    private fun setupRecyclerView() {
        adapter = EmergencyContactAdapter(contactsList) { contact ->
            deleteContact(contact)
        }
        contactsRecyclerView.adapter = adapter
    }

    private fun loadContacts() {
        contactsList.clear()
        contactsList.addAll(databaseHelper.getAllEmergencyContacts())
        adapter.notifyDataSetChanged()
        updateEmptyView()
    }

    private fun addContact() {
        val name = contactNameEditText.text.toString().trim()
        val phone = contactPhoneEditText.text.toString().trim()

        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Name and phone number cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        if (contactsList.size >= 5) {
            Toast.makeText(this, "You can only add up to 5 emergency contacts", Toast.LENGTH_SHORT).show()
            return
        }

        val newContact = EmergencyContact(name = name, phoneNumber = phone)
        val id = databaseHelper.addEmergencyContact(newContact)
        if (id != -1L) {
            val contactWithId = newContact.copy(id = id)
            val newPosition = contactsList.size
            contactsList.add(contactWithId)
            adapter.notifyItemInserted(newPosition)
            contactNameEditText.text.clear()
            contactPhoneEditText.text.clear()
            updateEmptyView()
        } else {
            Toast.makeText(this, "Error adding contact", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteContact(contact: EmergencyContact) {
        val position = contactsList.indexOf(contact)
        if (position != -1) {
            databaseHelper.deleteEmergencyContact(contact.id)
            contactsList.removeAt(position)
            adapter.notifyItemRemoved(position)
            updateEmptyView()
        }
    }

    private fun updateEmptyView() {
        if (contactsList.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            contactsRecyclerView.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            contactsRecyclerView.visibility = View.VISIBLE
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
