package com.codewif.framework.utils

import java.util.concurrent.TimeUnit


/**
 * Formats a time in milliseconds to hh:mm:ss.ms
 */
fun Long?.formatToDuration(): String {
    if (this == null)
        return ""

    val hours = TimeUnit.MILLISECONDS.toHours(this).toInt() % 24
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this).toInt() % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this).toInt() % 60
    val decimalSeconds =
        (TimeUnit.MILLISECONDS.toMillis(this) - ((hours * 60 * 60 * 1000) + (minutes * 60 * 1000) + seconds * 1000)) / 1000f + seconds
    return when {
        hours > 0 -> String.format("%d:%02d:%.3f", hours, minutes, decimalSeconds)
        minutes > 0 -> String.format("%02d:%.3f", minutes, decimalSeconds)
        seconds > 0 -> String.format("00:%.3f", decimalSeconds)
        else -> {
            "00:00"
        }
    }
}