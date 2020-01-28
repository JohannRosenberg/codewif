package com.mydomain.myapp.codewif

import android.content.Context
import com.codewif.framework.testing.TestRunner
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * This is where you configure the test runner.
 */
class TestController {
    companion object {
        private var initialized = false

        fun runTests(context: Context) {

            if (initialized)
                return

            initialized = true
            val mainScope = MainScope()

            mainScope.launch {
                TestRunner
                    .setAppContext(context)
                    .setProjectId("Codewif Sample App")
                    .setAppName()
                    .setVersionName(com.codewif.sample.BuildConfig.VERSION_NAME)
                    .setGitBranchName("unit_tests")
                    .setOnTestingCompletedListener { succeeded ->
                        val jsonFailed = TestRunner.exportFailedTestsToJson()
                        // Do something with the JSON data.
                    }
                    .sendTestResultsToBackend(url = "https://hookb.in/dmPG11m3DBFGDEK1Ym7l")
                    //.closeUIWhenTesting()
                    //.displayTestResults()
                    .showTestResultsAfterTesting()
                    .addTestSetups(::UITests, ::MathUnitTests, ::StringUnitTests)
                    .runTests()
            }
        }
    }
}