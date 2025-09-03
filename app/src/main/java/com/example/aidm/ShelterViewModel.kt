package com.example.aidm

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ShelterViewModel(private val repository: FakeRepo = FakeRepo()) : ViewModel() {

    private val _shelter = mutableStateOf<Shelter?>(null)
    val shelter: State<Shelter?> = _shelter

    fun loadShelter(id: String) {
        viewModelScope.launch {
            _shelter.value = repository.getShelter(id)
        }
    }
}
