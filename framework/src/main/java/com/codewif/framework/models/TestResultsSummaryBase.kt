package com.codewif.framework.models

import com.google.gson.annotations.Expose

open class TestResultsSummaryBase {
    @Expose
    var totalTested: Int = 0

    @Expose
    var totalSucceeded: Int = 0

    @Expose
    var totalFailed: Int = 0

    @Expose
    var totalSkipped: Int = 0

    @Expose
    var gitBranchName: String? = null

    @Expose
    var duration: Long = 0 // In milliseconds
}