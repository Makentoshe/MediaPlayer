package com.makentoshe.vkinternship

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.updateLayoutParams
import com.makentoshe.vkinternship.backdrop.BackdropBehavior
import com.makentoshe.vkinternship.backdrop.getBackdropBehavior
import java.io.File

class MainActivity : AppCompatActivity() {

    private val permission = android.Manifest.permission.READ_EXTERNAL_STORAGE
    private val permissionRequestCode = 1
    private val directoryPathRequest = 2

    private val folderButton by lazy { findViewById<Button>(R.id.get_folder_button) }

    private val coordinatorLayout by lazy {
        findViewById<CoordinatorLayout>(R.id.activity_main_coordinator)
    }

    private val foreground by lazy {
        findViewById<CardView>(R.id.activity_main_foreground)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        folderButton.setOnClickListener {
            if (checkPermissionsGranted()) displayDirectoryChooseDialog() else grantPermissions()
        }

        foreground.visibility = View.GONE
        val behavior = coordinatorLayout.getBackdropBehavior()
        behavior.addOnDropListener { state ->
            //background visible
            if (state == BackdropBehavior.DropState.OPEN) {
                foreground.setOnTouchListener(null)
                foreground.setOnClickListener { behavior.close(true) }
            }
            //background hide
            if (state == BackdropBehavior.DropState.CLOSE) {
                foreground.setOnClickListener(null)
                setOnTouchListenerForeground()
            }
        }
    }

    private fun grantPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(permission), permissionRequestCode)
    }

    private fun checkPermissionsGranted(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(this, permission)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == permissionRequestCode) {
            onReadExternalStoragePermissionResult(permissions, grantResults)
        }
    }

    private fun onReadExternalStoragePermissionResult(permissions: Array<out String>, grantResults: IntArray) {
        //check all permissions was granted
        permissions.forEachIndexed { index, _ ->
            if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                return Toast.makeText(this, "Permissions was denied", Toast.LENGTH_LONG).show()
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

        val directory = File(path).also {
            //returns from method if current path is not a directory
            if (!it.isDirectory) return
        }

        displayPlayerActivity(directory)
    }

    private fun displayPlayerActivity(directory: File) {
        coordinatorLayout.getBackdropBehavior().close(false)
        foreground.updateLayoutParams<ViewGroup.LayoutParams> {
            val point = Point()
            windowManager.defaultDisplay.getSize(point)
            height = point.y
        }
        foreground.visibility = View.VISIBLE
        setOnTouchListenerForeground()
    }

    private fun setOnTouchListenerForeground() {
        foreground.setOnTouchListener(object : OnSwipeTouchListener(this) {
            override fun onSwipeRight() = Unit
            override fun onSwipeLeft() = Unit
            override fun onSwipeTop() = Unit

            override fun onSwipeBottom() {
                coordinatorLayout.getBackdropBehavior().open(true)
            }
        })
    }

}
