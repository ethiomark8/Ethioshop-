package com.ethio.shop.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.ethio.shop.R

class SplashActivity : AppCompatActivity() {
    
    private val splashDelay: Long = 2000 // 2 seconds
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        
        // Navigate to MainActivity after delay
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToMain()
        }, splashDelay)
    }
    
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}