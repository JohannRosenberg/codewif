package com.codewif.shared.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.codewif.shared.App
import com.codewif.shared.App.Companion.ctx
import com.codewif.shared.R
import com.google.android.material.snackbar.Snackbar

open class NotificationsBase<T>(private var appName: String, private var channelId: String, private var channelName: String, private var classz: Class<T>) {

    fun displayErrorMessage(resId: Int) {
        if (App.currentActivity == null) {
            showNotification(
                R.string.cw_application_error,
                resId,
                R.drawable.cw_ic_warning,
                channelId,
                channelName,
                (0..0x7fffffff).random()
            )
        } else {
            showErrorSnackbar(resId)
        }
    }


    /**
     * Displays a message, either in an AlertDialog, if an activity is showning, or as a notification if no activity is showing.
     */
    fun displayMessage(resId: Int) {
        if (App.currentActivity == null) {
            createNotification(
                R.string.cw_information,
                resId,
                R.drawable.cw_ic_info,
                channelId,
                channelName
            )
        } else {
            displayModalDialog(resId)
        }
    }

    fun displayModalDialog(resId: Int) {
        displayModalDialog(ctx.getString(resId))
    }

    fun displayModalDialog(message: String) {
        if (App.currentActivity == null) {
            createNotification(
                R.string.cw_information,
                message,
                R.drawable.cw_ic_info,
                channelId,
                channelName
            )
        } else {
            val context = App.currentActivity as Context

            val alertDialog: AlertDialog?
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setMessage(message)
                .setCancelable(true)
                .setPositiveButton(context.getString(R.string.cw_ok)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setTitle(appName)

            alertDialog = dialogBuilder.show()
            alertDialog.setCancelable(false)
        }
    }


    /**
     * Displays a snackbar with normal text. Use showErrorSnackbar if you want to show an error message instead.
     * @param duration Set to Snackbar.LENGTH_INDEFINITE, Snackbar.LENGTH_LONG or Snackbar.LENGTH_SHORT
     */
    fun showInfoSnackbar(resId: Int, duration: Int) {
        val view = App.getCurrentActivityRootView()
        showSnackbar(resId, duration, false, view)
    }

    fun showErrorSnackbarForDuration(resId: Int, duration: Int, view: View) {
        showSnackbar(resId, duration, true, view)
    }

    fun showErrorSnackbar(resId: Int) {
        val view = App.getCurrentActivityRootView()
        showSnackbar(resId, Snackbar.LENGTH_INDEFINITE, true, view)
    }

    fun showErrorSnackbar(message: String) {
        val view = App.getCurrentActivityRootView()
        displaySnackbar(message, Snackbar.LENGTH_INDEFINITE, true, view)
    }

    fun showErrorSnackbar(resId: Int, view: View) {
        showSnackbar(resId, Snackbar.LENGTH_INDEFINITE, true, view)
    }

    private fun showSnackbar(resId: Int, duration: Int, isErrorMessage: Boolean, view: View?) {

        if (view == null)
            return

        displaySnackbar(ctx.getString(resId), duration, isErrorMessage, view)

    }

    fun displaySnackbar(message: String, duration: Int, isErrorMessage: Boolean, view: View?) {
        val snackbar = Snackbar.make(view as View, message, duration)
        snackbar.view.setBackgroundColor(ctx.resources.getColor(R.color.cw_snackbar_background))
        snackbar.setActionTextColor(ctx.resources.getColor(R.color.cw_snackbar_button_text_color))

        if (duration == Snackbar.LENGTH_INDEFINITE) {
            snackbar.setAction(ctx.getString(R.string.cw_cancel)) {}
        }

        val snackbarView = snackbar.view

        if (isErrorMessage) {
            val textView = snackbarView.findViewById(R.id.snackbar_text) as TextView
            textView.setTextColor(ctx.resources.getColor(R.color.cw_snackbar_error_text))
        }

        snackbar.show()
    }

    fun createNotification(
        titleResId: Int,
        message: String,
        iconResId: Int
    ): Notification {
        return makeNotification(titleResId, message, iconResId, channelId, channelName)
    }

    fun createNotification(
        titleResId: Int,
        message: String,
        iconResId: Int,
        channeld: String,
        channelName: String
    ): Notification {
        return makeNotification(titleResId, message, iconResId, channeld, channelName)
    }

    fun createNotification(
        titleResId: Int,
        textResId: Int,
        iconResId: Int,
        channeld: String,
        channelName: String
    ): Notification {
        return makeNotification(titleResId, ctx.getString(textResId), iconResId, channeld, channelName)
    }


    fun makeNotification(
        titleResId: Int,
        message: String,
        iconResId: Int,
        channeld: String,
        channelName: String
    ): Notification {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(channeld, channelName)
            } else {
                // If earlier version channel ID is not used
                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                ""
            }

        val notificationIntent = Intent(ctx, classz)
        val pendingIntent = PendingIntent.getActivity(ctx, 0, notificationIntent, 0)

        return NotificationCompat.Builder(ctx, channelId)
            .setContentTitle(ctx.getString(titleResId))
            .setContentText(message)
            .setSmallIcon(iconResId)
            .setContentIntent(pendingIntent)
            .build()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }


    fun showNotification(
        titleResId: Int,
        textResId: Int,
        iconResId: Int,
        channeld: String,
        channelName: String,
        notificationId: Int
    ) {
        with(NotificationManagerCompat.from(ctx)) {
            val notification = createNotification(titleResId, textResId, iconResId, channeld, channelName)
            notify(notificationId, notification)
        }
    }

    fun showToast(text: String) {
        val handler = Handler(Looper.getMainLooper())
        handler.post { Toast.makeText(ctx, text, Toast.LENGTH_LONG).show() }
    }
}