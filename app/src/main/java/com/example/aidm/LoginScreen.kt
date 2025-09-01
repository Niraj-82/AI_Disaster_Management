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
import com.example.aidm.AIDMTheme // Assuming your theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current // Used for Toasts or other context needs
    val scope = rememberCoroutineScope() // Scope for launching coroutines tied to this Composable

    // --- Simulated Login Logic ---
    // In a real app, this would be in a ViewModel and interact with a Repository/Backend.
    suspend fun performLogin(emailInput: String, passwordInput: String): Boolean {
        isLoading = true
        loginError = null // Clear previous errors
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
            isLoading = false // Ensure isLoading is set to false in all execution paths
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
                .padding(32.dp), // Main padding for the content
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
            Spacer(modifier = Modifier.height(32.dp)) // Space before form fields

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    loginError = null // Clear error when user types
                },
                label = { Text("Email Address") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next // Moves focus to next field
                ),
                modifier = Modifier.fillMaxWidth(),
                isError = loginError != null && email.isBlank() // Basic error state, customize as needed
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    loginError = null // Clear error when user types
                },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(), // Hides password characters
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done // Typically submits the form or closes keyboard
                ),
                modifier = Modifier.fillMaxWidth(),
                isError = loginError != null && password.isBlank() // Basic error state
            )

            // Display login error message if it exists
            loginError?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth() // Allow error text to wrap
                )
            }

            Spacer(modifier = Modifier.height(24.dp)) // Space before login button

            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        // Launch the suspend function in the Composable's scope
                        scope.launch {
                            if (performLogin(email, password)) {
                                onLoginSuccess() // Call the callback on successful login
                            }
                        }
                    } else {
                        loginError = "Email and password cannot be empty."
                        // Optionally show a toast for immediate feedback if fields are empty
                        // Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading // Disable button while loading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp), // Make progress indicator same height as text
                        color = MaterialTheme.colorScheme.onPrimary // Color for progress indicator on button
                    )
                } else {
                    Text("Login")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { /* TODO: Implement Forgot Password functionality */ }) {
                Text("Forgot Password?")
            }

            TextButton(onClick = { /* TODO: Implement Navigate to Sign Up screen */ }) {
                Text("Don't have an account? Sign Up")
            }
        }
    }
}

// Minimal Preview for the LoginScreen composable itself
@Preview(showBackground = true, name = "Login Screen Preview")
@Composable
fun LoginScreenStandalonePreview() {
    // It's good practice for previews to also be wrapped in your app's theme
    // if they rely on MaterialTheme attributes.
    AIDMTheme { // Replace with your actual theme if different
        LoginScreen(onLoginSuccess = {
            // This lambda does nothing in the preview
            println("Preview: Login Success triggered")
        })
    }
}
