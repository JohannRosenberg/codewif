package com.codewif.framework.ui.main

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.*
import com.codewif.framework.R
import com.codewif.framework.ui.ActivityConstants
import com.codewif.framework.ui.base.OnFragmentInteractionListener
import com.codewif.shared.eventBus.EventBusControllerBase
import com.codewif.shared.utils.security.PERMISSIONS_REQUEST_SD_READ_WRITE
import com.google.android.material.navigation.NavigationView


class CodewifMainActivity : AppCompatActivity(), OnFragmentInteractionListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cw_codewif_activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.tests_fragment, R.id.test_results_fragment
            ), drawerLayout
        )

        // Set up Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        setupActionBarWithNavController(navController, appBarConfiguration)

        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        navigationView.setupWithNavController(navController)

        if (intent.getBooleanExtra(ActivityConstants.BUNDLE_PARAM_NAVIGATE_TO_TEST_RESULTS_SCREEN, false)) {
            navController.navigate(R.id.test_results_fragment)
        }
    }

    override fun onResume() {
        super.onResume()
        EventBusControllerBase.publishActivityResumed(this)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onFragmentInteraction(uri: Uri) {
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_SD_READ_WRITE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    EventBusControllerBase.publishPermissionsResponse()
                }
            }
        }
    }
}
