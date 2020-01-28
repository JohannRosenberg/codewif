package com.codewif.testing.uitests.codewif

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
                    .setProjectId("Codewif Framework Library")
                    .setLibraryPackageName(com.codewif.framework.BuildConfig.LIBRARY_PACKAGE_NAME)
                    .setVersionName(com.codewif.framework.BuildConfig.VERSION_NAME)
                    .setGitBranchName("ui_tests")
                    .addTestSetups(::UITests)
                    .showTestResultsAfterTesting()
                    .runTests()
            }
        }
    }
}