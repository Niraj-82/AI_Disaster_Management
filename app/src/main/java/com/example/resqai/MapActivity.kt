package com.example.resqai

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.resqai.model.Shelter
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Initialize osmdroid configuration
        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE))

        mapView = findViewById(R.id.map)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        val shelter = intent.getParcelableExtra<Shelter>("shelter")

        if (shelter != null && shelter.latitude != null && shelter.longitude != null) {
            val shelterPoint = GeoPoint(shelter.latitude!!, shelter.longitude!!)

            mapView.controller.setZoom(15.0)
            mapView.controller.setCenter(shelterPoint)

            val shelterMarker = Marker(mapView)
            shelterMarker.position = shelterPoint
            shelterMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            shelterMarker.title = shelter.name
            mapView.overlays.add(shelterMarker)
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
}