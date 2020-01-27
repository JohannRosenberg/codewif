package com.mydomain.launcher.codewif

import com.codewif.framework.models.TestResult
import com.codewif.framework.models.UnitTest
import com.codewif.framework.testing.TestSetup
import com.mydomain.mylib.StringUtils

class StringUnitTests : TestSetup() {

    init {
        addTest(UnitTest("Has 3 words").testToRunAsync { callback ->
            val testResult = TestResult()
            testResult.succeeded = StringUtils.hasThreeWords("The dog jumped")
            callback.invoke(testResult)
        })

        addTest(UnitTest("Is a number").testToRunAsync { callback ->
            val testResult = TestResult()
            testResult.succeeded = StringUtils.isNumeric("123a")
            callback.invoke(testResult)
        })
    }
}