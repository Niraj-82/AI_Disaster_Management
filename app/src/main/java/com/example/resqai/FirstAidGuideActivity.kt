package com.example.resqai

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class FirstAidGuideActivity : AppCompatActivity() {

    private lateinit var lvFirstAidTopics: ListView
    private val topics = arrayOf(
        "Burns", "Bleeding", "Fractures", "Choking", "Sprains"
    )
    private val topicDetails = mapOf(
        "Burns" to "1. Stop Burning Process: Remove the person from the source of the burn. \n2. Cool the Burn: Use cool (not ice-cold) running water for 10-15 minutes. \n3. Cover the Burn: Use a sterile, non-fluffy dressing. \n4. Seek Medical Attention: For severe burns or if unsure.",
        "Bleeding" to "1. Apply Pressure: Use a clean cloth or dressing and apply firm pressure directly to the wound. \n2. Elevate the Wound: If possible, raise the injured part above the heart. \n3. Maintain Pressure: If blood soaks through, add more dressing on top – do not remove the original. \n4. Seek Medical Attention: For severe bleeding.",
        "Fractures" to "1. Immobilize the Area: Do not try to realign the bone. Keep the injured part still. \n2. Apply a Splint: If trained, apply a splint to support the limb. \n3. Apply Ice Packs: To reduce swelling and pain. Wrap ice in a cloth. \n4. Seek Medical Attention: Immediately.",
        "Choking" to "1. Encourage Coughing: If the person is coughing forcefully, encourage them to continue. \n2. Back Blows: If coughing is not effective, give up to 5 sharp back blows between the shoulder blades. \n3. Abdominal Thrusts (Heimlich Maneuver): If back blows fail, give up to 5 abdominal thrusts. \n4. Call for Emergency Help: If the obstruction is not cleared.",
        "Sprains" to "R.I.C.E. Method: \n1. Rest: Avoid using the injured joint. \n2. Ice: Apply an ice pack (wrapped in a cloth) for 15-20 minutes every 2-3 hours. \n3. Compression: Use an elastic bandage to wrap the joint, but not too tightly. \n4. Elevation: Keep the injured limb elevated above the heart as much as possible."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_aid_guide)

        title = getString(R.string.first_aid_guide_title_activity)

        lvFirstAidTopics = findViewById(R.id.lv_first_aid_topics)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, topics)
        lvFirstAidTopics.adapter = adapter

        lvFirstAidTopics.setOnItemClickListener { _, _, position, _ ->
            val selectedTopic = topics[position]
            val detail = topicDetails[selectedTopic] ?: "No details available."

            val intent = Intent(this, FirstAidDetailActivity::class.java).apply {
                putExtra("TOPIC_TITLE", selectedTopic)
                putExtra("TOPIC_DETAIL", detail)
            }
            startActivity(intent)
        }
    }
}
