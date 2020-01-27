package com.codewif.framework.testing

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.codewif.framework.da.local.Export
import com.codewif.framework.da.local.TestRepository
import com.codewif.framework.errorHandling.MissingSetupDataException
import com.codewif.framework.errorHandling.NoTestDefinedException
import com.codewif.framework.eventBus.EventBusController
import com.codewif.framework.logging.LOG_TAG
import com.codewif.framework.models.TestInfo
import com.codewif.framework.models.TestResult
import com.codewif.framework.ui.ActivityConstants
import com.codewif.framework.ui.main.CodewifMainActivity
import com.codewif.service.logging.CreatingFileException
import com.codewif.shared.App
import com.codewif.shared.eventBus.EventBusControllerBase
import com.codewif.shared.eventBus.EventBusTypes
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * Used to run one or more tests.
 */
open class TestRunner {

    // Dispatchers.IO was chosen as the default in the event that some coroutine gets called and what that coroutine function
    // does is not clearly UI, IO or CPU oriented (maybe it's a mixture). Using Dispatchers.IO allows for the worst case by
    // providing the most robust threading model.
    companion object : CoroutineScope by CoroutineScope(Dispatchers.IO) {

        /**
         * If set to true, testing will be terminated when the first test fails.
         * Any asynchronous tests that are still executing at the time of the failure will continue until they have been
         * completed.
         */
        private var terminateOnFirstFailedTest = false

        private lateinit var appContext: Context

        @Volatile
        private var runningTests = false
        private var testFailed = false
        private lateinit var jobRunner: Job
        private var closeActivityWhenRunningTests = false
        private var showActivityAfterTestingCompletes = false
        private var activityVisibleBeforeTesting = false
        private var postTestingExecuted = false
        private var sendTestResultsToBackend = false
        private var includeAllTestsSentToBackend = false
        private lateinit var onTestingCompletedListener: (succeeded: Boolean) -> Unit

        private var backendUrl: String? = null
        private var backendRequestHeaders: MutableMap<String, String>? = null

        /**
         * The name of the git branch that is being used to run tests.
         */
        var gitBranchName: String
            get() = TestRepository.gitBranchName
            set(value) {
                TestRepository.gitBranchName = value
            }

        var testsAreRunning: Boolean
            get() {
                return runningTests || TestRepository.tests.any { testInfo -> testInfo.testIsRunning }
            }
            private set(value) {}

        /**
         * Sets the context used by the app that is being tested. This is mandatory and should be the first thing the
         * client does.
         */
        fun setAppContext(context: Context): Companion {
            appContext = context
            TestRepository.projectId = context.applicationInfo.packageName
            return TestRunner
        }

        /**
         * Sets the project id that uniquely identifies the app being tested. This is required and should be called by the client
         * after calling setAppContext. The id should be something that remains unchanged for the life of your app. Avoid using
         * things like the application id of your app or some package name, which could potentially change over time. If your
         * project id ever changes, any test settings and test results that were associated with the previous project id will not
         * be available for the updated project id. If you're using Jira to manage your project, you might want to use the
         * Jira project name as the id.
         */
        fun setProjectId(projectId: String): Companion {
            TestRepository.projectId = projectId
            return TestRunner
        }

        /**
         * Sets the name of the app being tested. This is required if the app being tested is an application and not an
         * Android library.
         */
        fun setAppName(): Companion {
            TestRepository.appName = App.getApplicationName(appContext)
            return TestRunner
        }

        /**
         * Sets the package name of the library being tested. This is required if the app being tested is an Android library
         */
        fun setLibraryPackageName(libraryPackageName: String): Companion {
            TestRepository.libraryPackageName = libraryPackageName
            return TestRunner
        }


        /**
         * This is the versionName property that is set in the app's build.gradle file.
         */
        fun setVersionName(versionName: String): Companion {
            TestRepository.versionName = versionName
            return TestRunner
        }

        /**
         * The name of the git branch that is being used to run tests. This is required.
         */
        fun setGitBranchName(branchName: String): Companion {
            TestRepository.gitBranchName = branchName
            return TestRunner
        }

        /**
         * Sets the callback that will get called when testing completes.
         */
        fun setOnTestingCompletedListener(callback: (succeeded: Boolean) -> Unit): Companion {
            onTestingCompletedListener = callback
            return TestRunner
        }

        /**
         * Causes testing to terminate whenever the first test fails.
         */
        fun terminateTestingOnFirstFailure(): Companion {
            terminateOnFirstFailedTest = true
            return TestRunner
        }

        /**
         * Cancels testing.
         */
        fun cancelTesting(): Companion {
            Log.i(LOG_TAG, "Testing Canceled")
            cancel()
            EventBusController.publishTestingStateChange(false)
            return TestRunner
        }

        /**
         * Closes Codewif's UI before testing begins.
         */
        fun closeUIWhenTesting(): Companion {
            closeActivityWhenRunningTests = true
            return TestRunner
        }

        /**
         * Shows the tests results screen after testing has completed.
         */
        fun showTestResultsAfterTesting(): Companion {
            showActivityAfterTestingCompletes = true
            return TestRunner
        }

        /**
         * Exports failed tests with their test results to JSON.
         */
        fun exportFailedTestsToJson(): String {
            return Export.exportFailedTestsToJson()
        }

        /**
         * Exports all tests along with their test results to JSON.
         */
        fun exportAllTestsToJson(): String {
            return Export.exportAllTestsToJson()
        }

        /**
         * Displays the tests screen.
         */
        fun displayTests(): Companion {
            val intent = Intent(App.ctx, CodewifMainActivity::class.java)
            ContextCompat.startActivity(App.ctx, intent, null)
            return TestRunner
        }

        /**
         * Displays the test results screen.
         */
        fun displayTestResults(): Companion {
            val intent = Intent(App.ctx, CodewifMainActivity::class.java)
            intent.putExtra(ActivityConstants.BUNDLE_PARAM_NAVIGATE_TO_TEST_RESULTS_SCREEN, true)
            displayUI(intent)

            return TestRunner
        }


        /**
         * Sends the test results to the backend.
         * @param includeAllTests If set to true, the test results for all tests are included in the data sent to the backend.
         * @param url The url of the backend that the test results will be posted to. If not specified, Codewif's own backend will be used if account
         * settings have been provided.
         * @param requestHeaders If a url is provided, request headers can also be specified. This map contains pairs of header names for the keys and
         * the header values for the map values.
         */
        suspend fun sendTestResultsToBackend(
            includeAllTests: Boolean = false,
            url: String? = null,
            requestHeaders: MutableMap<String, String>? = null
        ): Companion {
            TestRepository.startCodewifService()

            sendTestResultsToBackend = true
            includeAllTestsSentToBackend = includeAllTests
            backendUrl = url
            backendRequestHeaders = requestHeaders
            return TestRunner
        }


        suspend fun addTestSetups(vararg testSetups: () -> TestSetup): Companion {
            TestRepository.addTestSetups(*testSetups)
            return TestRunner
        }


        /**
         * Runs all the tests defined in each test setup.
         */
        suspend fun runTests(): Companion {

            TestRepository.checkPermissionsAndServiceInstall()

            // Make sure mandatory stuff is setup before testing.
            if (TestRepository.projectId.isEmpty())
                throw MissingSetupDataException("The project id is not set. Make sure to call setProjectId.")

            if (!::appContext.isInitialized)
                throw MissingSetupDataException("The app's context is not set. Make sure to call setAppContext. This should be the first thing called when setting up a test.")

            if (TestRepository.appName.isNullOrEmpty() && TestRepository.libraryPackageName.isNullOrEmpty())
                throw MissingSetupDataException("The app name or library package name must be set before testing. If you are testing an app, call setAppName. If testing an Android library, call setLibraryPackageName.")

            if (TestRepository.versionName.isNullOrEmpty())
                throw MissingSetupDataException("The version name is not set. Make sure to call setVersionName.")

            if (TestRepository.gitBranchName.isEmpty())
                throw MissingSetupDataException("The Git branch name is not set. Make sure to call setGitBranchName. If you are using a different version control tool other than Git, specifiy the branch name here. If you are not using any version control, just assign any meaningful name.")

            jobRunner = launch {
                activityVisibleBeforeTesting = App.isCurrentActivity<CodewifMainActivity>()

                if (closeActivityWhenRunningTests && App.isCurrentActivity<CodewifMainActivity>()) {
                    App.closeActivity<CodewifMainActivity>()
                    waitTillActivityDestroyed()
                }

                Log.i(LOG_TAG, "Git Branch Name: $gitBranchName")
                Log.i(LOG_TAG, "Testing started")
                runningTests = true
                testFailed = false
                postTestingExecuted = false
                TestRepository.initializeForTesting()
                EventBusController.publishTestingStateChange(true)

                loop@ for (testSetup in TestRepository.testsSetups) {
                    val setup = testSetup()

                    for (unitTestSetup in setup.tests) {

                        if (testFailed && terminateOnFirstFailedTest) {
                            break@loop
                        }

                        val unitTest = unitTestSetup.value
                        val testInfo = TestRepository.getTestById(unitTest.id)

                        if ((TestRepository.useSingleTest && testInfo.id != TestRepository.tests[0].id) ||
                            (testInfo.skipTest && !TestRepository.useSingleTest)
                        ) {
                            continue
                        }

                        testInfo.testIsRunning = true
                        TestRepository.onTestResultsUpdated()

                        Log.i(
                            LOG_TAG,
                            "Unit Test Started; testName: ${testInfo.testName}; testSource: ${testInfo.testSource}; runSynchronously: ${testInfo.runSynchronously}"
                        )

                        when {
                            unitTest.getTestToRunAsync() != null -> {
                                async {
                                    lateinit var testResult: TestResult
                                    val startTime = Date()

                                    try {
                                        withContext(Dispatchers.IO) {
                                            unitTest.getTestToRunAsync()?.invoke { result ->
                                                testResult = result

                                                runBlocking {
                                                    doOnTestCompleted(testInfo, testResult, startTime, Date())
                                                }
                                            }
                                        }
                                    } catch (exception: Exception) {
                                        testResult = TestResult("Exception: ${exception.message}")
                                    }
                                }
                            }
                            unitTest.getTestToRunSync() != null -> {
                                lateinit var testResult: TestResult
                                val startTime = Date()

                                try {
                                    withContext(Dispatchers.IO) {
                                        testResult = unitTest.getTestToRunSync()?.invoke() as TestResult
                                    }
                                } catch (exception: Exception) {
                                    testResult = TestResult("Exception: ${exception.message}")
                                } finally {
                                    doOnTestCompleted(testInfo, testResult, startTime, Date())
                                }
                            }
                            unitTest.getUITestToRun() != null -> {
                                lateinit var testResult: TestResult
                                val startTime = Date()
                                var terminate = false

                                try {
                                    withContext(Dispatchers.Main) {
                                        unitTest.getUITestToRun()?.invoke()
                                        testResult = UITester.testUI(testInfo)
                                    }
                                } catch (exception: CreatingFileException) {
                                    terminate = true
                                    jobRunner.cancel()
                                    throw exception
                                } catch (exception: Exception) {
                                    testResult = TestResult("Exception: ${exception.message}")
                                } finally {
                                    // No need to copy the test results since UITester.testUI already stored the result.
                                    if (!terminate)
                                        doOnTestCompleted(testInfo, testResult, startTime, Date(), false)
                                }
                            }
                            else -> {
                                throw NoTestDefinedException("${testInfo.testName}, ${unitTestSetup.key}")
                            }
                        }

                        if (TestRepository.useSingleTest)
                            break@loop
                    }
                }

                runningTests = false

                if (!testsAreRunning && !postTestingExecuted) {
                    doAfterAllTestsCompleted()
                }
            }

            return TestRunner
        }

        private fun logTestResult(testInfo: TestInfo, testResult: TestResult) {
            var message =
                " testName: ${testInfo.testName}; testSource: ${testInfo.testSource}; runSynchronously: ${testInfo.runSynchronously}"

            if (!testResult.details.isNullOrEmpty())
                message += "; details: ${testResult.details}"

            if (testResult.succeeded) {
                Log.i(LOG_TAG, "Unit Test Succeeded; $message")
            } else {
                Log.e(LOG_TAG, "Unit Test Failed; $message")
            }
        }

        private suspend fun doOnTestCompleted(
            testInfo: TestInfo,
            testResult: TestResult,
            startTime: Date,
            endTime: Date,
            copyTestResults: Boolean = true
        ) {
            logTestResult(testInfo, testResult)

            testResult.apply {
                testId = testInfo.id
                duration = endTime.time - startTime.time
            }

            if (copyTestResults)
                TestRepository.copyTestResult(testResult)

            testInfo.testIsRunning = false
            TestRepository.onTestResultsUpdated()

            if (!testResult.succeeded) {
                testFailed = true

                if (terminateOnFirstFailedTest) {
                    cancel()
                }
            }

            if (!testsAreRunning || TestRepository.useSingleTest) {
                doAfterAllTestsCompleted()
            }
        }


        private suspend fun doAfterAllTestsCompleted() {
            TestRepository.doOnTestingCompleted()

            if (sendTestResultsToBackend) {
                TestRepository.sendTestResultsToBackend(includeAllTestsSentToBackend, backendUrl, backendRequestHeaders)
            }

            if (activityVisibleBeforeTesting || showActivityAfterTestingCompletes) {
                displayTestResults()
            }

            if (::onTestingCompletedListener.isInitialized)
                onTestingCompletedListener(!testFailed)

            EventBusController.publishTestingStateChange(false)
            postTestingExecuted = true
        }


        private fun displayUI(intent: Intent): Companion {
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

            ContextCompat.startActivity(App.ctx, intent, null)
            return TestRunner
        }


        private fun cancel() {
            runningTests = false
            jobRunner.cancel()
            TestRepository.resetRunningTests()
        }


        private suspend fun waitTillActivityDestroyed(): Unit = suspendCoroutine { cont ->
            EventBusControllerBase.subscribeToActivityDestroyed(this) { activityDestroyed ->
                if (activityDestroyed is CodewifMainActivity) {
                    EventBusControllerBase.unsubscribeFromEvent(this, EventBusTypes.ACTIVITY_DESTROYED)
                    cont.resume(Unit)
                }
            }
        }
    }
}
