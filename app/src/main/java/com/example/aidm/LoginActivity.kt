package com.example.aidm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
// Import for EditText can be android.widget.EditText or com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val textViewSignUp = findViewById<TextView>(R.id.textViewSignUp) // Corrected ID

        buttonLogin.setOnClickListener {
            // TODO: Implement actual login logic
            // For now, navigate to a placeholder HomeActivity
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        textViewSignUp.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}
