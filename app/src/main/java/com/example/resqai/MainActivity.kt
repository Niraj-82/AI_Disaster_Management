package com.example.resqai

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            // Not logged in, redirect to LoginActivity and finish this activity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return // Stop further execution of onCreate
        }

        // If the user is logged in, proceed to set up the main activity
        setContentView(R.layout.activity_main)

        db = FirebaseFirestore.getInstance()
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Set up card listeners from the included activity_home layout
        findViewById<CardView>(R.id.card_sos).setOnClickListener {
            startActivity(Intent(this, SOSActivity::class.java))
        }

        findViewById<CardView>(R.id.card_medical_info).setOnClickListener {
            startActivity(Intent(this, MedicalInfoActivity::class.java))
        }

        findViewById<CardView>(R.id.card_announcements).setOnClickListener {
            startActivity(Intent(this, AnnouncementActivity::class.java))
        }

        findViewById<CardView>(R.id.card_view_incidents).setOnClickListener {
            startActivity(Intent(this, ViewIncidentsActivity::class.java))
        }

        findViewById<CardView>(R.id.card_emergency_contacts).setOnClickListener {
            startActivity(Intent(this, EmergencyContactsActivity::class.java))
        }

        setupAdminMenu()


        // Handle back press to close the navigation drawer
        onBackPressedDispatcher.addCallback(this) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                // If the drawer is closed, and the callback is enabled,
                // disable it and dispatch the back press to trigger the default behavior.
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun setupAdminMenu() {
        val menu = navigationView.menu
        val addShelterItem = menu.findItem(R.id.nav_add_shelter)

        // Hide all admin items by default
        addShelterItem.isVisible = false

        val user = auth.currentUser
        if (user == null) {
            return
        }

        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val role = document.getString("role")
                    if (role == "admin") {
                        // User is an admin, show the items
                        addShelterItem.isVisible = true
                    }
                }
            }
            .addOnFailureListener {
                // Handle failure, maybe log it
            }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_report_incident -> {
                startActivity(Intent(this, ReportIncidentActivity::class.java))
            }
            R.id.nav_view_shelters -> {
                startActivity(Intent(this, FindShelterActivity::class.java))
            }
            R.id.nav_add_shelter -> {
                startActivity(Intent(this, AddShelterActivity::class.java))
            }
            R.id.nav_map -> {
                startActivity(Intent(this, MapActivity::class.java))
            }
            R.id.nav_logout -> {
                auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
