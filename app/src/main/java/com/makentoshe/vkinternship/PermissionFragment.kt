package com.makentoshe.vkinternship

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * Fragment for working with the permissions.
 * Checks permissions and if they granted - starts new activity_for_result attached
 * to base activity which can be called from "requireActivity" method or using "activity" property.
 */
class PermissionFragment : Fragment() {

    private val permission = android.Manifest.permission.READ_EXTERNAL_STORAGE
    private val permissionCode = 39

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (checkPermissionsGranted()) showDirectoryChooseActivity() else grantPermissions()
    }

    private fun checkPermissionsGranted(): Boolean {
        val permissionState = ContextCompat.checkSelfPermission(requireContext(), permission)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun grantPermissions() {
        requestPermissions(arrayOf(permission), permissionCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == permissionCode) onReadExternalStoragePermissionResult(permissions, grantResults)
    }

    private fun onReadExternalStoragePermissionResult(permissions: Array<out String>, grantResults: IntArray) {
        //check all permissions was granted
        permissions.forEachIndexed { index, permission ->
            if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                return Toast.makeText(requireContext(), "Permission $permission was denied", Toast.LENGTH_LONG).show()
            }
        }

        showDirectoryChooseActivity()
    }

    private fun showDirectoryChooseActivity() {
        //start new activity
        val intent = Intent(requireActivity(), DirectoryChooseActivity::class.java)
        ActivityCompat.startActivityForResult(requireActivity(), intent, REQUEST_CODE, null)
        //and remove current fragment
        requireFragmentManager().beginTransaction().remove(this).commit()
    }

    companion object {
        const val REQUEST_CODE = 2
    }
}