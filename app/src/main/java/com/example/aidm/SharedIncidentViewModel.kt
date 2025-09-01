package com.example.aidm

import androidx.lifecycle.ViewModel

/**
 * A shared ViewModel to hold a single instance of the repository.
 * This allows different screens (like AnnouncementsScreen and ReportIncidentScreen)
 * to access and modify the same data, ensuring they stay in sync.
 */
class SharedIncidentViewModel : ViewModel() {

    // Create a single, private instance of the repository that will be shared.
    private val repository = FakeRepo()

    /**
     * Provides access to the shared repository instance.
     */
    fun getRepository(): FakeRepo {
        return repository
    }
}
