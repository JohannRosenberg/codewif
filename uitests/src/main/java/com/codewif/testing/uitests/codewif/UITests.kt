package com.codewif.testing.uitests.codewif

import android.view.Gravity
import androidx.drawerlayout.widget.DrawerLayout
import com.codewif.framework.R
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

            delay(600)
        })

        addTest(UnitTest("Dummy test to skip", skipTest = true).uiTestToRun {
        })

        addTest(UnitTest("Open navigation drawer").uiTestToRun {
            TestRunner.getCurrentActivity()?.findViewById<DrawerLayout>(R.id.drawer_layout)?.openDrawer(Gravity.LEFT)
            delay(600)
        })

        addTest(UnitTest("Close navigation drawer").uiTestToRun {
            TestRunner.getCurrentActivity()?.findViewById<DrawerLayout>(R.id.drawer_layout)?.closeDrawer(Gravity.LEFT)
            delay(600)
        })

/*        addTest(UnitTest("Dummy test 1").uiTestToRun {
            delay(600)
        })

        addTest(UnitTest("Dummy test 2").uiTestToRun {
            delay(600)
        })

        addTest(UnitTest("Dummy test 3").uiTestToRun {
            delay(600)
        })*/
    }
}