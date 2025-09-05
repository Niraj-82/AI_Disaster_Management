package com.example.aidm

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException

class ShelterListActivity : AppCompatActivity(), ShelterAdapter.OnShelterClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var shelterAdapter: ShelterAdapter
    private var shelterList: MutableList<ShelterData> = mutableListOf()
    private lateinit var progressBar: ProgressBar

    private val json = Json { encodeDefaults = true; ignoreUnknownKeys = true; prettyPrint = true }
    private val cacheFileName = "shelter_cache.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shelter_list)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar_shelter_list)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Available Shelters"

        recyclerView = findViewById(R.id.recyclerViewShelters)
        progressBar = findViewById(R.id.progressBarShelters)

        recyclerView.layoutManager = LinearLayoutManager(this)
        shelterAdapter = ShelterAdapter(shelterList, this)
        recyclerView.adapter = shelterAdapter

        loadShelters()
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    private fun loadShelters() {
        progressBar.visibility = View.VISIBLE
        if (isNetworkAvailable()) {
            Log.d("ShelterListActivity", "Network available. Fetching fresh shelter data.")
            // Simulate network call
            // In a real app, replace this with your actual network call (e.g., Retrofit)
            Thread {
                try {
                    Thread.sleep(1000) // Simulate network delay
                    val freshShelters = getMockShelterData() // Replace with actual data fetching
                    runOnUiThread {
                        updateShelterList(freshShelters)
                        saveSheltersToCache(freshShelters)
                        progressBar.visibility = View.GONE
                    }
                } catch (e: Exception) {
                    Log.e("ShelterListActivity", "Error fetching fresh data", e)
                    runOnUiThread {
                        Toast.makeText(this, "Error fetching data: ${e.message}", Toast.LENGTH_SHORT).show()
                        loadSheltersFromCache() // Try cache as fallback
                    }
                }
            }.start()
        } else {
            Log.d("ShelterListActivity", "Network unavailable. Loading shelters from cache.")
            Toast.makeText(this, "Offline mode: Displaying cached shelters.", Toast.LENGTH_LONG).show()
            loadSheltersFromCache()
        }
    }

    private fun getMockShelterData(): List<ShelterData> {
        // IMPORTANT: Update this mock data with currentCapacity, maxCapacity, and availableSupplies
        return listOf(
            ShelterData(
                id = "1",
                name = "Northside Community Hall",
                address = "123 North Main St, Anytown, USA",
                currentCapacity = 30,    // Example
                maxCapacity = 75,       // Example
                latitude = 34.052235,
                longitude = -118.243683,
                contactInfo = "555-0101",
                servicesAvailable = listOf("Water", "First Aid", "Information"),
                availableSupplies = listOf("Bottled Water", "Band-Aids", "Face Masks") // Example
            ),
            ShelterData(
                id = "2",
                name = "Community Rec Center",
                address = "456 Park Ave, Townsville",
                currentCapacity = 45,    // Example
                maxCapacity = 100,     // Example
                latitude = 34.052238,
                longitude = -118.243689,
                contactInfo = "555-0202",
                servicesAvailable = listOf("Beds", "Hot Meals", "Showers"),
                availableSupplies = listOf("Blankets", "Water Bottles", "First Aid Kits", "Canned Food") // Example
            ),
            ShelterData(
                id = "3",
                name = "South End School Gym",
                address = "789 South Street, Cityville",
                currentCapacity = 60,    // Example
                maxCapacity = 120,     // Example
                latitude = 34.052240,
                longitude = -118.243699,
                contactInfo = "555-0303",
                servicesAvailable = listOf("Pets Allowed (crated)", "Charging Stations"),
                availableSupplies = listOf("Pet food (limited)", "Sanitizer") // Example
            )
        )
    }

    private fun saveSheltersToCache(shelters: List<ShelterData>) {
        try {
            val file = File(filesDir, cacheFileName)
            val jsonString = json.encodeToString(shelters)
            file.writeText(jsonString)
            Log.d("ShelterListActivity", "Shelters saved to cache: ${file.absolutePath}")
        } catch (e: IOException) {
            Log.e("ShelterListActivity", "Error saving shelters to cache", e)
        } catch (e: Exception) {
            Log.e("ShelterListActivity", "Serialization error while saving cache", e)
        }
    }

    private fun loadSheltersFromCache() {
        progressBar.visibility = View.VISIBLE
        try {
            val file = File(filesDir, cacheFileName)
            if (file.exists()) {
                val jsonString = file.readText()
                val cachedShelters = json.decodeFromString<List<ShelterData>>(jsonString)
                updateShelterList(cachedShelters)
                Log.d("ShelterListActivity", "Shelters loaded from cache.")
            } else {
                Log.d("ShelterListActivity", "Cache file not found. No shelters to display offline.")
                Toast.makeText(this, "No cached data available.", Toast.LENGTH_LONG).show()
                updateShelterList(emptyList()) // Show empty list
            }
        } catch (e: IOException) {
            Log.e("ShelterListActivity", "Error loading shelters from cache", e)
            Toast.makeText(this, "Error loading cached data.", Toast.LENGTH_LONG).show()
            updateShelterList(emptyList())
        } catch (e: Exception) {
            Log.e("ShelterListActivity", "Serialization error while loading cache", e)
            Toast.makeText(this, "Error reading cached data format.", Toast.LENGTH_LONG).show()
            updateShelterList(emptyList())
        }
        progressBar.visibility = View.GONE
    }

    private fun updateShelterList(newShelters: List<ShelterData>) {
        shelterList.clear()
        shelterList.addAll(newShelters)
        shelterAdapter.notifyDataSetChanged() // Consider DiffUtil for better performance
        if (newShelters.isEmpty()) {
            // Optionally show a "No shelters found" message in the UI
            // Toast.makeText(this, "No shelters to display.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onShelterClick(shelter: ShelterData) {
        val intent = Intent(this, ShelterDetailActivity::class.java).apply {
            putExtra(ShelterDetailActivity.EXTRA_SHELTER_DATA, shelter)
        }
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
