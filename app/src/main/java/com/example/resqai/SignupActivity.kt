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
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.example.resqai.viewmodel.SignupViewModel

class SignupActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var tilEmail: TextInputLayout
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var tilPassword: TextInputLayout
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var tilConfirmPassword: TextInputLayout
    private lateinit var editTextConfirmPassword: TextInputEditText
    private lateinit var tilAdminCode: TextInputLayout
    private lateinit var editTextAdminCode: TextInputEditText
    private lateinit var buttonSignup: MaterialButton
    private lateinit var textViewGoToLogin: TextView
    private lateinit var progressBar: ProgressBar

    private val signupViewModel: SignupViewModel by viewModels()
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
        tilAdminCode = findViewById(R.id.tilAdminCode)
        editTextAdminCode = findViewById(R.id.editTextAdminCode)
        buttonSignup = findViewById(R.id.buttonSignup)
        textViewGoToLogin = findViewById(R.id.textViewGoToLogin)
        progressBar = findViewById(R.id.progressBarSignup)

        buttonSignup.setOnClickListener {
            handleSignup()
        }

        textViewGoToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        signupViewModel.signupStatus.observe(this) {
            state ->
            when (state) {
                is SignupViewModel.SignupState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    buttonSignup.isEnabled = false
                }
                is SignupViewModel.SignupState.Success -> {
                    progressBar.visibility = View.GONE
                    buttonSignup.isEnabled = true
                    Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show()
                    // TODO: Navigate to the appropriate activity based on role
                    finish()
                }
                is SignupViewModel.SignupState.Error -> {
                    progressBar.visibility = View.GONE
                    buttonSignup.isEnabled = true
                    Toast.makeText(this, "Signup failed: ${state.message}", Toast.LENGTH_LONG).show()
                }
                else -> {
                    progressBar.visibility = View.GONE
                    buttonSignup.isEnabled = true
                }
            }
        }
    }

    private fun handleSignup() {
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        val confirmPassword = editTextConfirmPassword.text.toString().trim()
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

        val role = if (adminCode.isNotEmpty() && adminCode == ADMIN_SIGNUP_CODE) {
            "admin"
        } else {
            if(adminCode.isNotEmpty()){
                tilAdminCode.error = "Invalid admin code"
                isValid = false
            }
            "normal"
        }

        if (!isValid) {
            return
        }


        signupViewModel.signupUser(email, password, role)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
