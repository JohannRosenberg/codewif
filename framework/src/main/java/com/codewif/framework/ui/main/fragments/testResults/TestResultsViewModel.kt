package com.codewif.framework.ui.main.fragments.testResults

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.codewif.framework.da.local.TestRepository
import com.codewif.framework.eventBus.EventBusController
import com.codewif.framework.models.RecyclerViewTestInfo
import com.codewif.framework.models.TestResultsSummaryBase
import com.codewif.framework.ui.main.fragments.base.BaseTestsViewModel

class TestResultsViewModel : BaseTestsViewModel() {

    private var testResultsLiveData: LiveData<PagedList<RecyclerViewTestInfo>>
    private var testResultsDataSourceFactory = TestRepository.testResultsDataSourceFactory
    var onFinalTestResultsUpdated: MutableLiveData<Unit> = MutableLiveData()


    init {
        val config = PagedList.Config.Builder().setEnablePlaceholders(true).setPageSize(TestRepository.TESTS_PER_PAGE)
        testResultsLiveData = LivePagedListBuilder(testResultsDataSourceFactory, config.build()).build()

        EventBusController.subscribeToFinalTestResultsUpdated(this) {
            onFinalTestResultsUpdated.postValue(Unit)
        }
    }

    fun refreshDatasource() {
        testResultsLiveData.value?.dataSource?.invalidate()
    }

    fun getTestResultsSummary(): TestResultsSummaryBase {
        val summary = TestResultsSummaryBase()
        TestRepository.getTestResultsSummary(summary)
        return summary
    }


    fun getTestResults(): LiveData<PagedList<RecyclerViewTestInfo>> {
        return testResultsLiveData
    }
}