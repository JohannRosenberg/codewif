package com.codewif.shared.service.models

open class SendTestResultsToBackendServiceCall(var testResultsJSON: String, var url: String? = null, var requestHeaders: MutableMap<String, String>? = null)