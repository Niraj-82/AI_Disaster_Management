package com.example.aidm

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class FirstAidViewModel(private val repository: FakeRepo = FakeRepo()) : ViewModel() {

    private val _firstAidTopics = mutableStateOf<Map<String, List<String>>?>(null)
    val firstAidTopics: State<Map<String, List<String>>?> = _firstAidTopics

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    fun loadFirstAidTopics() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _firstAidTopics.value = repository.getFirstAidTopics()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load first aid topics: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
