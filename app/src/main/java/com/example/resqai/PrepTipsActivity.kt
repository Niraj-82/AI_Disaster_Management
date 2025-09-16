package com.example.resqai

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PrepTipsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DISASTER_TYPE = "com.example.resqai.EXTRA_DISASTER_TYPE"
    }

    private lateinit var tvPrepTipsTitle: TextView
    private lateinit var tvPrepTipsContent: TextView

    // Sample data - in a real app, this would be more extensive and possibly from resources
    private val disasterTipsMap = mapOf(
        "Earthquake" to "1. Drop, Cover, and Hold On during shaking.\n" +
                "2. Stay away from windows, glass, and anything that could fall.\n" +
                "3. Have an emergency kit with water, food, first aid, and a flashlight.\n" +
                "4. After shaking stops, check for injuries and damage. Evacuate if necessary.\n" +
                "5. Be prepared for aftershocks.",
        "Flood" to "1. Evacuate immediately if told to do so. Go to higher ground.\n" +
                "2. Do not walk, swim, or drive through floodwaters. Turn Around, Don\'t Drown!®\n" +
                "3. Keep an emergency kit with water, non-perishable food, and a weather radio.\n" +
                "4. Avoid contact with floodwater; it may be contaminated.\n" +
                "5. Listen to authorities for information and instructions.",
        "Wildfire" to "1. Create a defensible space around your home by clearing flammable materials.\n" +
                "2. Have an evacuation plan and practice it.\n" +
                "3. Keep an emergency supply kit ready.\n" +
                "4. Monitor news and emergency alerts for wildfire information.\n" +
                "5. If advised to evacuate, do so immediately.",
        "Hurricane" to "1. Board up windows and secure outdoor items.\n" +
                "2. Have an evacuation plan and know your evacuation zone.\n" +
                "3. Stock up on water, non-perishable food, batteries, and first aid supplies.\n" +
                "4. Stay informed through official alerts and news.\n" +
                "5. If you are not evacuating, stay indoors away from windows.",
        "Tornado" to "1. Go to a basement, safe room, or an interior room on the lowest floor with no windows.\n" +
                "2. Get under something sturdy like a heavy table or workbench.\n" +
                "3. Cover your body with a blanket, sleeping bag, or mattress. Protect your head.\n" +
                "4. Stay informed via NOAA Weather Radio or local news.\n" +
                "5. Do not stay in a mobile home.",
        "Pandemic" to "1. Wash your hands frequently with soap and water for at least 20 seconds.\n" +
                "2. Avoid touching your eyes, nose, and mouth.\n" +
                "3. Practice social distancing and wear a mask in public if recommended.\n" +
                "4. Stay home if you are sick.\n" +
                "5. Keep informed by public health officials."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prep_tips)

        tvPrepTipsTitle = findViewById(R.id.tv_prep_tips_title)
        tvPrepTipsContent = findViewById(R.id.tv_prep_tips_content)

        val disasterType = intent.getStringExtra(EXTRA_DISASTER_TYPE)

        if (disasterType != null) {
            tvPrepTipsTitle.text = getString(R.string.prep_tips_title_format, disasterType) // Assumes string: <string name="prep_tips_title_format">%s Tips</string>
            tvPrepTipsContent.text = disasterTipsMap[disasterType] ?: getString(R.string.no_tips_available) // Assumes string: <string name="no_tips_available">No tips available for this disaster type.</string>
        } else {
            tvPrepTipsTitle.text = getString(R.string.disaster_prep_title) // Or some default
            tvPrepTipsContent.text = getString(R.string.error_no_disaster_type) // Assumes string: <string name="error_no_disaster_type">Error: Disaster type not specified.</string>
        }
    }
}
