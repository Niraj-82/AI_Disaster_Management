package com.example.resqai

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // This layout includes the Nav Drawer and toolbar

        drawerLayout = findViewById(R.id.drawer_layout)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navigationView: NavigationView = findViewById(R.id.nav_view)
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

        checkUserRole(navigationView)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    // If the drawer is not open, perform the default back action
                    if (isEnabled) {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        })
    }

    private fun checkUserRole(navigationView: NavigationView) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            setAdminMenuVisibility(navigationView, false)
            updateCardVisibility(false)
            return
        }

        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val role = document.getString("role")
                val isAdmin = role == "admin"
                setAdminMenuVisibility(navigationView, isAdmin)
                updateCardVisibility(isAdmin)
            }
            .addOnFailureListener {
                setAdminMenuVisibility(navigationView, false)
                updateCardVisibility(false)
                Toast.makeText(this, "Failed to check user role.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateCardVisibility(isAdmin: Boolean) {
        // Shared incidents should be visible to everyone, so we ensure it's always visible here.
        findViewById<CardView>(R.id.card_view_incidents).visibility = View.VISIBLE
        
        // Announcements are also for everyone.
        findViewById<CardView>(R.id.card_announcements).visibility = View.VISIBLE
    }

    private fun setAdminMenuVisibility(navigationView: NavigationView, isVisible: Boolean) {
        try {
            // This correctly shows/hides the "Add Shelter" option in the navigation drawer for admins.
            navigationView.menu.findItem(R.id.nav_add_shelter).isVisible = isVisible
        } catch (e: Exception) {
            // Item not found, do nothing
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_report_incident -> startActivity(Intent(this, ReportIncidentActivity::class.java))
            R.id.nav_view_shelters -> startActivity(Intent(this, ViewSheltersActivity::class.java))
            R.id.nav_add_shelter -> startActivity(Intent(this, AddShelterActivity::class.java))
             R.id.nav_map -> {
                startActivity(Intent(this, MapActivity::class.java))
            }
            R.id.nav_logout -> {
                FirebaseAuth.getInstance().signOut()
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
