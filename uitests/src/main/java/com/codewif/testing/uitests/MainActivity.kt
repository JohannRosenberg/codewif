package com.codewif.testing.uitests

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codewif.testing.uitests.codewif.TestController

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Start testing automatically
        TestController.runTests(this)

        finish()
    }
}
