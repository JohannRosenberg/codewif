package com.codewif.framework.models

import com.google.gson.annotations.Expose
import java.util.*

/**
 * Use to hold summary information about test results. Typically used when exporting data to Json.
 */
open class TestResultsSummary : TestResultsSummaryBase() {

    /**
     * The project id that the tests are associated with.
     */
    @Expose
    var projectId: String? = null

    /**
     * The name of the app that was tested. Don't use this if testing a library.
     */
    @Expose
    var appName: String? = null

    /**
     * The name of the library that was tested. Don't use this if testing an app.
     */
    @Expose
    var libraryPackageName: String? = null

    /**
     * The version of the app or library that was tested.
     */
    @Expose
    var versionName: String? = null

    /**
     * The date/time when the tests were run.
     */
    @Expose
    var testDate: Date? = null
}