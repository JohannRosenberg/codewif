package com.codewif.service.ui

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codewif.service.CodewifService
import com.codewif.service.R
import com.codewif.shared.eventBus.EventBusControllerBase
import com.codewif.shared.utils.security.PERMISSIONS_REQUEST_SD_READ_WRITE
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_terminate_service.setOnClickListener {
            CodewifService.terminateService()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        EventBusControllerBase.publishActivityResumed(this)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_SD_READ_WRITE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    EventBusControllerBase.publishPermissionsResponse()
                }
            }
        }

        finish()
    }
}
