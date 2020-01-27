package com.codewif.service.util

import android.app.Notification
import com.codewif.service.R
import com.codewif.service.ui.MainActivity
import com.codewif.shared.App.Companion.ctx
import com.codewif.shared.utils.NotificationsBase

/**
 * Provides utility functions to display various kinds of messages: Snackbar, AlertDialog, Statusbar notifications, etc.
 */
class Notifications :
    NotificationsBase<MainActivity>(
        ctx.getString(R.string.app_name),
        "channelIdCodewifService",
        "channelNameCodewifService",
        MainActivity::class.java
    ) {

    val NOTIFICATION_ID_CODEWIF_SERVICE = 232323

    fun createServiceNotification(): Notification {
        return createNotification(
            R.string.service_running,
            ctx.getString(R.string.click_here),
            R.drawable.cw_ic_codewif_statusbar
        )
    }
}