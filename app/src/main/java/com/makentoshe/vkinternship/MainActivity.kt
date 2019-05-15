package com.makentoshe.vkinternship

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import com.makentoshe.vkinternship.backdrop.getBackdropBehavior
import java.io.File

class MainActivity : AppCompatActivity() {

    private val permission = android.Manifest.permission.READ_EXTERNAL_STORAGE
    private val permissionRequestCode = 1
    private val directoryPathRequest = 2

    private val folderButton by lazy { findViewById<Button>(R.id.get_folder_button) }

    private val foreground by lazy { findViewById<CardView>(R.id.activity_main_foreground) }

    private lateinit var foregroundController: BackdropForegroundController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //foreground layout controller
        val coordinatorLayout = findViewById<CoordinatorLayout>(R.id.activity_main_coordinator)
        val behavior = coordinatorLayout.getBackdropBehavior()
        foregroundController = BackdropForegroundController(behavior, foreground)

        //is first creation?
        if (savedInstanceState == null) foreground.visibility = View.GONE

        //background layout controller
        folderButton.setOnClickListener {
            if (checkPermissionsGranted()) displayDirectoryChooseDialog() else grantPermissions()
        }
    }

    private fun checkPermissionsGranted(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(this, permission)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun grantPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(permission), permissionRequestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == permissionRequestCode) {
            onReadExternalStoragePermissionResult(permissions, grantResults)
        }
    }

    private fun onReadExternalStoragePermissionResult(permissions: Array<out String>, grantResults: IntArray) {
        //check all permissions was granted
        permissions.forEachIndexed { index, permission ->
            if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                return Toast.makeText(this, "Permission $permission was denied", Toast.LENGTH_LONG).show()
            }
        }

        displayDirectoryChooseDialog()
    }

    private fun displayDirectoryChooseDialog() {
        val intent = Intent(this, DirectoryChooseActivity::class.java)
        startActivityForResult(intent, directoryPathRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == directoryPathRequest) {
            onDirectoryPathChooseResult(resultCode, data!!)
        }
    }

    private fun onDirectoryPathChooseResult(resultCode: Int, data: Intent) {
        if (!data.hasExtra(String::class.java.simpleName)) return
        val path = data.getStringExtra(String::class.java.simpleName)

        val directory = File(path)

        //returns from method if current path is not a directory
        if (!directory.isDirectory) {
            val message = "Выбраной директории не существует, или она является файлом"
            return Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
        if (!hasAtLeastOneMP3File(directory)) {
            val message = "В выбранной директории отсутствуют mp3 файлы"
            return Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

        displayPlayer(data)
    }

    private fun hasAtLeastOneMP3File(directory: File): Boolean {
        return directory.listFiles().any { it.extension == "mp3" }
    }

    private fun displayPlayer(data: Intent) {
        val intent = Intent(this, PlayerService::class.java)
        intent.putExtra(String::class.java.simpleName, data.getStringExtra(String::class.java.simpleName))
        startService(intent)
        foregroundController.onUpdate()
    }

}
