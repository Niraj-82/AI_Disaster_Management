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
import com.example.resqai.viewmodel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var tilEmail: TextInputLayout
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var tilPassword: TextInputLayout
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var buttonLogin: MaterialButton
    private lateinit var textViewGoToSignup: TextView
    private lateinit var progressBar: ProgressBar

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        toolbar = findViewById(R.id.toolbarLogin)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
        }

        loginViewModel.loginStatus.observe(this) { state ->
            when (state) {
                is LoginViewModel.LoginState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    buttonLogin.isEnabled = false
                }
                is LoginViewModel.LoginState.Success -> {
                    progressBar.visibility = View.GONE
                    buttonLogin.isEnabled = true
                    Toast.makeText(this, "Login Successful. Role: ${state.role}", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finishAffinity() // Clear back stack
                }
                is LoginViewModel.LoginState.Error -> {
                    progressBar.visibility = View.GONE
                    buttonLogin.isEnabled = true
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
                else -> { // Idle
                    progressBar.visibility = View.GONE
                    buttonLogin.isEnabled = true
                }
            }
        }
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

        loginViewModel.loginUser(email, password)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
