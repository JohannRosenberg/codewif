package com.mydomain.myapp.codewif

import com.codewif.framework.models.TestResult
import com.codewif.framework.models.UnitTest
import com.codewif.framework.testing.TestSetup
import kotlinx.coroutines.delay

class StringUnitTests : TestSetup() {

    init {
        addTest(UnitTest("Join first and last name").testToRunSync {
            val testResult = TestResult()
            testResult.succeeded = true
            testResult
        })

        addTest(UnitTest("Add Doctor Title").testToRunSync {
            val testResult = TestResult()
            testResult.succeeded = true
            testResult
        })

        addTest(UnitTest("Add Greeting").testToRunAsync { callback ->
            delay(200)
            val testResult = TestResult()
            testResult.succeeded = true
            callback.invoke(testResult)
        })

        addTest(UnitTest("Get Message").testToRunAsync { callback ->
            val testResult = TestResult()
            testResult.succeeded = true
            callback.invoke(testResult)
        })
    }
}
