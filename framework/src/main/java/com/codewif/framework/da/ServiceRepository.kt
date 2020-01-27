package com.codewif.framework.da

import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.codewif.framework.R
import com.codewif.framework.da.local.TestRepository
import com.codewif.framework.ui.main.CodewifMainActivity
import com.codewif.shared.App
import com.codewif.shared.App.Companion.ctx
import com.codewif.shared.eventBus.EventBusControllerBase
import com.codewif.shared.eventBus.EventBusTypes
import com.codewif.shared.service.*
import com.codewif.shared.service.models.SendTestResultsToBackendServiceCall
import com.codewif.shared.service.models.ServiceCallBase
import com.codewif.shared.service.models.StoreUITestsServiceCall
import com.codewif.shared.service.models.UITestInfoBase
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * Used to interact with the Codewif Service app.
 */
class ServiceRepository {
    companion object : CoroutineScope by CoroutineScope(Dispatchers.IO) {

        private var service: Messenger? = null
        private val messenger = Messenger(IncomingHandler())
        private val gson = Gson()
        private var broadcastReceiver: ServiceBroadcastReceiver? = null

        lateinit var serviceStartContinuation: Continuation<Unit>

        lateinit var getUITestsContinuation: Continuation<List<UITestInfoBase>>
        var uiTests = mutableListOf<UITestInfoBase>()


        lateinit var storeUITestsContinuation: Continuation<List<UITestInfoBase>>
        private var uiTestsStored = mutableListOf<UITestInfoBase>()

        /**
         * Class for interacting with the main interface of the service.
         */
        private val connection: ServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName, service: IBinder) {
                this@Companion.service = Messenger(service)
                serviceStartContinuation.resume(Unit)
            }

            override fun onServiceDisconnected(className: ComponentName) {
                service = null
            }
        }


        fun codewifServiceTerminated() {
            if (service != null) {
                ctx.unbindService(connection)
                service = null
            }
        }


        /**
         * Starts the Codewif Service if it's installed or prompts the user to install it if it's not installed.
         */
        suspend fun startCodewifService(): Boolean {
            if (service != null)
                return true

            var serviceInstalled = false

            try {
                ctx.packageManager.getPackageInfo(CODEWIF_SERVICE_PACKAGE_NAME, PackageManager.GET_ACTIVITIES)
                serviceInstalled = true
            } catch (exception: Exception) {
            }

            if (!serviceInstalled) {
                val intent = Intent(ctx, CodewifMainActivity::class.java)
                ContextCompat.startActivity(ctx, intent, null)

                waitTillActivityResumed()
                displayDialogToInstallCodewifService()
                return false
            }

            if (service == null) {
                startServiceAndWait()
            }

            if (broadcastReceiver == null) {
                val filter = IntentFilter(BROADCAST_ACTION_SERVICE_TERMINATED)
                broadcastReceiver = ServiceBroadcastReceiver()
                ctx.registerReceiver(broadcastReceiver, filter)
            }

            return true
        }


        private suspend fun startServiceAndWait(): Unit = suspendCoroutine { cont ->
            serviceStartContinuation = cont

            val intent = Intent()
            intent.component = ComponentName(CODEWIF_SERVICE_PACKAGE_NAME, CODEWIF_SERVICE_CLASS_NAME)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(ctx, intent)
            } else {
                ctx.startService(intent)
            }

            ctx.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }


        private suspend fun waitTillActivityResumed(): Unit = suspendCoroutine { cont ->
            EventBusControllerBase.subscribeToActivityResumed(this) {
                EventBusControllerBase.unsubscribeFromEvent(this, EventBusTypes.ACTIVITY_RESUMED)
                cont.resume(Unit)
            }
        }


        /**
         * Displays a dialog to inform the user to install the Codewif Service app. When this function is called, the app will terminaate after
         * the user clicks the dialog's ok button.
         */
        private suspend fun displayDialogToInstallCodewifService(): Unit = suspendCoroutine {
            launch(Dispatchers.Main) {
                val alertDialog: AlertDialog?
                val builder = AlertDialog.Builder(App.currentActivity as Context)

                builder.setMessage(ctx.getString(R.string.cw_codewif_service_not_installed))
                    .setPositiveButton(R.string.cw_ok) { _, _ ->
                        // Launch Google Play to let the user install the app.
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://play.google.com/store/apps/details?id=com.codewif.service")
                            setPackage("com.android.vending")
                        }

                        App.currentActivity?.startActivity(intent)
                        App.currentActivity?.finishAffinity()
                    }

                alertDialog = builder.show()
                alertDialog.setCancelable(false)
            }
        }

        /**
         * Handler of incoming messages from service.
         */
        internal class IncomingHandler : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    SERVICE_CMD_GET_UI_TESTS, SERVICE_CMD_STORE_UI_TESTS -> {
                        val bundle = (msg.obj) as Bundle
                        val items =
                            gson.fromJson(bundle.getString(SERVICE_BUNDLE_KEY_UI_TESTS), Array<UITestInfoBase>::class.java)
                                .toMutableList()
                        items.toCollection(uiTests)

                        when (msg.what) {
                            SERVICE_CMD_GET_UI_TESTS -> {
                                getUITestsContinuation.resume(uiTests)
                            }
                            SERVICE_CMD_STORE_UI_TESTS -> storeUITestsContinuation.resume(uiTests)
                        }
                    }
                }
            }
        }

        /**
         * Sends a command along with optional data to the service.
         */
        private fun sendToService(cmd: Int, data: String) {
            val bundle = Bundle()
            bundle.putString(SERVICE_BUNDLE_KEY_DATA_TO_SERVICE, data)
            val msg: Message = Message.obtain(null, cmd)
            msg.obj = bundle
            msg.replyTo = messenger
            this@Companion.service?.send(msg)
        }


        /**
         * Returns a list of ui tests that were previously performed.
         */
        suspend fun getUITests(): List<UITestInfoBase> = suspendCoroutine { cont ->
            getUITestsContinuation = cont
            uiTests.clear()

            val json = gson.toJson(ServiceCallBase(TestRepository.projectId))

            sendToService(SERVICE_CMD_GET_UI_TESTS, json)
            uiTests
        }


        /**
         * Stores UI test results.
         */
        suspend fun storeUITests(uiTests: List<UITestInfoBase>): List<UITestInfoBase> = suspendCoroutine { cont ->
            getUITestsContinuation = cont

            val json = gson.toJson(StoreUITestsServiceCall(TestRepository.projectId, uiTests))

            sendToService(SERVICE_CMD_STORE_UI_TESTS, json)
            uiTestsStored
        }


        fun sendTestResultsToBackend(
            testResultsJSON: String,
            url: String? = null,
            requestHeaders: MutableMap<String, String>? = null
        ) {
            val callData = SendTestResultsToBackendServiceCall(testResultsJSON, url, requestHeaders)
            val json = gson.toJson(callData)
            sendToService(SERVICE_CMD_SEND_TEST_RESULTS_TO_BACKEND, json)
        }
    }
}

