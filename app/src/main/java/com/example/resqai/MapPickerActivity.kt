package com.example.resqai

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.resqai.databinding.ActivityMapPickerBinding
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapController
import org.osmdroid.views.overlay.Marker

class MapPickerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapPickerBinding
    private var selectedLocation: GeoPoint? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(this, getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE))
        binding = ActivityMapPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapController = binding.mapPickerView.controller as MapController
        mapController.setZoom(15.0)

        // Default to a central location if no initial location is provided
        val startPoint = GeoPoint(34.0522, -118.2437) // Los Angeles
        mapController.setCenter(startPoint)

        binding.mapPickerView.setMultiTouchControls(true)

        val marker = Marker(binding.mapPickerView)
        binding.mapPickerView.overlays.add(marker)

        binding.mapPickerView.overlays.add(object : org.osmdroid.views.overlay.Overlay() {
            override fun onSingleTapConfirmed(e: android.view.MotionEvent, mapView: org.osmdroid.views.MapView): Boolean {
                val projection = mapView.projection
                selectedLocation = projection.fromPixels(e.x.toInt(), e.y.toInt()) as GeoPoint
                marker.position = selectedLocation
                mapView.invalidate() // Redraw the map
                return true
            }
        })

        binding.btnConfirmLocation.setOnClickListener {
            selectedLocation?.let {
                val resultIntent = Intent()
                resultIntent.putExtra("latitude", it.latitude)
                resultIntent.putExtra("longitude", it.longitude)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }
}
