package com.codewif.framework.models

import com.codewif.shared.service.models.UITestInfoBase
import com.google.gson.annotations.Expose

/**
 * This is the base class that all tests are derived from.
 */
open class TestInfoBase {

    constructor(testName: String) {
        this.testName = testName
    }

    constructor(testName: String, skipTest: Boolean) {
        this.testName = testName
        this.skipTest = skipTest
    }

    constructor(
        id: String,
        testName: String,
        testSource: String,
        isUITest: Boolean,
        runSynchronously: Boolean,
        skipTest: Boolean
    ) {
        this.id = id
        this.testName = testName
        this.testSource = testSource
        this.isUITest = isUITest
        this.runSynchronously = runSynchronously
        this.skipTest = skipTest
    }

    @Expose
    var testName: String

    @Expose(serialize = false)
    lateinit var id: String

    /**
     * Indicates where the test is located. This is package name followed by the class name.
     * Example: com.myapp.ui.MainActivity
     */
    @Expose
    lateinit var testSource: String

    /**
     * If set to true, the test is a user interface test.
     */
    @Expose
    var isUITest: Boolean = false

    @Expose
    var runSynchronously: Boolean = false

    /**
     * If set to true, the test will not be run.
     */
    @Expose
    var skipTest: Boolean = false

    @Expose(serialize = false)
    var testIsRunning = false

    @Expose
    var uiTestInfoPrevious: UITestInfoBase? = null
}
