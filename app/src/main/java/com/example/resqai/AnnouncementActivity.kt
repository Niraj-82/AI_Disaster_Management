package com.example.resqai

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

data class Announcement(val message: String = "", val timestamp: Long = 0)

class AnnouncementActivity : AppCompatActivity() {

    private lateinit var announcementsRecyclerView: RecyclerView
    private lateinit var announcementInputLayout: LinearLayout
    private lateinit var announcementEditText: EditText
    private lateinit var postAnnouncementButton: Button

    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_announcement)

        announcementsRecyclerView = findViewById(R.id.announcements_recycler_view)
        announcementInputLayout = findViewById(R.id.announcement_input_layout)
        announcementEditText = findViewById(R.id.announcement_edit_text)
        postAnnouncementButton = findViewById(R.id.post_announcement_button)

        checkUserRole()
        loadAnnouncements()

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

    private fun loadAnnouncements() {
        firestore.collection("announcements")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                val announcements = snapshots?.toObjects(Announcement::class.java) ?: emptyList()
                announcementsRecyclerView.adapter = AnnouncementAdapter(announcements)
            }
    }

    private fun postAnnouncement() {
        val message = announcementEditText.text.toString().trim()
        if (message.isNotEmpty()) {
            val announcement = Announcement(
                message = message,
                timestamp = System.currentTimeMillis()
            )
            firestore.collection("announcements").add(announcement)
                .addOnSuccessListener {
                    announcementEditText.text.clear()
                }
                .addOnFailureListener {
                    // Handle error
                }
        }
    }
}

class AnnouncementAdapter(private val announcements: List<Announcement>) :
    RecyclerView.Adapter<AnnouncementAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
        holder.messageTextView.text = announcement.message
        // Format the timestamp to a readable date/time
        holder.timestampTextView.text = android.text.format.DateUtils.getRelativeTimeSpanString(announcement.timestamp)
    }

    override fun getItemCount() = announcements.size
}

