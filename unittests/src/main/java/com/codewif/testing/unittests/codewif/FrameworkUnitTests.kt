package com.codewif.testing.unittests.codewif

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.codewif.framework.da.local.TestRepository
import com.codewif.framework.eventBus.EventBusController
import com.codewif.framework.models.TestResult
import com.codewif.framework.models.UnitTest
import com.codewif.framework.testing.TestSetup
import com.codewif.shared.App
import com.codewif.shared.eventBus.EventBusControllerBase
import com.codewif.shared.eventBus.EventBusTypes
import com.codewif.shared.utils.FileUtils
import com.codewif.testing.unittests.R
import java.io.ByteArrayOutputStream


/**
 * This is where tests are configured.
 */
class FrameworkUnitTests : TestSetup() {
    init {
        val ctx = this

        addTest(UnitTest(testName = "Store cat-1 to cache").testToRunSync {
            val testResult = TestResult()

            val id: Int = R.drawable.cat_1
            val bitmap: Bitmap = BitmapFactory.decodeResource(App.ctx.resources, id)

            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.WEBP, 100, stream)
            //val byteArray: ByteArray = stream.toByteArray()

            val fileUtils = FileUtils()
            fileUtils.saveUITestImageToDisk(stream, TestRepository.projectId, "Store cat-1 to cache")

            testResult.succeeded = true
            testResult
        })

        addTest(UnitTest("subscribeToTestingStateChange").testToRunAsync { callback ->
            val testResult = TestResult()

            EventBusController.subscribeToTestingStateChange(this) { runningTests ->
                EventBusControllerBase.unsubscribeFromEvent(ctx, EventBusTypes.TESTING_STATE_CHANGE)
                testResult.succeeded = runningTests
                callback.invoke(testResult)
            }

            EventBusController.publishTestingStateChange(true)
        })

        addTest(UnitTest("subscribeToFinalTestResultsUpdated").testToRunAsync { callback ->
            val testResult = TestResult()

            EventBusController.subscribeToFinalTestResultsUpdated(this) {
                EventBusControllerBase.unsubscribeFromEvent(ctx, EventBusTypes.TEST_RESULTS_UPDATED)
                testResult.succeeded = true
                callback.invoke(testResult)
            }

            EventBusController.publishFinalTestResultsUpdated()
        })

        addTest(UnitTest("subscribeToPermissionsResponse").testToRunAsync { callback ->
            val testResult = TestResult()

            EventBusControllerBase.subscribeToPermissionsResponse(this) {
                EventBusControllerBase.unsubscribeFromEvent(ctx, EventBusTypes.PERMISSIONS_RESPONSE)
                testResult.succeeded = true
                callback.invoke(testResult)
            }

            EventBusControllerBase.publishPermissionsResponse()
        })

        addTest(UnitTest("subscribeToActivityCreated").testToRunAsync { callback ->
            val testResult = TestResult()

            EventBusControllerBase.subscribeToActivityCreated(this) {
                EventBusControllerBase.unsubscribeFromEvent(ctx, EventBusTypes.ACTIVITY_CREATED)
                testResult.succeeded = true
                callback.invoke(testResult)
            }

            EventBusControllerBase.publishActivityCreated(Unit)
        })

        addTest(UnitTest("subscribeToActivityResumed").testToRunAsync { callback ->
            val testResult = TestResult()

            EventBusControllerBase.subscribeToActivityResumed(this) {
                EventBusControllerBase.unsubscribeFromEvent(ctx, EventBusTypes.ACTIVITY_RESUMED)
                testResult.succeeded = true
                callback.invoke(testResult)
            }

            EventBusControllerBase.publishActivityResumed(Unit)
        })

        addTest(UnitTest("subscribeToActivityDestroyed").testToRunAsync { callback ->
            val testResult = TestResult()

            EventBusControllerBase.subscribeToActivityDestroyed(this) {
                EventBusControllerBase.unsubscribeFromEvent(ctx, EventBusTypes.ACTIVITY_DESTROYED)
                testResult.succeeded = true
                callback.invoke(testResult)
            }

            EventBusControllerBase.publishActivityDestroyed(Unit)
        })
    }
}