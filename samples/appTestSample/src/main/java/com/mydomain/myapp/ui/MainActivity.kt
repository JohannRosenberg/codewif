package com.mydomain.myapp.ui

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.codewif.sample.R
import com.mydomain.myapp.codewif.TestController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ctx = this

        // Start testing automatically
        TestController.runTests(ctx)

        // Alternatively, start testing by clicking on some label.
        textview_title.setOnClickListener {
            TestController.runTests(ctx)
        }

        btn_signin.setOnClickListener {
            val alertDialog: AlertDialog
            val builder = AlertDialog.Builder(ctx)

            builder.setMessage("You have successfully signed in.")
                .setPositiveButton(com.codewif.framework.R.string.cw_ok) { _, _ ->

                }

            alertDialog = builder.show()
            alertDialog.setCancelable(false)
        }
    }
}
