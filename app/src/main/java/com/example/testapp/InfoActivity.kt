package com.example.testapp

import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class InfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        findViewById<TextView>(R.id.textViewBrandInfo).text = Build.BRAND
        findViewById<TextView>(R.id.textViewModelInfo).text = Build.MODEL
        findViewById<TextView>(R.id.textViewOSInfo).text = Build.VERSION.RELEASE
    }
}