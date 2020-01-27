package com.codewif.shared.service.models

open class UITestInfoBase {

    constructor()
    constructor(testId: String, hashcode: Int, snapshotUrl: String?) {
        this.testId = testId
        this.hashcode = hashcode
        this.snapshotUrl = snapshotUrl
    }

    lateinit var testId: String
    var hashcode: Int = 0
    var snapshotUrl: String? = null
}