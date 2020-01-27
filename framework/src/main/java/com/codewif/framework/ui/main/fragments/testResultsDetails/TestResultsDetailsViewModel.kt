package com.codewif.framework.ui.main.fragments.testResultsDetails

import com.codewif.framework.da.local.TestRepository
import com.codewif.framework.models.TestInfo
import com.codewif.framework.ui.base.BaseViewModel

class TestResultsDetailsViewModel : BaseViewModel() {

    fun getTestInfo(testId: String): TestInfo {
        return TestRepository.getTestById(testId)
    }


    suspend fun storeUITest(testInfo: TestInfo) {
        TestRepository.storeUITest(testInfo)
    }
}