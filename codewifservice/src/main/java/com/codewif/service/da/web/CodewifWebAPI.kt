package com.codewif.service.da.web

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface CodewifWebAPI {
    @POST
    suspend fun sendTestResultsToBackend(@Url url: String, @Body request: String)
}