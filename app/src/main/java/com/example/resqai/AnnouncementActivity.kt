package com.example.resqai

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 103
    private val SOS_CHANNEL_ID = "sos_alerts"
    private val GENERAL_CHANNEL_ID = "general_announcements"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_announcement)

        announcementsRecyclerView = findViewById(R.id.announcements_recycler_view)
        announcementInputLayout = findViewById(R.id.announcement_input_layout)
        announcementEditText = findViewById(R.id.announcement_edit_text)
        postAnnouncementButton = findViewById(R.id.post_announcement_button)

        announcementsRecyclerView.layoutManager = LinearLayoutManager(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        createNotificationChannels()
        requestNotificationPermission()
        checkUserRole()
        checkLocationPermissionAndLoadAnnouncements()

        postAnnouncementButton.setOnClickListener {
            postAnnouncement()
        }
    }
    
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_REQUEST_CODE)
            }
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
                        if (newAnnouncement != null) {
                            if (newAnnouncement.title == "SOS Alert") {
                                checkDistanceAndNotify(newAnnouncement)
                            } else {
                                sendNotification(newAnnouncement, isSos = false)
                            }
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
                if (distance <= 10000) { // 10km radius
                    sendNotification(announcement, isSos = true)
                }
            }
        }
    }

    private fun sendNotification(announcement: Announcement, isSos: Boolean) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.w("AnnouncementActivity", "Notification permission not granted, cannot send notification.")
            return
        }

        val channelId = if (isSos) SOS_CHANNEL_ID else GENERAL_CHANNEL_ID
        val icon = if (isSos) R.drawable.ic_sos else R.drawable.ic_launcher_foreground
        val priority = if (isSos) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT
        
        val intent: Intent
        if (isSos && announcement.location != null) {
            intent = Intent(this, IncidentsMapActivity::class.java).apply {
                putExtra("latitude", announcement.location.latitude)
                putExtra("longitude", announcement.location.longitude)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        } else {
            intent = Intent(this, AnnouncementActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
        
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            (announcement.id ?: announcement.timestamp ?: Date().time).hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = (announcement.id ?: announcement.timestamp ?: Date().time).hashCode()

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(icon)
            .setContentTitle(announcement.title ?: if (isSos) "SOS Alert" else "New Announcement")
            .setContentText(announcement.message ?: if (isSos) "Someone needs help near you." else "Check the app for details.")
            .setPriority(priority)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val sosChannelName = "SOS Alerts"
            val sosDescriptionText = "Notifications for SOS alerts from nearby users"
            val sosImportance = NotificationManager.IMPORTANCE_HIGH
            val sosChannel = NotificationChannel(SOS_CHANNEL_ID, sosChannelName, sosImportance).apply {
                description = sosDescriptionText
            }
            notificationManager.createNotificationChannel(sosChannel)

            val generalChannelName = "General Announcements"
            val generalDescriptionText = "Notifications for general announcements"
            val generalImportance = NotificationManager.IMPORTANCE_DEFAULT
            val generalChannel = NotificationChannel(GENERAL_CHANNEL_ID, generalChannelName, generalImportance).apply {
                description = generalDescriptionText
            }
            notificationManager.createNotificationChannel(generalChannel)
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
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    loadAnnouncements()
                } else {
                    Toast.makeText(this, "Location permission denied. Cannot check for nearby alerts.", Toast.LENGTH_LONG).show()
                    loadAnnouncements() 
                }
            }
            NOTIFICATION_PERMISSION_REQUEST_CODE -> {
                 if (!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "Notification permission denied. You may miss important alerts.", Toast.LENGTH_LONG).show()
                }
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
