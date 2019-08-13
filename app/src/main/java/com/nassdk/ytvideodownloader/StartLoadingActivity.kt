package com.nassdk.ytvideodownloader

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class StartLoadingActivity : AppCompatActivity() {

    private val LOADING_SCREEN_SHOW_TIME = 1500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_loading)

        Handler().postDelayed({

            val intent = Intent(this@StartLoadingActivity, MainActivity::class.java)
            startActivity(intent)
            finish()

        }, LOADING_SCREEN_SHOW_TIME.toLong())
    }
}