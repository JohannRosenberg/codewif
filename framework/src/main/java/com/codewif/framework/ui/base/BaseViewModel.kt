package com.codewif.framework.ui.base

import androidx.lifecycle.ViewModel
import com.codewif.shared.eventBus.EventBusControllerBase

open class BaseViewModel : ViewModel() {
    override fun onCleared() {
        super.onCleared()
        EventBusControllerBase.unsubscribeAllEventsForSource(this)
    }
}