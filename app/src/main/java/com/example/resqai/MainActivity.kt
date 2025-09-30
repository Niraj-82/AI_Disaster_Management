package com.example.resqai

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
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

        // --- Keep Listeners for Buttons on the Main Screen ---
        val announcementsButton: Button = findViewById(R.id.btn_announcements)
        announcementsButton.setOnClickListener {
            val intent = Intent(this, AnnouncementActivity::class.java)
            startActivity(intent)
        }

        val medicalInfoButton: Button = findViewById(R.id.btn_medical_info)
        medicalInfoButton.setOnClickListener {
            val intent = Intent(this, MedicalInfoActivity::class.java)
            startActivity(intent)
        }

        val sosButton: Button = findViewById(R.id.btn_sos)
        sosButton.setOnClickListener {
            val intent = Intent(this, SOSActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_disaster_prep -> {
                startActivity(Intent(this, DisasterPrepActivity::class.java))
            }
            R.id.nav_resource_tracker -> {
                startActivity(Intent(this, ResourceTrackerActivity::class.java))
            }
            R.id.nav_view_incidents -> {
                startActivity(Intent(this, SharedIncidentsActivity::class.java))
            }
            R.id.nav_incident_report -> {
                startActivity(Intent(this, ReportIncidentActivity::class.java))
            }
            R.id.nav_safe_routing -> {
                startActivity(Intent(this, SafeRoutingActivity::class.java))
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

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}