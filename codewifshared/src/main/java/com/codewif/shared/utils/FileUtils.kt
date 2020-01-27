package com.codewif.shared.utils

import android.os.Environment
import android.util.Log
import com.codewif.service.logging.CreatingFileException
import com.codewif.service.logging.WriteToSDCardException
import com.codewif.shared.logging.LOG_TAG
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class FileUtils {

    val CODEWIF_ROOT_FOLDER_NAME = "Codewif"
    val CODEWIF_SNAPSHOT_FOLDER_NAME = "snapshots"

    /**
     * Saves a ui screenshot image from a test to a png file on disk.
     * @return The path and filename of the stored image will be returned.
     */
    fun saveUITestImageToDisk(streamOut: ByteArrayOutputStream, projectId: String, testId: String): String {

        val filename = testId.replace("[^a-zA-Z0-9\\.\\-]", "_")
            .replace(".", "_")
            .replace(":", "_")
            .replace(" ", "_") + ".webp"

        val folder = createCodewifProjectSnapshotFolder(projectId)
        val fileOut = File(folder, filename)

        try {
            fileOut.createNewFile()
        } catch (exception: Exception) {
            throw CreatingFileException("Snapshot image could not be saved to disk. Check that the Storage permission for your app has been enabled, or your test app if you are testing an Android library. By default, this permission is not enabled when you use this app in debug mode. You need to manually enable it. It will also get disabled if you delete the app's data. Also make sure that the Storage permission for the Codewif Service app has also been enabled.")
        }

        val fos = FileOutputStream(fileOut)
        fos.write(streamOut.toByteArray())
        fos.close()

        return fileOut.toString()
    }


    private fun createCodewifProjectSnapshotFolder(projectId: String): File {
        val f = File(
            Environment.getExternalStorageDirectory(),
            "/${CODEWIF_ROOT_FOLDER_NAME}/${projectId}/${CODEWIF_SNAPSHOT_FOLDER_NAME}"
        )

        if (!f.exists()) {
            f.mkdirs()
        }

        return f
    }

    /**
     * Renames a file.
     */
    fun renameFile(oldFilename: String, newFileName: String) {
        val from = File(oldFilename)
        val to = File(newFileName)

        if (from.exists()) {
            from.renameTo(to)

            if (!to.exists()) {
                val errMessage =
                    "File could not be renamed. Check that the Storage permission for the Codewif Service app has been enabled. By default, this permission is not enabled when you use this app in debug mode. You need to manually enable it. It will also get disabled if you delete the app's data."
                Log.e(LOG_TAG, errMessage)
                throw WriteToSDCardException(errMessage)
            }
        }
    }
}