package com.example.resqai

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resqai.adapter.ShelterAdapter
import com.example.resqai.model.Shelter
import com.google.firebase.firestore.FirebaseFirestore

class FindShelterActivity : AppCompatActivity() {

    private lateinit var shelterRecyclerView: RecyclerView
    private lateinit var shelterAdapter: ShelterAdapter
    private lateinit var deleteAllButton: Button
    private val db = FirebaseFirestore.getInstance()
    private var shelters = mutableListOf<Shelter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_shelter)

        shelterRecyclerView = findViewById(R.id.shelter_recycler_view)
        deleteAllButton = findViewById(R.id.delete_all_shelters_button)
        shelterRecyclerView.layoutManager = LinearLayoutManager(this)

        setupDeleteAllButton()
        fetchShelters()
    }

    private fun setupDeleteAllButton() {
        deleteAllButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete All Shelters")
            .setMessage("Are you sure you want to delete all available shelters? This action cannot be undone.")
            .setPositiveButton("Yes, Delete") { _, _ ->
                deleteAllShelters()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteAllShelters() {
        val sheltersCollection = db.collection("shelters")
        sheltersCollection.get().addOnSuccessListener { querySnapshot ->
            val batch = db.batch()
            for (document in querySnapshot.documents) {
                batch.delete(document.reference)
            }
            batch.commit().addOnSuccessListener {
                Toast.makeText(this, "All shelters have been deleted.", Toast.LENGTH_SHORT).show()
                shelters.clear()
                shelterAdapter.notifyDataSetChanged()
            }.addOnFailureListener {
                Toast.makeText(this, "Error deleting shelters.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun fetchShelters() {
        db.collection("shelters").get()
            .addOnSuccessListener { result ->
                shelters = result.toObjects(Shelter::class.java)
                shelterAdapter = ShelterAdapter(shelters) { shelter ->
                    val lat = shelter.latitude
                    val lon = shelter.longitude

                    if (lat != null && lon != null) {
                        val intent = Intent(this@FindShelterActivity, IncidentsMapActivity::class.java).apply {
                            putExtra("latitude", lat)
                            putExtra("longitude", lon)
                        }
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@FindShelterActivity, "Shelter location not available.", Toast.LENGTH_SHORT).show()
                    }
                }
                shelterRecyclerView.adapter = shelterAdapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting shelters: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
