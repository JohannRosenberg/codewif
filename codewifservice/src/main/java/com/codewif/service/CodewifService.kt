package com.codewif.service

import android.Manifest
import android.app.Activity
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.codewif.service.da.TestsRepository
import com.codewif.service.logging.LOG_TAG
import com.codewif.service.models.UITestInfo
import com.codewif.service.ui.MainActivity
import com.codewif.service.util.Notifications
import com.codewif.shared.App
import com.codewif.shared.App.Companion.ctx
import com.codewif.shared.eventBus.EventBusControllerBase
import com.codewif.shared.eventBus.EventBusTypes
import com.codewif.shared.service.*
import com.codewif.shared.service.models.SendTestResultsToBackendServiceCall
import com.codewif.shared.service.models.ServiceCallBase
import com.codewif.shared.service.models.StoreUITestsServiceCall
import com.codewif.shared.utils.security.PERMISSIONS_REQUEST_SD_READ_WRITE
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * This services stores and retrieves data from the Room database as well as provide web access.
 */
class CodewifService : Service() {

    init {
        instance = this
    }

    companion object {
        private var instance: CodewifService? = null

        fun terminateService() {
            instance?.let {
                Intent().also { intent ->
                    intent.setAction(BROADCAST_ACTION_SERVICE_TERMINATED)
                    ctx.sendBroadcast(intent)
                }

                it.stopSelf()
                it.stopForeground(true)
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    private val messenger: Messenger = Messenger(IncomingHandler())


    /**
     * Handler for incoming messages from clients.
     */
    class IncomingHandler : Handler(), CoroutineScope by CoroutineScope(Dispatchers.IO) {

        private val gson = Gson()

        override fun handleMessage(msg: Message) {

            // Copies of the fields are made because when the following coroutine is launched, the handleMessage
            // returns immediately and the fields in msg are reset.

            val cmd = msg.what
            val bundleIn = msg.obj as Bundle
            val replyTo = msg.replyTo

            launch {
                checkPermissions()

                when (cmd) {
                    SERVICE_CMD_GET_UI_TESTS -> {

                        val data = getCallData<ServiceCallBase>(bundleIn)
                        val uiTests = TestsRepository.getUITests(data.projectId)

                        val json = gson.toJson(uiTests)
                        val bundleOut = Bundle()
                        bundleOut.putString(SERVICE_BUNDLE_KEY_UI_TESTS, json)

                        replyTo.send(
                            Message.obtain(
                                null,
                                SERVICE_CMD_GET_UI_TESTS, bundleOut
                            )
                        )
                    }
                    SERVICE_CMD_STORE_UI_TESTS -> {

                        val data = getCallData<StoreUITestsServiceCall>(bundleIn)
                        val uiTests = mutableListOf<UITestInfo>()
                        data.uiTests.forEach { uiTest ->
                            val uiTestInfo = UITestInfo(uiTest.testId, uiTest.hashcode, uiTest.snapshotUrl)
                            uiTestInfo.projectId = data.projectId
                            uiTests.add(uiTestInfo)
                        }

                        val uiTestsUpdated = TestsRepository.storeUITests(data.projectId, uiTests)

                        val json = gson.toJson(uiTestsUpdated)
                        val bundleOut = Bundle()
                        bundleOut.putString(SERVICE_BUNDLE_KEY_UI_TESTS, json)

                        replyTo.send(
                            Message.obtain(
                                null,
                                SERVICE_CMD_GET_UI_TESTS, bundleOut
                            )
                        )
                    }
                    SERVICE_CMD_SEND_TEST_RESULTS_TO_BACKEND -> {
                        val data = getCallData<SendTestResultsToBackendServiceCall>(bundleIn)
                        TestsRepository.sendTestResultsToBackend(data.testResultsJSON, data.url, data.requestHeaders)
                    }
                    else -> {

                    }
                }
            }
        }


        private suspend fun checkPermissions() {
            if (ContextCompat.checkSelfPermission(
                    App.ctx,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (!App.isCurrentActivity<MainActivity>()) {
                    val intent = Intent(App.ctx, MainActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    ContextCompat.startActivity(App.ctx, intent, null)

                    waitTillActivityResumed()
                }

                ActivityCompat.requestPermissions(
                    App.currentActivity as Activity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_SD_READ_WRITE
                )

                waitTillPermissionsResponse()
            }
        }

        private suspend fun waitTillActivityResumed(): Unit = suspendCoroutine { cont ->
            EventBusControllerBase.subscribeToActivityResumed(this) {
                EventBusControllerBase.unsubscribeFromEvent(this, EventBusTypes.ACTIVITY_RESUMED)
                cont.resume(Unit)
            }
        }


        private suspend fun waitTillPermissionsResponse(): Unit = suspendCoroutine { cont ->
            EventBusControllerBase.subscribeToPermissionsResponse(this) {
                EventBusControllerBase.unsubscribeFromEvent(this, EventBusTypes.PERMISSIONS_RESPONSE)
                cont.resume(Unit)
            }
        }

        private inline fun <reified T> getCallData(bundle: Bundle): T {
            return gson.fromJson(bundle.getString(SERVICE_BUNDLE_KEY_DATA_TO_SERVICE), T::class.java)
        }
    }

    override fun onCreate() {
        super.onCreate()
        val notifications = Notifications()
        startForeground(notifications.NOTIFICATION_ID_CODEWIF_SERVICE, notifications.createServiceNotification())
    }

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    override fun onBind(intent: Intent?): IBinder? {
        return messenger.binder
    }

    override fun onDestroy() {
        Log.e(LOG_TAG, "Service has been destroyed")
        super.onDestroy()
    }
}
