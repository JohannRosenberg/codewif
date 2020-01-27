package com.codewif.framework.testing

import com.codewif.framework.errorHandling.TestAlreadyAddedException
import com.codewif.framework.models.UnitTest

/**
 * One or more tests must be defined in a class that inherits from TestSetup.
 */
open class TestSetup {

    /**
     * A collection of all the tests to run.
     */
    val tests: MutableMap<String, UnitTest> = mutableMapOf()

    /**
     * Adds a test that will be executed. The order in which tests are added is the order in which they are executed.
     */
    fun addTest(unitTest: UnitTest) {

        // NOTE: this.javaClass.name doesn't return the name of the class TestSetup but rather the name of the class containing
        // the function that is calling this function.
        val key = this.javaClass.name + ": ${unitTest.testName}"
        unitTest.id = key
        unitTest.testSource = this.javaClass.name

        if (tests.containsKey(key)) {
            throw TestAlreadyAddedException(unitTest.testName)
        }

        tests[key] = unitTest
    }
}