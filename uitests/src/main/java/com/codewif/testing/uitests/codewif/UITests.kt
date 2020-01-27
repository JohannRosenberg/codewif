package com.codewif.testing.uitests.codewif

import com.codewif.framework.models.UnitTest
import com.codewif.framework.testing.TestSetup
import kotlinx.coroutines.delay

class UITests : TestSetup() {
    init {
        addTest(UnitTest("Main Activity").uiTestToRun {

            delay(200)
        })
    }
}