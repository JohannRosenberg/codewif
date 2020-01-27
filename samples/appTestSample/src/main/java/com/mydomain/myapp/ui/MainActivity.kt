package com.mydomain.myapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codewif.sample.R
import com.mydomain.myapp.codewif.TestController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Start testing automatically
        TestController.runTests(this)

        // Alternatively, start testing by clicking on some label.
        textview_title.setOnClickListener {
            TestController.runTests(this)
        }
    }
}
