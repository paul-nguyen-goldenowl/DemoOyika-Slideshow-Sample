package com.example.oyika_slideshow_sample

import PermissionRequester
import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

val AppCompatActivity.writePermission: PermissionRequester
    get() = PermissionRequester(
        this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        onDenied = {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        },
        onShowRationale = { toast("Please grant access to download images") })

fun AppCompatActivity.toast(msg: String){
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}