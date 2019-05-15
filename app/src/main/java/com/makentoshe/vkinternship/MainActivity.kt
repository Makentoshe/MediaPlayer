package com.makentoshe.vkinternship

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import com.makentoshe.vkinternship.backdrop.BackdropBehavior
import java.io.File

class MainActivity : AppCompatActivity() {

    private val permission = android.Manifest.permission.READ_EXTERNAL_STORAGE
    private val permissionRequestCode = 1
    private val directoryPathRequest = 2

    private val bottomBarUi by lazy { BottomBarUi(window.decorView) }

    private val folderButton by lazy { findViewById<Button>(R.id.get_folder_button) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomBarUi.setBottomBarState(this)

        folderButton.setOnClickListener {
            if (checkPermissionsGranted()) displayDirectoryChooseDialog() else grantPermissions()
        }

        val coordinatorLayout = findViewById<CoordinatorLayout>(R.id.activity_main_coordinator)
        val behavior = coordinatorLayout.getBackdropBehavior()
        val foreground = findViewById<View>(R.id.activity_main_foreground)
        val background = findViewById<View>(R.id.activity_main_background)

        with(behavior) {
            // Add listener
            addOnDropListener {
                println(it)
            }
        }

        foreground.setOnClickListener {
            behavior.open(true)
        }

        background.setOnClickListener {
            behavior.close(true)
        }

        println("SAS")
    }

    private fun grantPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(permission), permissionRequestCode)
    }

    private fun checkPermissionsGranted(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(this, permission)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

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
        super.onActivityResult(requestCode, resultCode, data)
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
        //TODO
    }
}

fun CoordinatorLayout.getBackdropBehavior(): BackdropBehavior {
    val backgroundChild = getChildAt(0)
    val foregroundChild = getChildAt(1)
    val params = foregroundChild.layoutParams as CoordinatorLayout.LayoutParams
    val behavior = params.behavior as BackdropBehavior
    behavior.setLayouts(foregroundChild, backgroundChild)
    return behavior
}

fun <T : CoordinatorLayout.Behavior<*>> View.findBehavior(): T = layoutParams.run {
    if (this !is CoordinatorLayout.LayoutParams) throw IllegalArgumentException("View's layout params should be CoordinatorLayout.LayoutParams")

    (layoutParams as CoordinatorLayout.LayoutParams).behavior as? T
        ?: throw IllegalArgumentException("Layout's behavior is not current behavior")
}