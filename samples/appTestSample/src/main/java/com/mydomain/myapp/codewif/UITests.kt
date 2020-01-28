package com.mydomain.myapp.codewif

import android.widget.Button
import com.codewif.framework.models.UnitTest
import com.codewif.framework.testing.TestRunner
import com.codewif.framework.testing.TestSetup
import com.codewif.sample.R
import kotlinx.coroutines.delay

class UITests : TestSetup() {

    init {
        addTest(UnitTest("Main Activity").uiTestToRun {

            delay(1000)
        })

        addTest(UnitTest("Sign in button pressed").uiTestToRun {
            TestRunner.getCurrentActivity()?.findViewById<Button>(R.id.btn_signin)?.performClick()
            delay(1000)
        })
    }
}