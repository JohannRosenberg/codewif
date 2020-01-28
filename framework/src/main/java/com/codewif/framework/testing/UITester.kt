package com.codewif.framework.testing

import android.graphics.Bitmap
import com.codewif.framework.da.local.TestRepository
import com.codewif.framework.models.TestInfo
import com.codewif.framework.models.TestResult
import com.codewif.shared.App.Companion.currentActivity
import com.codewif.shared.service.models.UITestInfoBase
import com.codewif.shared.utils.FileUtils
import com.jraska.falcon.Falcon
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


/**
 * Provides support to test user interfaces.
 */
class UITester {
    companion object {
        fun testUI(testInfo: TestInfo): TestResult {

            val testResults = TestResult()
            testInfo.testResults = testResults
            testInfo.testResults?.uiTestInfoCurrent = UITestInfoBase()
            testInfo.testResults?.uiTestInfoCurrent?.testId = testInfo.id

            // Create a snapshot. The width and height is half the screen height to save on disk space as well as the amount of
            // memory needed to load the snapshot into imageviews.

            var bitmap = Falcon.takeScreenshotBitmap(currentActivity)
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width / 2, bitmap.height / 2, false)

            val bufferOut = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.WEBP, 80, bufferOut)

            val byteData = bufferOut.toByteArray()
            testResults.uiTestInfoCurrent?.hashcode = Arrays.hashCode(byteData)

            when {
                testInfo.uiTestInfoPrevious == null -> {
                    testResults.succeeded = true
                    saveUITestImageToDisk(testInfo, bufferOut)

                }
                testInfo.uiTestInfoPrevious?.hashcode == testResults.uiTestInfoCurrent?.hashcode -> {
                    testResults.succeeded = true
                    testResults.uiTestInfoCurrent?.snapshotUrl = testInfo.uiTestInfoPrevious?.snapshotUrl

                    // Even though the test passed, it is possible that the snapshot got deleted from disk.
                    // If it doesn't exist, store it.

                    if (!File(testInfo.uiTestInfoPrevious?.snapshotUrl).exists()) {
                        testInfo.uiTestInfoPrevious?.snapshotUrl?.let { filename ->
                            val fileUtils = FileUtils()
                            fileUtils.storeDataToDisk(TestRepository.projectId, bufferOut, File(filename))
                        }
                    }
                }
                else -> {
                    saveUITestImageToDisk(testInfo, bufferOut)
                }
            }

            return testResults
        }


        private fun saveUITestImageToDisk(testInfo: TestInfo, bufferOut: ByteArrayOutputStream) {
            val fileUtils = FileUtils()
            testInfo.testResults?.uiTestInfoCurrent?.snapshotUrl =
                fileUtils.saveUITestImageToDisk(bufferOut, TestRepository.projectId, testInfo.id)
        }
    }
}