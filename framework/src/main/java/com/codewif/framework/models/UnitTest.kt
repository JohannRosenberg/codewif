package com.codewif.framework.models

/**
 * Defines the test to be executed. This class must only be used when setting up tests. It must not be used to store
 * any state that lasts for the life time of the app. For state management of tests that last the life time of the app,
 * use TestInfo instead. Instances of UnitTest are created and destroyed immediately after they have fulfilled their purpose.
 */
open class UnitTest : TestInfoBase {
    constructor(testName: String) : super(testName)
    constructor(testName: String, skipTest: Boolean) : super(testName, skipTest)

    private var testToRunSync: (suspend () -> TestResult)? = null
    private var testToRunAsync: (suspend (testToRunAsync: (resultCallback: TestResult) -> Unit) -> Unit)? = null
    private var uiTestToRun: (suspend () -> Unit)? = null

    fun testToRunSync(testToRun: suspend () -> TestResult): UnitTest {
        this.testToRunSync = testToRun
        return this
    }

    fun getTestToRunSync(): (suspend () -> TestResult)? {
        return this.testToRunSync
    }

    fun testToRunAsync(testToRun: suspend (testToRun: (resultCallback: TestResult) -> Unit) -> Unit): UnitTest {
        this.testToRunAsync = testToRun
        return this
    }

    fun getTestToRunAsync(): (suspend (testToRunAsync: (resultCallback: TestResult) -> Unit) -> Unit)? {
        return this.testToRunAsync
    }

    fun uiTestToRun(testToRun: suspend () -> Unit): UnitTest {
        this.uiTestToRun = testToRun
        return this
    }

    fun getUITestToRun(): (suspend () -> Unit)? {
        return this.uiTestToRun
    }
}
