package com.codewif.shared.service

const val CODEWIF_SERVICE_PACKAGE_NAME = "com.codewif.service"
const val CODEWIF_SERVICE_CLASS_NAME = "$CODEWIF_SERVICE_PACKAGE_NAME.CodewifService"

// Service commands
const val SERVICE_CMD_GET_UI_TESTS = 1
const val SERVICE_CMD_STORE_UI_TESTS = 2
const val SERVICE_CMD_SEND_TEST_RESULTS_TO_BACKEND = 3

// Service bundle parameters
const val SERVICE_BUNDLE_KEY_DATA_TO_SERVICE = "dataToService"
const val SERVICE_BUNDLE_KEY_UI_TESTS = "uiTests"

// Broadcast Receiver actions
const val BROADCAST_ACTION_SERVICE_TERMINATED = "com.codewif.service.ACTION_SERVICE_TERMINATED"







