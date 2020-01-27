package com.codewif.shared.eventBus

data class EventBusItem(val sourceContext: Any, val eventType: EventBusTypes, val callback: Any)