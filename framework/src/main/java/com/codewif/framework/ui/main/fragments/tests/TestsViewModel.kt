package com.codewif.framework.ui.main.fragments.tests

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.codewif.framework.da.local.TestRepository
import com.codewif.framework.models.RecyclerViewTestInfo
import com.codewif.framework.ui.main.fragments.base.BaseTestsViewModel

class TestsViewModel : BaseTestsViewModel() {

    private var testsLiveData: LiveData<PagedList<RecyclerViewTestInfo>>
    private var testsDataSourceFactory = TestRepository.testsDataSourceFactory


    init {
        val config = PagedList.Config.Builder().setEnablePlaceholders(true).setPageSize(TestRepository.TESTS_PER_PAGE)
        testsLiveData = LivePagedListBuilder(testsDataSourceFactory, config.build()).build()
    }


    fun getTests(): LiveData<PagedList<RecyclerViewTestInfo>> {
        return testsLiveData
    }

    fun skipTest(testId: String, skipTest: Boolean) {
        TestRepository.skipTest(testId, skipTest)
    }

    fun initializeForAllTests() {
        TestRepository.initializeForAllTests()
    }
}
