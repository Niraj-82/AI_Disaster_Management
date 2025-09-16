package com.example.resqai

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class FirstAidDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_aid_detail)

        val topicTitle = intent.getStringExtra("TOPIC_TITLE") ?: getString(R.string.first_aid_detail_title_default)
        val topicDetail = intent.getStringExtra("TOPIC_DETAIL") ?: getString(R.string.first_aid_detail_content_unavailable)

        title = topicTitle // Sets the activity title

        val tvDetailTitle: TextView = findViewById(R.id.tv_first_aid_detail_title)
        val tvDetailContent: TextView = findViewById(R.id.tv_first_aid_detail_content)

        tvDetailTitle.text = topicTitle
        tvDetailContent.text = topicDetail
    }
}
