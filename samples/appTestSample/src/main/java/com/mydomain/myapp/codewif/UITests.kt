package com.mydomain.myapp.codewif

import android.content.Intent
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.codewif.framework.models.UnitTest
import com.codewif.framework.testing.TestRunner
import com.codewif.framework.testing.TestSetup
import com.codewif.sample.R
import com.mydomain.myapp.ui.MainActivity
import kotlinx.coroutines.delay

class UITests : TestSetup() {

    init {
        addTest(UnitTest("Main Activity at Start").uiTestToRun {
            val intent = Intent(TestRunner.getAppContext(), MainActivity::class.java)
            ContextCompat.startActivity(TestRunner.getAppContext(), intent, null)
            delay(500)
        })

        addTest(UnitTest("Sign in failed").uiTestToRun {
            TestRunner.getCurrentActivity()?.findViewById<TextView>(R.id.et_username)?.text = "somedude"
            TestRunner.getCurrentActivity()?.findViewById<TextView>(R.id.et_password)?.text = "abracadabra"
            TestRunner.getCurrentActivity()?.findViewById<Button>(R.id.btn_signin)?.performClick()
            delay(500)
        })

        addTest(UnitTest("Account Settings Screen").uiTestToRun {
            (TestRunner.getCurrentActivity() as MainActivity).alertDialogSignIn.dismiss()
            TestRunner.getCurrentActivity()?.findViewById<TextView>(R.id.et_username)?.text = "john"
            TestRunner.getCurrentActivity()?.findViewById<TextView>(R.id.et_password)?.text = "123456"
            TestRunner.getCurrentActivity()?.findViewById<Button>(R.id.btn_signin)?.performClick()
            delay(500)
        })
    }
}