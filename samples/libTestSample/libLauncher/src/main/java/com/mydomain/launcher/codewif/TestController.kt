package com.mydomain.launcher.codewif

import android.content.Context
import com.codewif.framework.testing.TestRunner
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


/**
 * This is where you configure the test runner.
 */
object TestController {
    fun runTests(context: Context) {

        val mainScope = MainScope()

        // NOTE: For the setLibraryPackageName and setVersionName methods, prefix the BuildConfig with the package name of your
        // library. If you leave this out, BuildConfig may inadvertently refer to the package of the module used to launch the activity
        // and the code below will not compile because LIBRARY_PACKAGE_NAME can only be referenced from a library module.

        mainScope.launch {
            TestRunner
                .setAppContext(context)
                .setProjectId("My Cool Library")
                .setLibraryPackageName(com.mydomain.mylib.BuildConfig.LIBRARY_PACKAGE_NAME)
                .setVersionName(com.mydomain.mylib.BuildConfig.VERSION_NAME)
                .setGitBranchName("unit_tests")
                .setOnTestingCompletedListener { succeeded ->
                    val jsonFailed = TestRunner.exportFailedTestsToJson()
                    // Do something with the JSON data.
                }
                .displayTestResults()
                .addTestSetups(::StringUnitTests)
                .runTests()
        }
    }
}
