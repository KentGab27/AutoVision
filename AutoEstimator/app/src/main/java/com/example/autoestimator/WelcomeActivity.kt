package com.example.autoestimator

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // Open Camera screen
        findViewById<MaterialButton>(R.id.btnCamera).setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }

        // Open Gallery directly
        findViewById<MaterialButton>(R.id.btnUpload).setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java).putExtra("openGallery", true))
        }
    }
}
