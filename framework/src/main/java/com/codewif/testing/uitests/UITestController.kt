package com.codewif.testing.uitests

import android.app.Activity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.codewif.framework.R
import com.codewif.shared.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class UITestController {
    companion object : CoroutineScope by CoroutineScope(Dispatchers.Main) {
        private var navController: NavController

        init {
            navController = Navigation.findNavController(App.currentActivity as Activity, R.id.nav_host_fragment)
        }

        fun displayTestsScreen() {
            navController.navigate(R.id.tests_fragment)
        }

        fun displayTestResultsScreen() {
            navController.navigate(R.id.test_results_fragment)
        }
    }
}