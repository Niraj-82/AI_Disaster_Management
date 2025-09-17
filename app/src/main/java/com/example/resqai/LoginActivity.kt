package com.example.resqai

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels // If using ViewModel
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
// TODO: Create LoginViewModel
// import com.example.resqai.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var tilEmail: TextInputLayout
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var tilPassword: TextInputLayout
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var buttonLogin: MaterialButton
    private lateinit var textViewGoToSignup: TextView
    private lateinit var progressBar: ProgressBar

    // TODO: Initialize LoginViewModel
    // private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        toolbar = findViewById(R.id.toolbarLogin)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // For back navigation if needed
        supportActionBar?.setDisplayShowHomeEnabled(true)

        tilEmail = findViewById(R.id.tilLoginEmail)
        editTextEmail = findViewById(R.id.editTextLoginEmail)
        tilPassword = findViewById(R.id.tilLoginPassword)
        editTextPassword = findViewById(R.id.editTextLoginPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        textViewGoToSignup = findViewById(R.id.textViewGoToSignup)
        progressBar = findViewById(R.id.progressBarLogin)

        buttonLogin.setOnClickListener {
            handleLogin()
        }

        textViewGoToSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            // finish() // Optional: finish LoginActivity so user can't go back to it from Signup
        }

        // TODO: Observe ViewModel LiveData for login status, errors, loading
        // loginViewModel.loginStatus.observe(this) { status ->
        //     when (status) {
        //         is LoginViewModel.LoginState.Loading -> {
        //             progressBar.visibility = View.VISIBLE
        //             buttonLogin.isEnabled = false
        //         }
        //         is LoginViewModel.LoginState.Success -> {
        //             progressBar.visibility = View.GONE
        //             buttonLogin.isEnabled = true
        //             Toast.makeText(this, "Login Successful. Role: ${status.userRole}", Toast.LENGTH_SHORT).show()
        //             // TODO: Navigate to HomeActivity or AdminActivity based on role
        //             // For example:
        //             // if (status.userRole == "admin") {
        //             //     startActivity(Intent(this, AdminDashboardActivity::class.java))
        //             // } else {
        //             //     startActivity(Intent(this, HomeActivity::class.java))
        //             // }
        //             // finishAffinity() // Clear back stack
        //         }
        //         is LoginViewModel.LoginState.Error -> {
        //             progressBar.visibility = View.GONE
        //             buttonLogin.isEnabled = true
        //             Toast.makeText(this, status.message, Toast.LENGTH_LONG).show()
        //         }
        //         else -> { // Idle
        //             progressBar.visibility = View.GONE
        //             buttonLogin.isEnabled = true
        //         }
        //     }
        // }
    }

    private fun handleLogin() {
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        var isValid = true
        if (email.isEmpty()) {
            tilEmail.error = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = "Enter a valid email address"
            isValid = false
        } else {
            tilEmail.error = null
        }

        if (password.isEmpty()) {
            tilPassword.error = "Password is required"
            isValid = false
        } else {
            tilPassword.error = null
        }

        if (!isValid) {
            return
        }

        progressBar.visibility = View.VISIBLE
        buttonLogin.isEnabled = false
        // TODO: Call ViewModel to perform login
        // loginViewModel.loginUser(email, password)
        Toast.makeText(this, "Login attempt: Email: $email", Toast.LENGTH_LONG).show()
        // Simulate network call for now
        // progressBar.visibility = View.GONE
        // buttonLogin.isEnabled = true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
