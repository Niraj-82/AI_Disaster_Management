package com.example.aidm

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.google.android.material.appbar.MaterialToolbar

class HostActivityWithTopBar : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host_with_top_bar)

        // Find the toolbar using the ID from within activity_top_bar.xml
        val toolbar: MaterialToolbar = findViewById(R.id.reusable_material_toolbar)
        setSupportActionBar(toolbar)

        // Set the title for this specific activity
        supportActionBar?.title = "Host Activity"

        // Enable and handle the navigation icon (e.g., hamburger or back arrow)
        // The icon is set in activity_top_bar.xml (app:navigationIcon)
        // To make it a back button, you'd typically use:
        // supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // supportActionBar?.setDisplayShowHomeEnabled(true)
        // And then override onOptionsItemSelected to handle android.R.id.home

        // Example: Handling the default navigation icon set in XML
        toolbar.setNavigationOnClickListener {
            // Handle navigation icon press, e.g., open navigation drawer or finish activity
            Toast.makeText(this, "Navigation icon clicked!", Toast.LENGTH_SHORT).show()
            // If it's meant to be a back button:
            // super.onBackPressedDispatcher.onBackPressed()
        }

        // Example button in the content area
        val exampleButton: Button = findViewById(R.id.button_example_action)
        exampleButton.setOnClickListener {
            Toast.makeText(this, "Button in content clicked!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_host_activity, menu)

        val searchItem = menu.findItem(R.id.action_search_host)
        val searchView = searchItem.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Toast.makeText(this@HostActivityWithTopBar, "Search submitted: $query", Toast.LENGTH_SHORT).show()
                searchView.clearFocus() // Hide keyboard
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle text changes if needed for live search suggestions
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            android.R.id.home -> { // If setDisplayHomeAsUpEnabled(true) is used for Up navigation
                super.onBackPressedDispatcher.onBackPressed()
                true
            }
            R.id.action_settings_host -> {
                Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_profile_host -> {
                Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
                true
            }
            // R.id.action_search_host is handled by its actionViewClass
            else -> super.onOptionsItemSelected(item)
        }
    }
}
