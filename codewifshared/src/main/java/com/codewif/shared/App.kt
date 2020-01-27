package com.codewif.shared

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.View
import com.codewif.shared.eventBus.EventBusControllerBase


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        ctx = this

        registerActivityLifecycleCallbacks(activityLifecycleTracker)
    }

    companion object {
        private val activityLifecycleTracker: AppLifecycleTracker = AppLifecycleTracker()

        // IMPORTANT: Never rename ctx to "context". context is already defined as part of Android activities and fragments.
        lateinit var ctx: App

        // Returns the current activity.
        var currentActivity: Activity?
            get() = activityLifecycleTracker.currentActivity
            private set(value) {}


        fun getCurrentActivityRootView(): View? =
            activityLifecycleTracker.currentActivity?.let {
                it.findViewById(android.R.id.content)
            }


        inline fun <reified T : Any> isCurrentActivity(): Boolean = (currentActivity != null) && (currentActivity is T)

        inline fun <reified T : Any> closeActivity() {
            if (isCurrentActivity<T>()) {
                currentActivity?.finish()
            }
        }

        /**
         * Returns the name of the application that is using this library.
         */
        fun getApplicationName(context: Context): String? {
            val applicationInfo = context.applicationInfo
            val stringId = applicationInfo.labelRes
            return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(stringId)
        }
    }


    /**
     * Callbacks for handling the lifecycle of activities.
     */
    class AppLifecycleTracker : ActivityLifecycleCallbacks {

        private var currentAct: Activity? = null

        var currentActivity: Activity?
            get() = currentAct
            private set(value) {}

        override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
            EventBusControllerBase.publishActivityCreated(activity)
        }

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityResumed(activity: Activity) {
            currentAct = activity
        }

        override fun onActivityPaused(activity: Activity) {
        }

        override fun onActivityStopped(activity: Activity) {
            if ((currentAct != null) && (activity == currentAct))
                currentAct = null
        }


        override fun onActivityDestroyed(activity: Activity) {
            EventBusControllerBase.publishActivityDestroyed(activity)
        }

        override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle?) {
        }
    }
}