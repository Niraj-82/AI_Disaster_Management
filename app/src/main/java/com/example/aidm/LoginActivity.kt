package com.example.aidm

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.error
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aidm.ui.theme.AIDMTheme // Assuming your theme is here
import kotlinx.coroutines.delay // For simulating login delay

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AIDMTheme { // Replace AIDMTheme with your actual theme
                LoginScreen(
                    onLoginSuccess = {
                        // Navigate to your main app screen (e.g., MainActivity)
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish() // Finish LoginActivity so user can't go back
                    }
                )
            }
        }
    }
}

@androidx.compose.runtime.Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var email by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    var password by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    var isLoading by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var loginError by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // Simulate login logic
    suspend fun performLogin(emailInput: String, passwordInput: String): Boolean {
        isLoading = true
        loginError = null
        delay(1500) // Simulate network delay
        // ** IMPORTANT: Replace this with your actual authentication logic **
        // Example: Call your ViewModel/Repository to authenticate with a backend
        return if (emailInput == "user@example.com" && passwordInput == "password123") {
            isLoading = false
            true
        } else {
            isLoading = false
            loginError = "Invalid email or password."
            false
        }
    }

    androidx.compose.material3.Surface(
        modifier = Modifier.fillMaxSize(),
        color = androidx.compose.material3.MaterialTheme.colorScheme.background
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            androidx.compose.material3.Text(
                text = "Welcome Back!",
                style = androidx.compose.material3.MaterialTheme.typography.headlineLarge,
                color = androidx.compose.material3.MaterialTheme.colorScheme.primary
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
            androidx.compose.material3.Text(
                text = "Login to continue",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(32.dp))

            androidx.compose.material3.OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { androidx.compose.material3.Text("Email Address") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                isError = loginError != null
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))

            androidx.compose.material3.OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { androidx.compose.material3.Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth(),
                isError = loginError != null
            )

            loginError?.let {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.material3.Text(
                    text = it,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                )
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(24.dp))

            androidx.compose.material3.Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        // Launch a coroutine to perform login (since it's a suspend function)
                        // In a real app, this would likely be handled in a ViewModel
                        // For simplicity, launching directly here.
                        kotlinx.coroutines.GlobalScope.launch { // Use a proper scope in real app
                            if (performLogin(email, password)) {
                                onLoginSuccess()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
                        loginError = "Email and password cannot be empty."
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    androidx.compose.material3.Text("Login")
                }
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))

            androidx.compose.material3.TextButton(onClick = { /* TODO: Handle forgot password */ }) {
                androidx.compose.material3.Text("Forgot Password?")
            }
            androidx.compose.material3.TextButton(onClick = { /* TODO: Navigate to Sign Up screen */ }) {
                androidx.compose.material3.Text("Don't have an account? Sign Up")
            }
        }
    }
}

@Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun LoginScreenPreview() {
    AIDMTheme {
        LoginScreen(onLoginSuccess = {})
    }
}
