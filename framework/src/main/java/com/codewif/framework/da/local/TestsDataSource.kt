package com.codewif.framework.da.local

import androidx.paging.PositionalDataSource
import com.codewif.framework.models.RecyclerViewTestInfo

/**
 * The data source used by recyclerviews to show tests.
 */
class TestsDataSource : PositionalDataSource<RecyclerViewTestInfo>() {

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<RecyclerViewTestInfo>) {
        var lastIndex = params.startPosition + params.loadSize - 1

        if (lastIndex > TestRepository.tests.lastIndex)
            lastIndex = TestRepository.tests.lastIndex

        val newList = mutableListOf<RecyclerViewTestInfo>()
        TestRepository.copyTestItems(params.startPosition, lastIndex, newList)

        callback.onResult(newList)
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<RecyclerViewTestInfo>) {
        var lastIndex = params.requestedStartPosition + params.requestedLoadSize

        if (lastIndex > TestRepository.tests.lastIndex)
            lastIndex = TestRepository.tests.lastIndex

        val newList = mutableListOf<RecyclerViewTestInfo>()
        TestRepository.copyTestItems(params.requestedStartPosition, lastIndex, newList)

        callback.onResult(newList, params.requestedStartPosition, TestRepository.tests.size)
    }
}