package com.mydomain.myapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.codewif.sample.R
import com.mydomain.myapp.codewif.TestController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var alertDialogSignIn: AlertDialog

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
            // Fake testing the username and password. In a real app, this is tested on the backend.

            val username = et_username.text.toString()
            val password = et_password.text.toString()

            if ((username != "john") || (password != "123456")) {
                val builder = AlertDialog.Builder(ctx)

                builder.setMessage("Incorrect username and/or password")
                    .setPositiveButton(com.codewif.framework.R.string.cw_ok) { _, _ ->
                    }

                alertDialogSignIn = builder.show()
                alertDialogSignIn.setCancelable(false)
            } else {
                val intent = Intent(ctx, AccountsActivity::class.java)
                ContextCompat.startActivity(ctx, intent, null)
            }
        }
    }
}
