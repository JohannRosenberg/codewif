package com.codewif.framework.ui.utils

import com.codewif.framework.R
import com.codewif.framework.ui.main.CodewifMainActivity
import com.codewif.shared.App.Companion.ctx
import com.codewif.shared.utils.NotificationsBase


/**
 * Provides utility functions to display various kinds of messages: Snackbar, AlertDialog, Statusbar notifications, etc.
 */
class Notifications :
    NotificationsBase<CodewifMainActivity>(
        ctx.getString(R.string.app_name),
        "channelIdCodewifLib",
        "channelNameCodewifLib",
        CodewifMainActivity::class.java
    )