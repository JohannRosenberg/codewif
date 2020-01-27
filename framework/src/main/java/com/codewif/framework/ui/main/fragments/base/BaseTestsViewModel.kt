package com.codewif.framework.ui.main.fragments.base


import androidx.lifecycle.MutableLiveData
import com.codewif.framework.eventBus.EventBusController
import com.codewif.framework.testing.TestRunner
import com.codewif.framework.ui.base.BaseViewModel

open class BaseTestsViewModel : BaseViewModel() {
    var onTestingStateChanged: MutableLiveData<Boolean> = MutableLiveData()

    init {
        EventBusController.subscribeToTestingStateChange(this) { runningTests ->
            onTestingStateChanged.postValue(runningTests)
        }

        onTestingStateChanged.postValue(TestRunner.testsAreRunning)
    }

    var testsAreRunning: Boolean
        get() = TestRunner.testsAreRunning
        private set(value) {}
}