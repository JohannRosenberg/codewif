package com.codewif.framework.models

import com.google.gson.annotations.Expose

class TestResultsExport {
    @Expose
    var summary = TestResultsSummary()

    @Expose
    var tests = mutableListOf<TestInfo>()
}