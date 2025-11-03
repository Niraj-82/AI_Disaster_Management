package com.example.resqai

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resqai.model.Shelter
import com.google.firebase.firestore.FirebaseFirestore

class FindShelterActivity : AppCompatActivity() {

    private lateinit var shelterRecyclerView: RecyclerView
    private lateinit var shelterAdapter: ShelterAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_shelter)

        shelterRecyclerView = findViewById(R.id.shelter_recycler_view)
        shelterRecyclerView.layoutManager = LinearLayoutManager(this)

        fetchShelters()
    }

    private fun fetchShelters() {
        db.collection("shelters").get()
            .addOnSuccessListener { result ->
                val shelters = result.toObjects(Shelter::class.java)
                shelterAdapter = ShelterAdapter(shelters, null, false) { shelter ->
                    val intent = Intent(this, MapActivity::class.java)
                    intent.putExtra("shelter", shelter)
                    startActivity(intent)
                }
                shelterRecyclerView.adapter = shelterAdapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting shelters: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}