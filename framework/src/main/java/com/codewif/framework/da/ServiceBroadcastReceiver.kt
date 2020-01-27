package com.codewif.framework.da

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ServiceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        ServiceRepository.codewifServiceTerminated()
    }
}