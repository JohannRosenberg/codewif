package com.codewif.testing.uitests.codewif

import com.codewif.framework.models.UnitTest
import com.codewif.framework.testing.TestSetup
import com.codewif.testing.uitests.UITestController
import kotlinx.coroutines.delay

class UITests : TestSetup() {
    init {
        addTest(UnitTest("Tests Screen").uiTestToRun {
            UITestController.displayTestsScreen()
            delay(800)
        })
    }
}