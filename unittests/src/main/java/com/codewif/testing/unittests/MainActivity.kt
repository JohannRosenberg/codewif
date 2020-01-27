package com.codewif.testing.unittests

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codewif.testing.unittests.codewif.TestController

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        TestController.runTests(this)

        finish()
    }
}
