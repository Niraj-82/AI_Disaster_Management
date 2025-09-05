package com.example.aidm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ShelterViewModel : ViewModel() {

    private val _shelters = MutableLiveData<List<ShelterData>>()
    val shelters: LiveData<List<ShelterData>> = _shelters

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        loadShelters()
    }

    fun loadShelters() {
        _isLoading.value = true
        _errorMessage.value = null

        // Simulate network/database delay
        CoroutineScope(Dispatchers.IO).launch {
            try {
                delay(2000) // Simulate a 2-second delay

                // Placeholder data - replace with actual data fetching logic
                val placeholderShelters = listOf(
                    ShelterData("1", "Community Center Alpha", "123 Main St, Anytown", 75, 100, 34.0522, -118.2437, "555-1234", listOf("Food", "Water", "Beds")),
                    ShelterData("2", "Redwood High School Gym", "456 Oak Ave, Anytown", 150, 200, 34.0588, -118.2500, "555-5678", listOf("Food", "Water", "Medical", "Pet Friendly")),
                    ShelterData("3", "Southside Church Hall", "789 Pine Ln, Anytown", 40, 50, 34.0410, -118.2323, "555-9012", listOf("Water", "Beds")),
                    ShelterData("4", "North Park Arena", "101 Park Dr, Anytown", 200, 300, 34.0650, -118.2590, "555-3456", listOf("Food", "Water", "Beds", "Showers"))
                )
                // Uncomment to simulate empty list
                // val placeholderShelters = emptyList<ShelterData>()

                // Uncomment to simulate error
                // throw Exception("Failed to load shelters")

                _shelters.postValue(placeholderShelters)
            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "An unknown error occurred")
                _shelters.postValue(emptyList()) // Clear list on error
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    // Call this if you implement pull-to-refresh
    fun refreshShelters() {
        loadShelters()
    }
}
