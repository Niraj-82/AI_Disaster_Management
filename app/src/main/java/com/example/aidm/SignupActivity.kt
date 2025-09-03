package com.example.aidm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val buttonSignUp = findViewById<Button>(R.id.buttonSignUp)
        val textViewLogin = findViewById<TextView>(R.id.textViewLogin)

        buttonSignUp.setOnClickListener {
            // TODO: Implement actual signup logic
            // For now, navigate to a placeholder HomeActivity
            startActivity(Intent(this, HomeActivity::class.java))
            finishAffinity() // Clear back stack
        }

        textViewLogin.setOnClickListener {
            // Navigate back to LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
