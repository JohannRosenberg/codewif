package com.codewif.testing.uitests.codewif

import com.codewif.framework.models.UnitTest
import com.codewif.framework.testing.TestRunner
import com.codewif.framework.testing.TestSetup
import com.codewif.framework.ui.main.CodewifMainActivity
import com.codewif.testing.uitests.UITestController
import kotlinx.coroutines.delay

class UITests : TestSetup() {
    init {
        addTest(UnitTest("Tests Screen").uiTestToRun {
            if (TestRunner.getCurrentActivity() is CodewifMainActivity) {
                UITestController.displayTestsScreen()
            } else {
                TestRunner.displayTests()
            }

            delay(5000)
        })
    }
}