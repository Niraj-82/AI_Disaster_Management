package com.example.resqai.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.resqai.repository.UserRepository

class LoginViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private val _loginStatus = MutableLiveData<LoginState>()
    val loginStatus: LiveData<LoginState> = _loginStatus

    fun loginUser(email: String, password: String) {
        _loginStatus.value = LoginState.Loading
        userRepository.loginUser(email, password) {
            _loginStatus.postValue(it)
        }
    }

    sealed class LoginState {
        object Loading : LoginState()
        data class Success(val userId: String, val role: String) : LoginState()
        data class Error(val message: String) : LoginState()
        object Idle : LoginState()
    }
}
