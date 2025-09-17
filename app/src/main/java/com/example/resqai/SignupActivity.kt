package com.example.resqai

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

// TODO: Create SignupViewModel
// import com.example.resqai.viewmodel.SignupViewModel

class SignupActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var tilEmail: TextInputLayout
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var tilPassword: TextInputLayout
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var tilConfirmPassword: TextInputLayout
    private lateinit var editTextConfirmPassword: TextInputEditText
    private lateinit var switchAdmin: SwitchMaterial
    private lateinit var tilAdminCode: TextInputLayout
    private lateinit var editTextAdminCode: TextInputEditText
    private lateinit var buttonSignup: MaterialButton
    private lateinit var textViewGoToLogin: TextView
    private lateinit var progressBar: ProgressBar

    // TODO: Initialize SignupViewModel
    // private val signupViewModel: SignupViewModel by viewModels()
    private val ADMIN_SIGNUP_CODE = "12345"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        toolbar = findViewById(R.id.toolbarSignup)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        tilEmail = findViewById(R.id.tilSignupEmail)
        editTextEmail = findViewById(R.id.editTextSignupEmail)
        tilPassword = findViewById(R.id.tilSignupPassword)
        editTextPassword = findViewById(R.id.editTextSignupPassword)
        tilConfirmPassword = findViewById(R.id.tilSignupConfirmPassword)
        editTextConfirmPassword = findViewById(R.id.editTextSignupConfirmPassword)
        switchAdmin = findViewById(R.id.switchAdminSignup)
        tilAdminCode = findViewById(R.id.tilAdminCode)
        editTextAdminCode = findViewById(R.id.editTextAdminCode)
        buttonSignup = findViewById(R.id.buttonSignup)
        textViewGoToLogin = findViewById(R.id.textViewGoToLogin)
        progressBar = findViewById(R.id.progressBarSignup)

        switchAdmin.setOnCheckedChangeListener { _, isChecked ->
            tilAdminCode.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        buttonSignup.setOnClickListener {
            handleSignup()
        }

        textViewGoToLogin.setOnClickListener {
            // TODO: Navigate to LoginActivity
            // startActivity(Intent(this, LoginActivity::class.java))
            // finish()
            Toast.makeText(this, "Navigate to Login Screen", Toast.LENGTH_SHORT).show()
        }

        // TODO: Observe ViewModel LiveData for signup status, errors, loading
    }

    private fun handleSignup() {
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        val confirmPassword = editTextConfirmPassword.text.toString().trim()
        val isAdmin = switchAdmin.isChecked
        val adminCode = editTextAdminCode.text.toString().trim()

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
        } else if (password.length < 6) {
            tilPassword.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            tilPassword.error = null
        }

        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.error = "Confirm password is required"
            isValid = false
        } else if (password != confirmPassword) {
            tilConfirmPassword.error = "Passwords do not match"
            isValid = false
        } else {
            tilConfirmPassword.error = null
        }

        if (isAdmin) {
            if (adminCode.isEmpty()) {
                tilAdminCode.error = "Admin code is required"
                isValid = false
            } else if (adminCode != ADMIN_SIGNUP_CODE) {
                tilAdminCode.error = "Invalid admin code"
                isValid = false
            } else {
                tilAdminCode.error = null
            }
        }

        if (!isValid) {
            return
        }
        
        progressBar.visibility = View.VISIBLE
        buttonSignup.isEnabled = false
        // TODO: Call ViewModel to perform signup
        // val role = if (isAdmin) "admin" else "normal"
        // signupViewModel.signupUser(email, password, role)
        Toast.makeText(this, "Signup attempt: Email: $email, Admin: $isAdmin", Toast.LENGTH_LONG).show()
        // Simulate network call
        // progressBar.visibility = View.GONE
        // buttonSignup.isEnabled = true

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
