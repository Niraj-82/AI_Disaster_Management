package com.example.resqai

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resqai.model.Announcement
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.Timestamp
import java.util.Date

class AnnouncementActivity : AppCompatActivity() {

    private lateinit var announcementsRecyclerView: RecyclerView
    private lateinit var announcementInputLayout: LinearLayout
    private lateinit var announcementEditText: EditText
    private lateinit var postAnnouncementButton: Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    private val LOCATION_PERMISSION_REQUEST_CODE = 102
    private val NOTIFICATION_CHANNEL_ID = "sos_alerts"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_announcement)

        announcementsRecyclerView = findViewById(R.id.announcements_recycler_view)
        announcementInputLayout = findViewById(R.id.announcement_input_layout)
        announcementEditText = findViewById(R.id.announcement_edit_text)
        postAnnouncementButton = findViewById(R.id.post_announcement_button)

        announcementsRecyclerView.layoutManager = LinearLayoutManager(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        createNotificationChannel()
        checkUserRole()
        checkLocationPermissionAndLoadAnnouncements()

        postAnnouncementButton.setOnClickListener {
            postAnnouncement()
        }
    }

    private fun checkUserRole() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.getString("role") == "admin") {
                    announcementInputLayout.visibility = View.VISIBLE
                }
            }
    }

    private fun checkLocationPermissionAndLoadAnnouncements() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            loadAnnouncements()
        }
    }

    private fun loadAnnouncements() {
        firestore.collection("announcements")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Error loading announcements", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val announcements = snapshots?.documents?.mapNotNull { document ->
                    try {
                        val timestampObject = document.get("timestamp")
                        val timestamp = when (timestampObject) {
                            is Timestamp -> timestampObject.toDate().time
                            is Long -> timestampObject
                            else -> null
                        }

                        Announcement(
                            id = document.id,
                            title = document.getString("title"),
                            message = document.getString("message"),
                            timestamp = timestamp,
                            location = document.getGeoPoint("location")
                        )
                    } catch (ex: Exception) {
                        Log.e("AnnouncementActivity", "Error parsing announcement document", ex)
                        null // Ignore documents that fail to parse
                    }
                } ?: emptyList()

                announcementsRecyclerView.adapter = AnnouncementAdapter(announcements)

                snapshots?.documentChanges?.forEach { dc ->
                    if (dc.type == com.google.firebase.firestore.DocumentChange.Type.ADDED) {
                         val newAnnouncement = announcements.firstOrNull { it.id == dc.document.id }
                        if (newAnnouncement?.title == "SOS Alert") {
                            checkDistanceAndNotify(newAnnouncement)
                        }
                    }
                }
            }
    }

    private fun checkDistanceAndNotify(announcement: Announcement) {
        val announcementGeoPoint = announcement.location ?: return

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { userLocation: Location? ->
            if (userLocation != null) {
                val announcementLocation = Location("").apply {
                    latitude = announcementGeoPoint.latitude
                    longitude = announcementGeoPoint.longitude
                }

                val distance = userLocation.distanceTo(announcementLocation) // Distance in meters
                if (distance <= 1000) { // 1km radius
                    sendNotification(announcement)
                }
            }
        }
    }

    private fun sendNotification(announcement: Announcement) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = (announcement.id ?: announcement.timestamp ?: Date().time).hashCode()

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_sos)
            .setContentTitle(announcement.title ?: "SOS Alert")
            .setContentText(announcement.message ?: "Someone needs help near you.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "SOS Alerts"
            val descriptionText = "Notifications for SOS alerts from nearby users"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun postAnnouncement() {
        val message = announcementEditText.text.toString().trim()
        if (message.isNotEmpty()) {
            val announcement = Announcement(
                title = "General Announcement",
                message = message,
                timestamp = Date().time,
                location = null
            )
            firestore.collection("announcements").add(announcement)
                .addOnSuccessListener {
                    announcementEditText.text.clear()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to post announcement", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                loadAnnouncements()
            } else {
                Toast.makeText(this, "Location permission denied. Cannot check for nearby alerts.", Toast.LENGTH_LONG).show()
                loadAnnouncements()
            }
        }
    }
}

class AnnouncementAdapter(private val announcements: List<Announcement>) :
    RecyclerView.Adapter<AnnouncementAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.announcement_title_text_view)
        val messageTextView: TextView = view.findViewById(R.id.announcement_message_text_view)
        val timestampTextView: TextView = view.findViewById(R.id.announcement_timestamp_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_announcement, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val announcement = announcements[position]
        holder.titleTextView.text = announcement.title ?: "No Title"
        holder.messageTextView.text = announcement.message ?: ""
        holder.timestampTextView.text = announcement.timestamp?.let {
            android.text.format.DateUtils.getRelativeTimeSpanString(it)
        } ?: ""
    }

    override fun getItemCount() = announcements.size
}
