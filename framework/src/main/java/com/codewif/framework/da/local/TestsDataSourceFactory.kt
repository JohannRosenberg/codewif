package com.codewif.framework.da.local

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.codewif.framework.models.RecyclerViewTestInfo


/**
 * Used by the Paging Library to provide a data source for the recyclerviews used to display tests and test results.
 */
class TestsDataSourceFactory :
    DataSource.Factory<Int, RecyclerViewTestInfo>() {
    private val sourceLiveData = MutableLiveData<TestsDataSource>()
    private lateinit var latestSource: TestsDataSource

    override fun create(): DataSource<Int, RecyclerViewTestInfo> {
        latestSource = TestsDataSource()
        sourceLiveData.postValue(latestSource)
        return latestSource
    }
}