package com.codewif.service.models

import androidx.room.Entity
import com.codewif.shared.service.models.UITestInfoBase

@Entity(tableName = "UITests", primaryKeys = ["projectId", "testId"])
open class UITestInfo(testId: String, hashcode: Int, snapshotUrl: String?) : UITestInfoBase(testId, hashcode, snapshotUrl) {
    lateinit var projectId: String
}