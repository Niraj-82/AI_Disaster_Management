package com.example.aidm

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var buttonLogin: MaterialButton
    private lateinit var buttonSignInWithGoogle: MaterialButton
    private lateinit var textViewForgotPassword: TextView
    private lateinit var textViewSignUp: TextView

    private lateinit var googleSignInClient: GoogleSignInClient
    // You can use LoginViewModel if you plan to move logic there
    // private val loginViewModel: LoginViewModel by viewModels()

    companion object {
        private const val TAG = "LoginActivity"
    }

    // ActivityResultLauncher for Google Sign-In
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Google Sign In was successful, authenticate with Firebase or your backend
                val account = task.getResult(ApiException::class.java)
                Log.d(TAG, "Google Sign In successful: ${account?.displayName} (${account?.email})")
                Toast.makeText(this, "Google Sign In successful: ${account?.displayName}", Toast.LENGTH_SHORT).show()
                // TODO: Authenticate with your backend (e.g., send ID token to server)
                // navigateToHomeScreen() // Or your main app screen
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google Sign In failed", e)
                Toast.makeText(this, "Google Sign In failed: ${e.statusCode}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.w(TAG, "Google Sign In flow cancelled or failed before result: ${result.resultCode}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonSignInWithGoogle = findViewById(R.id.buttonSignInWithGoogle)
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword)
        textViewSignUp = findViewById(R.id.textViewSignUp)

        // Configure Google Sign-In
        // IMPORTANT: Replace "YOUR_WEB_CLIENT_ID" with your actual Web Client ID from Google Cloud Console
        // It's recommended to store this in strings.xml
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Create this string resource
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Standard Login Button
        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // TODO: Implement your standard email/password login logic (e.g., call ViewModel)
            Toast.makeText(this, "Login Clicked (Email: $email)", Toast.LENGTH_SHORT).show()
            // Example: loginViewModel.loginUser(email, password)
            // navigateToHomeScreen()
        }

        // Google Sign-In Button
        buttonSignInWithGoogle.setOnClickListener {
            signInWithGoogle()
        }

        // Forgot Password
        textViewForgotPassword.setOnClickListener {
            // TODO: Navigate to a Forgot Password screen or show a dialog
            Toast.makeText(this, "Forgot Password Clicked", Toast.LENGTH_SHORT).show()
        }

        // Sign Up
        textViewSignUp.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun navigateToHomeScreen() {
        val intent = Intent(this, HomeActivity::class.java) // Assuming HomeActivity is your main screen
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        // val account = GoogleSignIn.getLastSignedInAccount(this)
        // if (account != null) {
        //    Toast.makeText(this, "Already signed in as ${account.displayName}", Toast.LENGTH_SHORT).show()
        //    navigateToHomeScreen()
        // }
    }
}
