package com.example.aidm

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SignupActivity : AppCompatActivity() {

    private lateinit var editTextFullNameSignup: TextInputEditText
    private lateinit var editTextEmailSignup: TextInputEditText
    private lateinit var editTextPasswordSignup: TextInputEditText
    private lateinit var editTextConfirmPasswordSignup: TextInputEditText
    private lateinit var textFieldFullNameSignup: TextInputLayout
    private lateinit var textFieldEmailSignup: TextInputLayout
    private lateinit var textFieldPasswordSignup: TextInputLayout
    private lateinit var textFieldConfirmPasswordSignup: TextInputLayout
    private lateinit var buttonSignup: MaterialButton
    private lateinit var buttonSignUpWithGoogle: MaterialButton
    private lateinit var textViewLoginLink: TextView

    private lateinit var googleSignInClient: GoogleSignInClient
    // private val signupViewModel: SignupViewModel by viewModels() // If you have a ViewModel

    companion object {
        private const val TAG = "SignupActivity"
    }

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d(TAG, "Google Sign Up successful: ${account?.displayName} (${account?.email})")
                Toast.makeText(this, "Google Sign Up successful: ${account?.displayName}", Toast.LENGTH_SHORT).show()
                // TODO: Authenticate with your backend (e.g., send ID token to server and create user)
                // If the user's name is available from Google, you can pre-fill or use it.
                // val fullName = account?.displayName
                // val email = account?.email
                // signupViewModel.registerGoogleUser(account.idToken, fullName, email)
                navigateToHomeScreen()
            } catch (e: ApiException) {
                Log.w(TAG, "Google Sign Up failed", e)
                Toast.makeText(this, "Google Sign Up failed: ${e.statusCode}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        editTextFullNameSignup = findViewById(R.id.editTextFullNameSignup)
        editTextEmailSignup = findViewById(R.id.editTextEmailSignup)
        editTextPasswordSignup = findViewById(R.id.editTextPasswordSignup)
        editTextConfirmPasswordSignup = findViewById(R.id.editTextConfirmPasswordSignup)
        textFieldFullNameSignup = findViewById(R.id.textFieldFullNameSignup)
        textFieldEmailSignup = findViewById(R.id.textFieldEmailSignup)
        textFieldPasswordSignup = findViewById(R.id.textFieldPasswordSignup)
        textFieldConfirmPasswordSignup = findViewById(R.id.textFieldConfirmPasswordSignup)
        buttonSignup = findViewById(R.id.buttonSignup)
        buttonSignUpWithGoogle = findViewById(R.id.buttonSignUpWithGoogle)
        textViewLoginLink = findViewById(R.id.textViewLoginLink)

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Ensure this string exists
            .requestEmail()
            .requestProfile() // Request profile to get display name
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        buttonSignup.setOnClickListener {
            performStandardSignup()
        }

        buttonSignUpWithGoogle.setOnClickListener {
            signUpWithGoogle()
        }

        textViewLoginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun performStandardSignup() {
        val fullName = editTextFullNameSignup.text.toString().trim()
        val email = editTextEmailSignup.text.toString().trim()
        val password = editTextPasswordSignup.text.toString() // No trim for password
        val confirmPassword = editTextConfirmPasswordSignup.text.toString()

        var isValid = true

        if (fullName.isEmpty()) {
            textFieldFullNameSignup.error = "Full name is required"
            isValid = false
        } else {
            textFieldFullNameSignup.error = null
        }

        if (email.isEmpty()) {
            textFieldEmailSignup.error = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textFieldEmailSignup.error = "Enter a valid email address"
            isValid = false
        } else {
            textFieldEmailSignup.error = null
        }

        if (password.isEmpty()) {
            textFieldPasswordSignup.error = "Password is required"
            isValid = false
        } else if (password.length < 6) { // Example: Minimum password length
            textFieldPasswordSignup.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            textFieldPasswordSignup.error = null
        }

        if (confirmPassword.isEmpty()) {
            textFieldConfirmPasswordSignup.error = "Confirm password is required"
            isValid = false
        } else if (password != confirmPassword) {
            textFieldConfirmPasswordSignup.error = "Passwords do not match"
            isValid = false
        } else {
            textFieldConfirmPasswordSignup.error = null
        }

        if (isValid) {
            // TODO: Implement your standard signup logic (e.g., call ViewModel)
            Toast.makeText(this, "Signup Clicked (Name: $fullName, Email: $email)", Toast.LENGTH_SHORT).show()
            // Example: signupViewModel.registerUser(fullName, email, password)
            // navigateToHomeScreen()
        }
    }

    private fun signUpWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun navigateToHomeScreen() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
