package com.codewif.framework.da.local

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.codewif.framework.models.RecyclerViewTestInfo


/**
 * Used by the Paging Library to provide a data source for the recyclerviews used to display tests and test results.
 */
class TestResultsDataSourceFactory :
    DataSource.Factory<Int, RecyclerViewTestInfo>() {
    val sourceLiveData = MutableLiveData<TestResultsDataSource>()
    private lateinit var latestSource: TestResultsDataSource

    override fun create(): DataSource<Int, RecyclerViewTestInfo> {
        latestSource = TestResultsDataSource()
        sourceLiveData.postValue(latestSource)
        return latestSource
    }
}