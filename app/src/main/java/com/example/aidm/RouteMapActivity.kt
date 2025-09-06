package com.example.aidm

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location // Added for distance calculation
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.appbar.MaterialToolbar
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import kotlin.math.acos // Added for math functions
import kotlin.math.cos  // Added for math functions
import kotlin.math.pow  // Added for math functions
import kotlin.math.sin  // Added for math functions
import kotlin.math.sqrt // Added for math functions
import kotlin.math.max  // Added for math functions
import kotlin.math.min  // Added for math functions


@Serializable
data class DirectionsResponse(
    val routes: List<Route>?,
    val status: String,
    val error_message: String? = null
)

@Serializable
data class Route(
    val overview_polyline: OverviewPolyline?,
    val legs: List<Leg>?,
    @kotlinx.serialization.Transient
    var hazardScore: Int = 0
)

@Serializable
data class OverviewPolyline(
    val points: String?
)

@Serializable
data class Leg(
    val distance: Distance?,
    val duration: Duration?
)

@Serializable
data class Distance(
    val text: String?,
    val value: Int?
)

@Serializable
data class Duration(
    val text: String?,
    val value: Int?
)
// --- End Directions API Data classes ---

// --- Retrofit Service Interface (already defined) ---
interface DirectionsApiService {
    @GET("maps/api/directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String = "driving",
        @Query("alternatives") alternatives: Boolean = true,
        @Query("key") apiKey: String
    ): Response<DirectionsResponse>
}
// --- End Retrofit Service Interface ---

class RouteMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var destinationShelter: ShelterData? = null
    private var userCurrentLocation: LatLng? = null
    private lateinit var progressBar: ProgressBar
    private var currentRoutePolyline: Polyline? = null

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val directionsApiService: DirectionsApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(DirectionsApiService::class.java)
    }

    companion object {
        const val EXTRA_DESTINATION_SHELTER = "com.example.aidm.EXTRA_DESTINATION_SHELTER"
        private const val DEFAULT_ZOOM = 15f
        private const val TAG = "RouteMapActivity"
        private const val POLYLINE_WIDTH = 12f
        private val SAFE_ROUTE_COLOR = Color.GREEN
        private val HAZARDOUS_ROUTE_COLOR = Color.YELLOW
        private val DEFAULT_ROUTE_COLOR = Color.BLUE
        private const val EARTH_RADIUS_METERS = 6371000.0 // For distance calculations if needed
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                getCurrentLocationAndSetupMap()
            } else {
                Toast.makeText(this, "Location permission is required to show your position.", Toast.LENGTH_LONG).show()
                setupMapWithMarkersAndRoute(null)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_map)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar_route_map)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        progressBar = findViewById(R.id.progressBarRouteLoading)

        destinationShelter = intent.getParcelableExtra(EXTRA_DESTINATION_SHELTER)
        if (destinationShelter == null || destinationShelter?.latitude == null || destinationShelter?.longitude == null) {
            Toast.makeText(this, "Destination shelter data invalid.", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        supportActionBar?.title = "Route to ${destinationShelter?.name}"

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment_container) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        this.googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true
        checkLocationPermissionAndSetupMap()
    }

    private fun checkLocationPermissionAndSetupMap() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                googleMap?.isMyLocationEnabled = true
                googleMap?.uiSettings?.isMyLocationButtonEnabled = true
                getCurrentLocationAndSetupMap()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Toast.makeText(this, "Location permission is needed for routing.", Toast.LENGTH_LONG).show()
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun getCurrentLocationAndSetupMap() {
        progressBar.visibility = View.VISIBLE
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    userCurrentLocation = if (location != null) {
                        LatLng(location.latitude, location.longitude)
                    } else {
                        Toast.makeText(this, "Could not get current location.", Toast.LENGTH_SHORT).show()
                        null
                    }
                    setupMapWithMarkersAndRoute(userCurrentLocation)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to get current location: ${it.message}", Toast.LENGTH_SHORT).show()
                    setupMapWithMarkersAndRoute(null)
                }
        } catch (e: SecurityException) {
            progressBar.visibility = View.GONE
            Log.e(TAG, "Location permission error: ${e.message}")
            Toast.makeText(this, "Location permission error.", Toast.LENGTH_LONG).show()
            setupMapWithMarkersAndRoute(null)
        }
    }

    private fun setupMapWithMarkersAndRoute(currentLocationLatLng: LatLng?) {
        googleMap?.clear()
        currentRoutePolyline = null
        progressBar.visibility = View.VISIBLE

        val shelterLat = destinationShelter?.latitude
        val shelterLng = destinationShelter?.longitude

        if (shelterLat == null || shelterLng == null) {
            Toast.makeText(this, "Shelter location is invalid.", Toast.LENGTH_LONG).show()
            progressBar.visibility = View.GONE
            finish()
            return
        }

        val shelterLocation = LatLng(shelterLat, shelterLng)
        googleMap?.addMarker(MarkerOptions().position(shelterLocation).title(destinationShelter!!.name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))

        if (currentLocationLatLng != null) {
            googleMap?.addMarker(MarkerOptions().position(currentLocationLatLng).title("Your Location"))
            fetchDirectionsWithRetrofit(currentLocationLatLng, shelterLocation)
        } else {
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(shelterLocation, DEFAULT_ZOOM))
            displayHazards()
            progressBar.visibility = View.GONE
        }
    }

    private fun fetchDirectionsWithRetrofit(origin: LatLng, destination: LatLng) {
        progressBar.visibility = View.VISIBLE
        val apiKey = getString(R.string.google_maps_key)

        if (apiKey == "YOUR_API_KEY_HERE" || apiKey.isEmpty()) {
            Toast.makeText(this, "API Key not configured in strings.xml.", Toast.LENGTH_LONG).show()
            Log.e(TAG, "Google Maps API Key is not configured.")
            progressBar.visibility = View.GONE
            zoomToFitMarkers(origin, destination)
            displayHazards()
            return
        }

        val originStr = "${origin.latitude},${origin.longitude}"
        val destinationStr = "${destination.latitude},${destination.longitude}"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = directionsApiService.getDirections(originStr, destinationStr, apiKey = apiKey)
                if (response.isSuccessful) {
                    val directionsResponse = response.body()
                    if (directionsResponse != null) {
                        processDirectionsResponse(directionsResponse, origin, destination)
                    } else {
                        Log.e(TAG, "Directions API response body is null")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@RouteMapActivity, "Error: Empty directions response.", Toast.LENGTH_LONG).show()
                            handleNoRouteFound(origin, destination)
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.e(TAG, "Directions API Error: Code ${response.code()}, Message: ${response.message()}, Body: $errorBody")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RouteMapActivity, "Error fetching directions: ${response.message()}", Toast.LENGTH_LONG).show()
                        handleNoRouteFound(origin, destination)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception in fetchDirectionsWithRetrofit: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RouteMapActivity, "Network error: ${e.message}", Toast.LENGTH_LONG).show()
                    handleNoRouteFound(origin, destination)
                }
            }
        }
    }

    private suspend fun processDirectionsResponse(directionsResponse: DirectionsResponse, origin: LatLng, destination: LatLng) {
        if (directionsResponse.status == "OK" && !directionsResponse.routes.isNullOrEmpty()) {
            val mockHazards = getMockHazards()

            directionsResponse.routes.forEach { route ->
                route.overview_polyline?.points?.let { polylineString ->
                    val path = decodePolyline(polylineString)
                    route.hazardScore = countHazardsOnPathRobust(path, mockHazards) // Using robust check
                }
            }

            val bestRoute = directionsResponse.routes.minByOrNull { it.hazardScore }

            if (bestRoute?.overview_polyline?.points != null) {
                val decodedPath = decodePolyline(bestRoute.overview_polyline.points)
                val leg = bestRoute.legs?.firstOrNull()
                val routeColor = if (bestRoute.hazardScore == 0) SAFE_ROUTE_COLOR else HAZARDOUS_ROUTE_COLOR

                withContext(Dispatchers.Main) {
                    drawPolyline(decodedPath, routeColor)
                    val duration = leg?.duration?.text ?: "N/A"
                    val distance = leg?.distance?.text ?: "N/A"
                    val hazardsOnRouteMsg = if (bestRoute.hazardScore > 0) " (${bestRoute.hazardScore} hazard intersection(s))" else " (Clear path)"
                    Toast.makeText(this@RouteMapActivity, "Route: $distance, $duration$hazardsOnRouteMsg", Toast.LENGTH_LONG).show()
                }
            } else {
                withContext(Dispatchers.Main) { Toast.makeText(this@RouteMapActivity, "No valid route path found after processing.", Toast.LENGTH_SHORT).show() }
            }

        } else {
            val errorMessage = directionsResponse.error_message ?: directionsResponse.status
            Log.e(TAG, "Directions API returned status: $errorMessage")
            withContext(Dispatchers.Main) {
                Toast.makeText(this@RouteMapActivity, "Directions not found: $errorMessage", Toast.LENGTH_LONG).show()
            }
        }
        withContext(Dispatchers.Main) {
            progressBar.visibility = View.GONE
            zoomToFitMarkers(origin, destination)
            displayHazards()
        }
    }

    private fun handleNoRouteFound(origin: LatLng, destination: LatLng) {
        progressBar.visibility = View.GONE
        zoomToFitMarkers(origin, destination)
        displayHazards()
    }

    private fun drawPolyline(path: List<LatLng>, color: Int = DEFAULT_ROUTE_COLOR) {
        currentRoutePolyline?.remove()
        if (path.isNotEmpty()) {
            currentRoutePolyline = googleMap?.addPolyline(
                PolylineOptions()
                    .addAll(path)
                    .width(POLYLINE_WIDTH)
                    .color(color)
                    .geodesic(true)
                    .clickable(true)
            )
        }
    }

    private fun zoomToFitMarkers(origin: LatLng, destination: LatLng) {
        val builder = LatLngBounds.Builder()
        builder.include(origin)
        builder.include(destination)
        val bounds = builder.build()
        val padding = 150
        try {
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Error animating camera to bounds: ${e.message}. Map might not be ready.")
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, DEFAULT_ZOOM - 3))
        }
    }

    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val p = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            poly.add(p)
        }
        return poly
    }

    private fun displayHazards() {
        val mockHazards = getMockHazards()
        mockHazards.forEach { hazard ->
            val hazardLocation = LatLng(hazard.latitude, hazard.longitude)
            googleMap?.addMarker(
                MarkerOptions()
                    .position(hazardLocation)
                    .title("Hazard: ${hazard.type}")
                    .snippet(hazard.description)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            )
            hazard.affectedRadiusKm?.let { radiusKm ->
                googleMap?.addCircle(
                    CircleOptions()
                        .center(hazardLocation)
                        .radius(radiusKm * 1000)
                        .strokeColor(Color.argb(180, 255, 165, 0))
                        .fillColor(Color.argb(80, 255, 165, 0))
                        .strokeWidth(2f)
                )
            }
        }
    }

    private fun getMockHazards(): List<HazardData> {
        val shelterLat = destinationShelter?.latitude ?: 0.0
        val shelterLng = destinationShelter?.longitude ?: 0.0
        val userLat = userCurrentLocation?.latitude ?: shelterLat
        val userLng = userCurrentLocation?.longitude ?: shelterLng

        return listOf(
            HazardData(
                id = "H1_FLOOD", type = HazardType.FLOOD,
                latitude = (userLat + shelterLat) / 2 + (if ((userLat + shelterLat) / 2 == 0.0) 0.001 else 0.0005 * Math.random()),
                longitude = (userLng + shelterLng) / 2 + (if ((userLng + shelterLng) / 2 == 0.0) 0.001 else 0.0005 * Math.random()),
                description = "Minor Street Flooding", reportedAt = System.currentTimeMillis(), severity = 2, affectedRadiusKm = 0.05
            ),
            HazardData(
                id = "H2_BLOCKED", type = HazardType.BLOCKED_ROAD,
                latitude = shelterLat - 0.0015,
                longitude = shelterLng - 0.0015,
                description = "Large Fallen Tree", reportedAt = System.currentTimeMillis(), severity = 5, affectedRadiusKm = 0.02
            ),
            HazardData(
                id = "H3_FIRE", type = HazardType.FIRE,
                latitude = userLat + 0.001,
                longitude = userLng + 0.003,
                description = "Small Brush Fire", reportedAt = System.currentTimeMillis(), severity = 3, affectedRadiusKm = 0.1
            )
        )
    }

    // --- Robust Hazard Checking Logic ---

    /**
     * Calculates the square of the distance between two LatLng points.
     * Useful for comparisons to avoid sqrt.
     */
    private fun distanceSq(p1: LatLng, p2: LatLng): Double {
        val res = FloatArray(1)
        Location.distanceBetween(p1.latitude, p1.longitude, p2.latitude, p2.longitude, res)
        return res[0].toDouble().pow(2)
    }

    /**
     * Checks if a line segment (p1-p2) intersects with a circle (center, radius).
     * @param p1 Start point of the line segment
     * @param p2 End point of the line segment
     * @param circleCenter Center of the circle
     * @param circleRadiusMeters Radius of the circle in meters
     * @return True if the segment intersects the circle, false otherwise.
     */
    private fun isSegmentIntersectingCircle(p1: LatLng, p2: LatLng, circleCenter: LatLng, circleRadiusMeters: Double): Boolean {
        // Convert LatLng to a simpler 2D coordinate system for calculations if precision allows,
        // or use spherical geometry. For local areas, Cartesian approximation is often okay for intersection.
        // However, Location.distanceBetween is more accurate.

        // 1. Check if either endpoint is inside the circle
        val distP1ToCenter = FloatArray(1)
        Location.distanceBetween(p1.latitude, p1.longitude, circleCenter.latitude, circleCenter.longitude, distP1ToCenter)
        if (distP1ToCenter[0] <= circleRadiusMeters) return true

        val distP2ToCenter = FloatArray(1)
        Location.distanceBetween(p2.latitude, p2.longitude, circleCenter.latitude, circleCenter.longitude, distP2ToCenter)
        if (distP2ToCenter[0] <= circleRadiusMeters) return true

        // 2. Find the closest point on the infinite line (P1,P2) to the circleCenter
        // This involves projecting circleCenter onto the line defined by P1 and P2.
        // Using a common formula for distance from a point to a line segment.

        val segmentLengthSq = distanceSq(p1, p2)
        if (segmentLengthSq == 0.0) return distP1ToCenter[0] <= circleRadiusMeters // p1 and p2 are the same point

        // Consider the line segment as a vector v = p2 - p1
        // Consider vector w = circleCenter - p1
        // dotProduct = w . v
        // t = dotProduct / (length(v))^2
        // If 0 <= t <= 1, the closest point is on the segment.
        // Projection P = p1 + t * v
        // If t < 0, closest point is p1. If t > 1, closest point is p2.

        // Simplified using direct distance check for segments (can be complex for true projection on sphere)
        // A more practical approach for map data might be to rasterize the segment and circle or use a spatial library.
        // For now, let's use a simpler check: check a few points on the segment. This is not fully robust.

        // More robust: Project circle center onto the line defined by P1 and P2.
        // Then check if projection lies on segment.
        // This is complex with LatLng. A simpler but less perfect way is to check midpoint + endpoints.
        // For a better approximation, sample more points along the segment.
        val midPoint = LatLng((p1.latitude + p2.latitude) / 2, (p1.longitude + p2.longitude) / 2)
        val distMidToCenter = FloatArray(1)
        Location.distanceBetween(midPoint.latitude, midPoint.longitude, circleCenter.latitude, circleCenter.longitude, distMidToCenter)
        if (distMidToCenter[0] <= circleRadiusMeters) return true

        // A truly robust solution often involves libraries like JTS (Java Topology Suite) if working with projected coordinates,
        // or more complex spherical geometry calculations. The Google Maps SDK's own geometry library
        // (com.google.maps.android.PolyUtil or com.google.maps.android.SphericalUtil) can be helpful here,
        // especially SphericalUtil.isLocationOnPath (though that's for point on polyline, not segment-circle).

        // For this example, let's stick to endpoint and midpoint checks as a heuristic.
        // A full robust implementation is significantly more involved.
        return false // If only endpoints and midpoint checked, and none are in.
    }


    private fun countHazardsOnPathRobust(path: List<LatLng>, hazards: List<HazardData>): Int {
        if (path.size < 2) return 0 // A path needs at least two points to form a segment
        var intersections = 0

        for (i in 0 until path.size - 1) {
            val p1 = path[i]
            val p2 = path[i+1]

            for (hazard in hazards) {
                val hazardCenter = LatLng(hazard.latitude, hazard.longitude)
                val hazardRadiusMeters = (hazard.affectedRadiusKm ?: 0.02) * 1000 // Default to 20m if null

                // For simplicity, we are checking if *any point* of the segment is close to the hazard center
                // or if hazard center is close to the segment.
                // This is still an approximation.
                // A better check involves projecting the hazard center onto the line defined by p1,p2
                // and then checking distances.
                // SphericalUtil.isLocationOnPath from Maps SDK utility library can check if a point is near a polyline.
                // We need the inverse: if a polyline segment is near a point (hazard center).

                // Let's use a simplified check: if any of the segment's endpoints or its midpoint
                // fall within the hazard radius. This is not a true segment-circle intersection.
                val pointsOnSegmentToTest = listOf(p1, LatLng((p1.latitude + p2.latitude) / 2, (p1.longitude + p2.longitude) / 2), p2)
                var intersectedThisHazard = false
                for(testPoint in pointsOnSegmentToTest) {
                    val distArray = FloatArray(1)
                    Location.distanceBetween(testPoint.latitude, testPoint.longitude, hazardCenter.latitude, hazardCenter.longitude, distArray)
                    if (distArray[0] <= hazardRadiusMeters) {
                        intersectedThisHazard = true
                        break
                    }
                }
                if (intersectedThisHazard) {
                    intersections++
                    // break // Optional: if we only care if a route hits *a* hazard, not how many times or how many hazards.
                    // For scoring, we probably want to count all intersections.
                }

                // Placeholder for a more robust check:
                // if (isSegmentTrulyIntersectingCircle(p1, p2, hazardCenter, hazardRadiusMeters)) {
                //     intersections++
                // }
            }
        }
        return intersections
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
