package com.codewif.shared.eventBus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async


/**
 * Provides support for an event bus.
 */
open class EventBusControllerBase {
    companion object : CoroutineScope by CoroutineScope(Dispatchers.IO) {
        val eventBusItems = mutableListOf<EventBusItem>()

        /**
         * Subscribe to event that indicates when a response has been returned when requesting permissions.
         */
        fun subscribeToPermissionsResponse(sourceContext: Any, callback: () -> Unit) {
            eventBusItems.add(EventBusItem(sourceContext, EventBusTypes.PERMISSIONS_RESPONSE, callback))
        }


        /**
         * Publish event when a permissions request response is available.
         */
        fun publishPermissionsResponse() {
            eventBusItems.filter { it.eventType == EventBusTypes.PERMISSIONS_RESPONSE }.forEach { eventBusItem ->
                async { (eventBusItem.callback as () -> Unit).invoke() }
            }
        }


        /**
         * Subscribe to event that indicates that the main activity has been created.
         */
        fun subscribeToActivityCreated(sourceContext: Any, callback: (activity: Any) -> Unit) {
            eventBusItems.add(EventBusItem(sourceContext, EventBusTypes.ACTIVITY_CREATED, callback))
        }


        /**
         * Publish event that main activity is created.
         */
        fun publishActivityCreated(activity: Any) {
            eventBusItems.filter { it.eventType == EventBusTypes.ACTIVITY_CREATED }.forEach { eventBusItem ->
                async { (eventBusItem.callback as (activity: Any) -> Unit).invoke(activity) }
            }
        }


        /**
         * Subscribe to event that indicates that the main activity has resumed
         */
        fun subscribeToActivityResumed(sourceContext: Any, callback: (activity: Any) -> Unit) {
            eventBusItems.add(EventBusItem(sourceContext, EventBusTypes.ACTIVITY_RESUMED, callback))
        }


        /**
         * Publish event that main activity has resumed.
         */
        fun publishActivityResumed(activity: Any) {
            eventBusItems.filter { it.eventType == EventBusTypes.ACTIVITY_RESUMED }.forEach { eventBusItem ->
                async { (eventBusItem.callback as (activity: Any) -> Unit).invoke(activity) }
            }
        }


        /**
         * Subscribe to event that indicates that the main activity has been destroyed.
         */
        fun subscribeToActivityDestroyed(sourceContext: Any, callback: (activity: Any) -> Unit) {
            eventBusItems.add(EventBusItem(sourceContext, EventBusTypes.ACTIVITY_DESTROYED, callback))
        }

        /**
         * Publish event that main activity is destroyed.
         */
        fun publishActivityDestroyed(activity: Any) {
            eventBusItems.filter { it.eventType == EventBusTypes.ACTIVITY_DESTROYED }.forEach { eventBusItem ->
                async { (eventBusItem.callback as (activity: Any) -> Unit).invoke(activity) }
            }
        }


        /**
         * Unsubscribes an item for the specified event type.
         */
        fun unsubscribeFromEvent(sourceContext: Any, eventType: EventBusTypes) {
            eventBusItems.remove(eventBusItems.first { it.sourceContext == sourceContext && it.eventType == eventType })
        }

        /**
         * Unsubscribe a source from all events. Usually this is a class instance.
         */
        fun unsubscribeAllEventsForSource(sourceContext: Any) {
            eventBusItems.removeAll { it.sourceContext == sourceContext }
        }
    }
}

enum class EventBusTypes {
    PERMISSIONS_RESPONSE,
    ACTIVITY_CREATED,
    ACTIVITY_RESUMED,
    ACTIVITY_DESTROYED,
    TESTING_STATE_CHANGE,
    TEST_RESULTS_UPDATED
}