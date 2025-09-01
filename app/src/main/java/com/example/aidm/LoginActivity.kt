package com.example.aidm

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aidm.AIDMTheme // Ensure this is your correct theme import
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AIDMTheme {
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

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope() // Use rememberCoroutineScope for Composable-tied coroutines

    // --- Simulated Login Logic ---
    // In a real app, this would be in a ViewModel and interact with a Repository/Backend.
    suspend fun performLogin(emailInput: String, passwordInput: String): Boolean {
        isLoading = true
        loginError = null
        delay(1500) // Simulate network delay

        // ** IMPORTANT: Replace this with your actual authentication logic **
        return if (emailInput == "user@example.com" && passwordInput == "password123") {
            // Simulate successful login
            true
        } else {
            // Simulate failed login
            loginError = "Invalid email or password."
            false
        }.also {
            isLoading = false // Ensure isLoading is set to false in all paths
        }
    }
    // --- End of Simulated Login Logic ---

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome Back!",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Login to continue",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; loginError = null }, // Clear error on change
                label = { Text("Email Address") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                isError = loginError != null && email.isBlank() // Example: error if related field is blank
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; loginError = null }, // Clear error on change
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth(),
                isError = loginError != null && password.isBlank() // Example: error if related field is blank
            )

            loginError?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth() // Ensure error text can wrap
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        scope.launch { // Use the Composable's scope
                            if (performLogin(email, password)) {
                                onLoginSuccess()
                            }
                        }
                    } else {
                        loginError = "Email and password cannot be empty."
                        // Optionally show a toast for immediate feedback if fields are empty
                        // Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary // Or primary if it contrasts better
                    )
                } else {
                    Text("Login")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { /* TODO: Handle forgot password */ }) {
                Text("Forgot Password?")
            }
            TextButton(onClick = { /* TODO: Navigate to Sign Up screen */ }) {
                Text("Don't have an account? Sign Up")
            }
        }
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=360dp,height=640dp,dpi=480" // Corrected: units added to width/height, 'unit' param removed
)
@Composable
fun LoginScreenPreview() {
    AIDMTheme {
        LoginScreen(onLoginSuccess = {})
    }
}
// Ensure MainActivity exists (even if it's just a placeholder for now)
// class MainActivity : ComponentActivity() { /* ... */ }
