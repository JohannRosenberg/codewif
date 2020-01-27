package com.codewif.framework.eventBus

import com.codewif.shared.eventBus.EventBusControllerBase
import com.codewif.shared.eventBus.EventBusItem
import com.codewif.shared.eventBus.EventBusTypes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async


/**
 * Provides support for an event bus.
 */
open class EventBusController : EventBusControllerBase() {
    companion object : CoroutineScope by CoroutineScope(Dispatchers.IO) {
        /**
         * Subscribe to a change in testing state.
         */
        fun subscribeToTestingStateChange(sourceContext: Any, callback: (isRunning: Boolean) -> Unit) {
            eventBusItems.add(EventBusItem(sourceContext, EventBusTypes.TESTING_STATE_CHANGE, callback))
        }

        /**
         * Publish a change in testing state.
         */
        fun publishTestingStateChange(runningTests: Boolean) {
            eventBusItems.filter { it.eventType == EventBusTypes.TESTING_STATE_CHANGE }.forEach { eventBusItem ->
                async { (eventBusItem.callback as (state: Boolean) -> Unit).invoke(runningTests) }
            }
        }

        /**
         * Subscribe to the event that indicates that all tests have been completed and the test results have been updated.
         */
        fun subscribeToFinalTestResultsUpdated(sourceContext: Any, callback: () -> Unit) {
            eventBusItems.add(EventBusItem(sourceContext, EventBusTypes.TEST_RESULTS_UPDATED, callback))
        }

        /**
         * Publishes event to indicate that all tests have been completed and the test results have been updated.
         */
        fun publishFinalTestResultsUpdated() {
            eventBusItems.filter { it.eventType == EventBusTypes.TEST_RESULTS_UPDATED }.forEach { eventBusItem ->
                async { (eventBusItem.callback as () -> Unit).invoke() }
            }
        }
    }
}

