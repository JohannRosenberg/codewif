package com.codewif.framework.da.local

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.codewif.framework.da.ServiceRepository
import com.codewif.framework.eventBus.EventBusController
import com.codewif.framework.models.RecyclerViewTestInfo
import com.codewif.framework.models.TestInfo
import com.codewif.framework.models.TestResult
import com.codewif.framework.models.TestResultsSummaryBase
import com.codewif.framework.testing.TestSetup
import com.codewif.framework.ui.main.CodewifMainActivity
import com.codewif.shared.App
import com.codewif.shared.eventBus.EventBusControllerBase
import com.codewif.shared.eventBus.EventBusTypes
import com.codewif.shared.service.models.UITestInfoBase
import com.codewif.shared.utils.security.PERMISSIONS_REQUEST_SD_READ_WRITE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * Provides storage and retrieval of test information including test setups and test results.
 */
open class TestRepository {

    companion object {
        const val TESTS_PER_PAGE = 50

        val testsSetups = mutableListOf<() -> TestSetup>()
        private val allTests = mutableListOf<TestInfo>()
        private val singleTest = mutableListOf<TestInfo>()
        var tests = allTests
        val testsDataSourceFactory = TestsDataSourceFactory()
        val testResultsDataSourceFactory = TestResultsDataSourceFactory()

        private var sortedTests = mutableListOf<Int>()
        private var sortingTests = false
        private var useSortedTestResults = false

        lateinit var projectId: String
        var appName: String? = ""
        var versionName: String? = ""
        var libraryPackageName: String? = ""
        var gitBranchName: String = ""
        var testingStartTime: Date? = null
        var testingEndTime: Date? = null

        @Suppress("UNUSED_PARAMETER")
        var useSingleTest: Boolean
            get() = singleTest.isNotEmpty()
            private set(value) {}


        /**
         * Returns true if any of the defined tests are not UI tests.
         */
        fun hasNonUITests(): Boolean {
            return allTests.any { !it.skipTest && !it.isUITest }
        }


        suspend fun doOnTestingCompleted() {
            testingEndTime = Date()
            sortTestResultsWithFailuresFirst()
            EventBusController.publishFinalTestResultsUpdated()
            storeFirstTimeSuccessfulUITests()
        }

        /**
         * Returns a summary of the test results.
         */
        fun getTestResultsSummary(summary: TestResultsSummaryBase) {
            summary.gitBranchName = gitBranchName

            if ((testingStartTime != null) && (testingEndTime != null))
                summary.duration = testingEndTime?.time as Long - testingStartTime?.time as Long

            summary.apply {
                if (useSingleTest) {
                    val testInfo = singleTest[0]
                    totalTested = 1
                    totalSucceeded = if ((testInfo.testResults != null) && (testInfo.testResults?.succeeded == true)) 1 else 0
                    totalFailed = if (totalSucceeded == 1) 0 else 1
                } else {
                    totalTested = allTests.filter { it.testResults != null }.size
                    totalSucceeded = allTests.filter { (it.testResults != null) && (it.testResults?.succeeded == true) }.size
                    totalFailed = allTests.filter { (it.testResults != null) && (it.testResults?.succeeded == false) }.size
                    totalSkipped = allTests.filter { it.skipTest }.size
                }
            }
        }

        fun initializeForSingleTest(testId: String) {
            singleTest.clear()
            singleTest.add(allTests.first { it.id == testId })
            tests = singleTest
        }

        fun initializeForAllTests() {
            useSortedTestResults = false
            singleTest.clear()
            tests = allTests
        }

        /**
         * Resets the state needed to run tests.
         */
        suspend fun initializeForTesting() {
            sortedTests.clear()
            sortingTests = false
            useSortedTestResults = false
            testingStartTime = Date()
            testingEndTime = null
            tests.forEach { it.testResults = null }
            getPreviousUITestResults()
            testResultsDataSourceFactory.sourceLiveData.value?.invalidate()
        }


        suspend fun onTestResultsUpdated() {
            withContext(Dispatchers.Main) {
                testResultsDataSourceFactory.sourceLiveData.value?.invalidate()
            }
        }

        /**
         * Sorts the list of tests placing the failed test at the top of the list. This is not actually a sort. The list of
         * tests does not get sorted. Instead, the indexes are sorted and stored in the sortedTests collection.
         */
        private fun sortTestResultsWithFailuresFirst() {
            sortingTests = true
            sortedTests.clear()
            var lastFailIndex = -1

            for ((index, testInfo) in tests.withIndex()) {
                if (testInfo.testResults?.succeeded == false) {
                    lastFailIndex++
                    sortedTests.add(lastFailIndex, index)
                } else {
                    sortedTests.add(index)
                }
            }

            sortingTests = false
            useSortedTestResults = true
        }

        /**
         * Returns a list of test items that are used on the test results screen.
         * @param startIndex The starting index
         * @param lastIndex The last index is included in the returned list.
         * @param items Upon returning, this list will be filled with items.
         */
        fun copyTestItemsForTestResults(startIndex: Int, lastIndex: Int, items: MutableList<RecyclerViewTestInfo>) {
            lateinit var testInfo: TestInfo

            for (i in startIndex..lastIndex) {
                testInfo = if (useSortedTestResults && !sortingTests) {
                    tests[sortedTests[i]]
                } else {
                    tests[i]
                }

                copyTestToRecyclerviewItem(testInfo, items)
            }
        }

        /**
         * Returns the sorted test results.
         */
        fun getSortedTestResults(items: MutableList<TestInfo>, includeAllTests: Boolean) {
            for (i in 0..tests.lastIndex) {
                val testInfo = tests[sortedTests[i]]

                if (includeAllTests || ((testInfo.testResults != null) && ((testInfo.testResults?.succeeded == false)))) {
                    items.add(testInfo)
                }
            }
        }

        fun copyTestItems(startIndex: Int, lastIndex: Int, items: MutableList<RecyclerViewTestInfo>) {
            for (i in startIndex..lastIndex) {
                copyTestToRecyclerviewItem(tests[i], items)
            }
        }

        private fun copyTestToRecyclerviewItem(testInfo: TestInfo, items: MutableList<RecyclerViewTestInfo>) {
            val rvNewItem = RecyclerViewTestInfo(
                testInfo.id,
                testInfo.testName,
                testInfo.testSource,
                testInfo.isUITest,
                testInfo.runSynchronously,
                testInfo.skipTest
            )
            rvNewItem.testIsRunning = testInfo.testIsRunning

            if (testInfo.testResults != null) {
                rvNewItem.testSucceeded = testInfo.testResults?.succeeded
            }

            items.add(rvNewItem)
        }


        /**
         * Adds one or more test setups to a collection.
         */
        suspend fun addTestSetups(vararg testSetups: () -> TestSetup): Companion {
            if (testsSetups.isNotEmpty())
                return TestRepository

            var uiTestsExist = false

            for (testSetup in testSetups) {
                testsSetups.add(testSetup)

                // IMPORTANT: Destroy the testSetup immediately after using it to avoid using up memory.
                val setup = testSetup()

                for (unitTestKeyVal in setup.tests) {
                    val unitTest = unitTestKeyVal.value

                    // Copy over the properties from the unit test.
                    val testInfo = TestInfo(
                        id = unitTest.id,
                        testName = unitTest.testName,
                        testSource = unitTest.testSource,
                        isUITest = unitTest.getUITestToRun() != null,
                        runSynchronously = unitTest.getTestToRunSync() !== null,
                        skipTest = unitTest.skipTest
                    )

                    allTests.add(testInfo)

                    if (testInfo.isUITest)
                        uiTestsExist = true
                }
            }

            if (uiTestsExist) {
                checkPermissionsAndServiceInstall()
            }

            return TestRepository
        }


        /**
         * Returns a list of all the previous ui tests that were run.
         */
        private suspend fun getPreviousUITestResults() {
            allTests.filter { it.isUITest }.forEach { it.uiTestInfoPrevious = null }

            if (allTests.any { testInfo -> testInfo.isUITest }) {
                val uiTestsPrev = ServiceRepository.getUITests()

                uiTestsPrev.forEach { uiTest ->
                    allTests.firstOrNull { testInfo ->
                        testInfo.id == uiTest.testId
                    }?.uiTestInfoPrevious = uiTest
                }
            }
        }


        /**
         * Copies the test result to the test that is stored in the tests collection.
         * @param testResult We store a copy of this data instead of the original. Test results originating from the
         * repository are the source of truth.
         */
        fun copyTestResult(testResult: TestResult): TestInfo {
            val testInfo: TestInfo = allTests.find { it.id == testResult.testId } as TestInfo
            testInfo.testResults = TestResult(testResult.succeeded, testResult.details)
            testInfo.testResults?.duration = testResult.duration

            return testInfo
        }

        /**
         * If a test is running, it's testIsRunning flag is set to false.
         */
        fun resetRunningTests() {
            allTests.filter { it.testIsRunning }.forEach { it.testIsRunning = false }
        }


        /**
         * Returns the test for the specified test id.
         */
        fun getTestById(testId: String): TestInfo = allTests.find { it.id == testId } as TestInfo


        /**
         * Enables or disables the skipping of a test.
         * @param skipTest If set to true, the test will be skipped.
         */
        fun skipTest(testId: String, skipTest: Boolean) {
            allTests.first { it.id == testId }.skipTest = skipTest
        }


        /**
         * Store any ui tests that have been done for the first time and were successful.
         */
        private suspend fun storeFirstTimeSuccessfulUITests() {
            val uiTests = mutableListOf<UITestInfoBase>()

            allTests.filter { testInfo ->
                testInfo.isUITest && (testInfo.uiTestInfoPrevious == null) && (testInfo.testResults?.succeeded == true) && (testInfo.testResults?.uiTestInfoCurrent != null)
            }.forEach {
                it.testResults?.uiTestInfoCurrent?.testId = it.id
                uiTests.add(it.testResults?.uiTestInfoCurrent as UITestInfoBase)
            }

            sendUITestToService(uiTests)
        }


        suspend fun storeUITest(testInfo: TestInfo) {
            val uiTests = mutableListOf<UITestInfoBase>()
            val uiTestInfo = testInfo.testResults?.uiTestInfoCurrent
            uiTests.add(uiTestInfo as UITestInfoBase)
            sendUITestToService(uiTests)
        }


        private suspend fun sendUITestToService(uiTests: List<UITestInfoBase>) {
            if (uiTests.isEmpty())
                return

            val uiTestsStored = ServiceRepository.storeUITests(uiTests)

            // The backend service will have updated the snapshot urls to include the latest image number appended to the
            // end of the filename (before the .png extension). Update all the snapshot urls that are local.

            uiTestsStored.forEach { uiTestStored ->
                allTests.find { testInfo ->
                    testInfo.id == uiTestStored.testId
                }?.testResults?.uiTestInfoCurrent?.snapshotUrl = uiTestStored.snapshotUrl
            }
        }


        suspend fun sendTestResultsToBackend(
            includeAllTests: Boolean = false,
            url: String? = null,
            requestHeaders: MutableMap<String, String>? = null
        ) {
            startCodewifService()
            val testResultsJSON = if (includeAllTests) Export.exportAllTestsToJson() else Export.exportFailedTestsToJson()
            ServiceRepository.sendTestResultsToBackend(testResultsJSON, url, requestHeaders)
        }


        /**
         * Starts the Codewif Service. This function should be called by any code in TestRunner where the service is needed.
         */
        suspend fun startCodewifService(): Boolean {
            return ServiceRepository.startCodewifService()
        }


        /**
         * If any UI tests are being performed, permission to store the webp images to the sd card is needed. Also, if any tests need the Codewif Service,
         * a check is made to see if it is installed and prompts the user to install it if it is not installed.
         */
        suspend fun checkPermissionsAndServiceInstall() {

            if (allTests.none { testInfo -> testInfo.isUITest } || !ServiceRepository.startCodewifService()) {
                return
            }

            if (ContextCompat.checkSelfPermission(
                    App.ctx,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (!App.isCurrentActivity<CodewifMainActivity>()) {
                    val intent = Intent(App.ctx, CodewifMainActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    ContextCompat.startActivity(App.ctx, intent, null)

                    waitTillActivityCreated()
                }

                ActivityCompat.requestPermissions(
                    App.currentActivity as Activity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_SD_READ_WRITE
                )

                waitTillPermissionsResponse()
            }
        }

        private suspend fun waitTillActivityCreated(): Unit = suspendCoroutine { cont ->
            EventBusControllerBase.subscribeToActivityCreated(this) {
                EventBusControllerBase.unsubscribeFromEvent(this, EventBusTypes.ACTIVITY_CREATED)
                cont.resume(Unit)
            }
        }

        private suspend fun waitTillPermissionsResponse(): Unit = suspendCoroutine { cont ->
            EventBusControllerBase.subscribeToPermissionsResponse(this) {
                EventBusControllerBase.unsubscribeFromEvent(this, EventBusTypes.PERMISSIONS_RESPONSE)
                cont.resume(Unit)
            }
        }
    }
}