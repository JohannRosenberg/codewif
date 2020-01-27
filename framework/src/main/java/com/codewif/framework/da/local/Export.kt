package com.codewif.framework.da.local

import com.codewif.framework.models.TestInfo
import com.codewif.framework.models.TestResultsExport
import com.google.gson.GsonBuilder
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.util.*

/**
 * Provides utility functions to export data.
 */
class Export {
    companion object {
        /**
         * Exports all the failed tests to Json.
         */
        fun exportFailedTestsToJson(): String {
            return serializeTestsToJson(false)
        }

        /**
         * Exports all tests to Json.
         */
        fun exportAllTestsToJson(): String {
            return serializeTestsToJson(true)
        }

        private fun serializeTestsToJson(includeAllTests: Boolean): String {

            val results = TestResultsExport()
            TestRepository.getTestResultsSummary(results.summary)

            results.apply {
                results.summary.projectId = TestRepository.projectId

                if (!TestRepository.appName.isNullOrEmpty())
                    summary.appName = TestRepository.appName
                else if (!TestRepository.libraryPackageName.isNullOrEmpty())
                    summary.libraryPackageName = TestRepository.libraryPackageName

                summary.versionName = TestRepository.versionName
                summary.testDate = TestRepository.testingStartTime

                tests = mutableListOf<TestInfo>()
                TestRepository.getSortedTestResults(tests, includeAllTests)
            }

            val gson = GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(
                    Date::class.java,
                    JsonSerializer<Date> { date: Date, _: Type?, _: JsonSerializationContext? ->
                        JsonPrimitive(
                            date.time
                        )
                    }
                )
                .create()

            return gson.toJson(results)
        }
    }
}