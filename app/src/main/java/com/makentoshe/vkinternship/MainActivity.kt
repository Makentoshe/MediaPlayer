package com.makentoshe.vkinternship

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    private val permission = android.Manifest.permission.READ_EXTERNAL_STORAGE
    private val permissionRequestCode = 1

    private val bottomBarUi by lazy { BottomBarUi(window.decorView) }

    private val folderButton by lazy { findViewById<Button>(R.id.get_folder_button) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomBarUi.setBottomBarState(this)

        folderButton.setOnClickListener {
            if (checkPermissionsGranted()) displayDirectoryChooseDialog() else grantPermissions()
        }
    }

    private fun grantPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(permission), permissionRequestCode)
    }

    private fun checkPermissionsGranted(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(this, permission)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == permissionRequestCode) {
            onReadExternalStoragePermissionResult(permissions, grantResults)
        }
    }

    private fun onReadExternalStoragePermissionResult(
        permissions: Array<out String>, grantResults: IntArray
    ) {
        //check all permissions was granted
        permissions.forEachIndexed { index, _ ->
            if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Permissions was denied", Toast.LENGTH_LONG).show()
                return
            }
        }

        displayDirectoryChooseDialog()
    }

    private fun displayDirectoryChooseDialog() {
        //start dialog
    }
}
