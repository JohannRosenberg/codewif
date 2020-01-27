package com.codewif.framework.models

import com.google.gson.annotations.Expose

class TestInfo(
    id: String,
    testName: String,
    testSource: String,
    isUITest: Boolean,
    runSynchronously: Boolean,
    skipTest: Boolean
) :
    TestInfoBase(id, testName, testSource, isUITest, runSynchronously, skipTest) {

    @Expose
    var testResults: TestResult? = null
}