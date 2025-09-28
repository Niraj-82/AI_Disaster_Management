package com.example.resqai.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.resqai.repository.UserRepository

class SignupViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private val _signupStatus = MutableLiveData<SignupState>()
    val signupStatus: LiveData<SignupState> = _signupStatus

    fun signupUser(email: String, password: String, role: String) {
        _signupStatus.value = SignupState.Loading
        userRepository.signupUser(email, password, role) {
            _signupStatus.postValue(it)
        }
    }

    sealed class SignupState {
        object Loading : SignupState()
        data class Success(val userId: String, val role: String) : SignupState()
        data class Error(val message: String) : SignupState()
        object Idle : SignupState()
    }
}
