package com.codewif.testing.uitests.codewif

import android.view.Gravity
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
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

            delay(1000)
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

        addTest(UnitTest("Test results screen").uiTestToRun {
            TestRunner.displayTestResults()
            delay(500)
            //delay(1000)
        })

        addTest(UnitTest("Test details screen").uiTestToRun {
            val rvTests = TestRunner.getCurrentActivity()?.findViewById<RecyclerView>(R.id.rv_tests)
            rvTests?.findViewHolderForAdapterPosition(0)?.itemView?.performClick()

            delay(1000)
            TestRunner.getCurrentActivity()?.findViewById<TextView>(R.id.tv_duration)?.text = "00:3.456"

            delay(1000)
        })
    }
}