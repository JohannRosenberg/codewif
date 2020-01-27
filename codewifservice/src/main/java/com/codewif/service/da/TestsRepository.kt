package com.codewif.service.da


import android.util.Log
import com.codewif.service.da.room.RoomDB
import com.codewif.service.da.web.CodewifWebAPI
import com.codewif.service.da.web.RetrofitClient
import com.codewif.service.da.web.URLs.Companion.CODEWIF_BASE_URL
import com.codewif.service.logging.LOG_TAG
import com.codewif.service.models.UITestInfo
import com.codewif.shared.utils.FileUtils
import java.io.File

class TestsRepository {
    companion object {
        private val roomDao = RoomDB.roomDB.roomDao()
        private var webApi: CodewifWebAPI = RetrofitClient.createRetrofitClient()

        suspend fun getUITests(projectId: String): List<UITestInfo> {
            return roomDao.getUITests(projectId)
        }

        suspend fun storeUITests(projectId: String, newTests: List<UITestInfo>): List<UITestInfo> {

            val oldTests = getUITests(projectId)

            newTests.forEach { newTest ->
                newTest.snapshotUrl?.let {
                    var imageNumber = 1

                    val oldTest = oldTests.find { oldTest ->
                        (oldTest.projectId == newTest.projectId) && (oldTest.testId == newTest.testId)
                    }

                    oldTest?.snapshotUrl?.let {
                        val startPos = it.lastIndexOf("_") + 1
                        val endPos = it.lastIndexOf(".")
                        imageNumber = it.substring(startPos, endPos).toInt() + 1

                        // Delete the previous image if it exists since it is no longer needed and just ends up wasting disk space.
                        val prevFile = File(it)

                        if (prevFile.exists()) {
                            prevFile.delete()
                        }
                    }

                    val startPos = it.lastIndexOf(".") + 1
                    val newFilename = it.substring(0, startPos - 1) + "_" + imageNumber + ".webp"

                    val fileUtils = FileUtils()
                    fileUtils.renameFile(it, newFilename)

                    newTest.snapshotUrl = newFilename
                }
            }

            roomDao.storeUITests(newTests)
            return newTests
        }


        /**
         * Sends the test results to the backend server.
         */
        suspend fun sendTestResultsToBackend(
            testResultsJson: String,
            url: String? = null,
            requestHeaders: MutableMap<String, String>? = null
        ) {
            if ((url != null) && (requestHeaders?.isNotEmpty() == true)) {
                RetrofitClient.requestHeaders[url] = requestHeaders
            }

            try {
                webApi.sendTestResultsToBackend(url ?: CODEWIF_BASE_URL, testResultsJson)
            } catch (exception: Exception) {
                Log.e(LOG_TAG, "Problem sending test results to backend. Url: $url  > ${exception.localizedMessage}")
            }
        }
    }
}