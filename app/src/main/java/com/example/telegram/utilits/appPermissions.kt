package com.example.telegram.utilits

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat

const val READ_CONTACT = Manifest.permission.READ_CONTACTS
const val PERMISSION_REQUEST_CODE = 200

fun checkPermissions(permissions: String): Boolean {
    return if (Build.VERSION.SDK_INT >= 23
        && ContextCompat.checkSelfPermission(APP_ACTIVITY, permissions) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(APP_ACTIVITY, arrayOf(permissions), PERMISSION_REQUEST_CODE)
        false
    } else true
}