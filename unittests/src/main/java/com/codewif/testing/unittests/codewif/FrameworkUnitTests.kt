package com.codewif.testing.unittests.codewif

import com.codewif.framework.eventBus.EventBusController
import com.codewif.framework.models.TestResult
import com.codewif.framework.models.UnitTest
import com.codewif.framework.testing.TestSetup

/**
 * This is where tests are configured.
 */
class FrameworkhUnitTests : TestSetup() {
    init {
/*        addTest(UnitTest("Number is divisible by 4").testToRunSync {
            val testResult = TestResult()



            //testResult.succeeded = MathUtis.divisibleByFour(18)
            testResult
        })*/

        addTest(UnitTest("Is a prime number").testToRunAsync { callback ->
            val testResult = TestResult()

            EventBusController.subscribeToTestingStateChange(this) { runningTests ->
                testResult.succeeded = runningTests
                callback.invoke(testResult)
            }

            EventBusController.publishTestingStateChange(true)
        })

    }
}