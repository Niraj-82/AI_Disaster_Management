package com.example.aidm

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class ShelterDetailActivity : AppCompatActivity() {

    private lateinit var shelterData: ShelterData

    companion object {
        const val EXTRA_SHELTER_DATA = "com.example.aidm.EXTRA_SHELTER_DATA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shelter_detail)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar_shelter_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        shelterData = intent.getParcelableExtra(EXTRA_SHELTER_DATA)
            ?: run {
                Toast.makeText(this, "Shelter data not found.", Toast.LENGTH_LONG).show()
                finish()
                return
            }

        populateUI()

        val buttonRouteToShelter: MaterialButton = findViewById(R.id.buttonRouteToShelter)
        buttonRouteToShelter.setOnClickListener {
            val intent = Intent(this, RouteMapActivity::class.java).apply {
                putExtra(RouteMapActivity.EXTRA_DESTINATION_SHELTER, shelterData)
            }
            startActivity(intent)
        }
    }

    private fun populateUI() {
        supportActionBar?.title = shelterData.name

        val textViewName: TextView = findViewById(R.id.textViewShelterDetailName)
        val textViewAddress: TextView = findViewById(R.id.textViewShelterDetailAddress)
        val textViewCapacity: TextView = findViewById(R.id.textViewShelterDetailCapacity) // For currentCapacity/maxCapacity
        val textViewContact: TextView = findViewById(R.id.textViewShelterDetailContact)
        val textViewServices: TextView = findViewById(R.id.textViewShelterDetailServices)
        val textViewSupplies: TextView = findViewById(R.id.textViewShelterDetailSupplies) // For availableSupplies

        textViewName.text = shelterData.name
        textViewAddress.text = shelterData.address
        textViewCapacity.text = "Capacity: ${shelterData.getCapacityString()}" // Uses your existing method
        textViewContact.text = shelterData.contactInfo ?: "Not available"
        textViewServices.text = shelterData.servicesAvailable?.joinToString(", ") ?: "Information not available"
        textViewSupplies.text = shelterData.availableSupplies?.joinToString(", ") ?: "Information not available"

        // Handle visibility if supplies info is not available (optional)
        val textViewSuppliesTitle: TextView = findViewById(R.id.textViewShelterDetailSuppliesTitle)
        if (shelterData.availableSupplies.isNullOrEmpty()) {
            textViewSuppliesTitle.visibility = TextView.GONE
            textViewSupplies.visibility = TextView.GONE
        } else {
            textViewSuppliesTitle.visibility = TextView.VISIBLE
            textViewSupplies.visibility = TextView.VISIBLE
            textViewSupplies.text = shelterData.availableSupplies.joinToString(", ")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
