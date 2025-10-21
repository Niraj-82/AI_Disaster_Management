package com.example.resqai

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.card.MaterialCardView
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
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
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

        setupAdminMenu()

        // Correcting the findViewById typo.
        val cardSos: MaterialCardView = findViewById(R.id.card_sos)
        val cardMedicalInfo: MaterialCardView = findViewById(R.id.card_medical_info)
        val cardAnnouncements: MaterialCardView = findViewById(R.id.card_announcements)
        val cardViewIncidents: MaterialCardView = findViewById(R.id.card_view_incidents)

        cardSos.setOnClickListener {
            startActivity(Intent(this, SosActivity::class.java))
        }

        cardMedicalInfo.setOnClickListener {
            startActivity(Intent(this, MedicalInfoActivity::class.java))
        }

        cardAnnouncements.setOnClickListener {
            startActivity(Intent(this, AnnouncementActivity::class.java))
        }
        
        cardViewIncidents.setOnClickListener {
            startActivity(Intent(this, SharedIncidentsActivity::class.java))
        }

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
            // Not logged in, ensure admin items are hidden
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
                startActivity(Intent(this, ViewSheltersActivity::class.java))
            }
            R.id.nav_add_shelter -> {
                startActivity(Intent(this, AddShelterActivity::class.java))
            }
            R.id.nav_map -> {
                 // TODO: Create MapActivity
                Toast.makeText(this, "Map feature coming soon!", Toast.LENGTH_SHORT).show()
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
