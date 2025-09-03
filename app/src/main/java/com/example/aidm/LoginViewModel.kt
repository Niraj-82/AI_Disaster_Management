package com.example.aidm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val loginError: String? = null
)

class LoginViewModel : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    private val _loginEvent = MutableSharedFlow<Unit>()
    val loginEvent = _loginEvent.asSharedFlow()

    fun onEmailChange(email: String) {
        uiState = uiState.copy(email = email, loginError = null)
    }

    fun onPasswordChange(password: String) {
        uiState = uiState.copy(password = password, loginError = null)
    }

    fun onLoginClick() {
        if (uiState.email.isNotBlank() && uiState.password.isNotBlank()) {
            viewModelScope.launch {
                performLogin()
            }
        } else {
            uiState = uiState.copy(loginError = "Email and password cannot be empty.")
        }
    }

    private suspend fun performLogin() {
        uiState = uiState.copy(isLoading = true, loginError = null)
        delay(1500) // Simulate network delay

        val success = uiState.email == "user@example.com" && uiState.password == "password123"

        if (success) {
            _loginEvent.emit(Unit)
        } else {
            uiState = uiState.copy(loginError = "Invalid email or password.")
        }
        uiState = uiState.copy(isLoading = false)
    }
}