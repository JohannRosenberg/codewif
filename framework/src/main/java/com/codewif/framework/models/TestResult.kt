package com.codewif.framework.models

import com.codewif.shared.service.models.UITestInfoBase
import com.google.gson.annotations.Expose

/**
 * Contains information about a test result.
 */
open class TestResult {

    constructor()

    constructor(details: String?) {
        this.details = details
    }

    constructor(succeeded: Boolean, details: String?) {
        this.succeeded = succeeded
        this.details = details
    }

    /**
     * The test that the test results apply to.
     */
    @Expose
    lateinit var testId: String

    /**
     * If set to true, the test succeeded.
     */
    @Expose
    var succeeded: Boolean = false

    /**
     * Any additional details about the test results that can provide more information to testers or developers.
     */
    @Expose
    var details: String? = null

    /**
     * How long the test took to execute.
     */
    @Expose
    var duration: Long = 0 // In milliseconds

    /**
     * For UI tests, this contains the ui test results after a test has completed.
     */
    @Expose
    var uiTestInfoCurrent: UITestInfoBase? = null

}