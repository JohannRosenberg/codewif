package com.codewif.framework.models

/**
 * Used to hold information about tests. Specifically used by recyclerviews.
 */
open class RecyclerViewTestInfo(
    id: String,
    testName: String,
    testSource: String,
    isUITest: Boolean,
    runSynchronously: Boolean,
    skipTest: Boolean
) :
    TestInfoBase(id, testName, testSource, isUITest, runSynchronously, skipTest) {

    var testSucceeded: Boolean? = null
}
