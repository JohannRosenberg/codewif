package com.codewif.testing.unittests.codewif

import com.codewif.framework.eventBus.EventBusController
import com.codewif.framework.models.TestResult
import com.codewif.framework.models.UnitTest
import com.codewif.framework.testing.TestSetup
import com.codewif.shared.eventBus.EventBusControllerBase
import com.codewif.shared.eventBus.EventBusTypes

/**
 * This is where tests are configured.
 */
class FrameworkhUnitTests : TestSetup() {
    init {
        val ctx = this

/*        addTest(UnitTest("Number is divisible by 4").testToRunSync {
            val testResult = TestResult()



            //testResult.succeeded = MathUtis.divisibleByFour(18)
            testResult
        })*/

        addTest(UnitTest("subscribeToTestingStateChange").testToRunAsync { callback ->
            val testResult = TestResult()

            EventBusController.subscribeToTestingStateChange(this) { runningTests ->
                EventBusControllerBase.unsubscribeFromEvent(ctx, EventBusTypes.TESTING_STATE_CHANGE)
                testResult.succeeded = runningTests
                callback.invoke(testResult)
            }

            EventBusController.publishTestingStateChange(true)
        })

    }
}