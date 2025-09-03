package com.example.aidm

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Immediately launch SplashActivity
        startActivity(Intent(this, SplashActivity::class.java))
        finish() // Finish MainActivity so it's not on the back stack
    }
}
