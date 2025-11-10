package com.example.resqai

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.resqai.databinding.ActivityHomeBinding
import com.example.resqai.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var homeBinding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        // Inflate the included layout and get its binding
        homeBinding = ActivityHomeBinding.bind(mainBinding.root.findViewById(R.id.home_content_container))

        val drawerLayout = mainBinding.drawerLayout
        val toolbar = mainBinding.toolbar
        setSupportActionBar(toolbar)

        val navigationView = mainBinding.navView
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        setupCardListeners()

        checkUserRole(navigationView)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    if (isEnabled) {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        })
    }

    private fun setupCardListeners() {
        homeBinding.cardSos.setOnClickListener {
            startActivity(Intent(this, SOSActivity::class.java))
        }

        homeBinding.cardMedicalInfo.setOnClickListener {
            startActivity(Intent(this, MedicalInfoActivity::class.java))
        }

        homeBinding.cardAnnouncements.setOnClickListener {
            startActivity(Intent(this, AnnouncementActivity::class.java))
        }

        homeBinding.cardViewIncidents.setOnClickListener {
            startActivity(Intent(this, ViewIncidentsActivity::class.java))
        }

        homeBinding.cardEmergencyContacts.setOnClickListener {
            startActivity(Intent(this, EmergencyContactsActivity::class.java))
        }

        homeBinding.cardChatbot.setOnClickListener {
            startActivity(Intent(this, ChatbotActivity::class.java))
        }
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
        homeBinding.cardViewIncidents.visibility = View.VISIBLE
        homeBinding.cardAnnouncements.visibility = View.VISIBLE
    }

    private fun setAdminMenuVisibility(navigationView: NavigationView, isVisible: Boolean) {
        try {
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
        mainBinding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
