package com.makentoshe.vkinternship

import android.view.View
import android.widget.Button
import androidx.fragment.app.FragmentManager
import com.makentoshe.vkinternship.backdrop.BackdropBehavior

class BackdropBackgroundController(
    private val behavior: BackdropBehavior, private val background: View, private val fragmentManager: FragmentManager
) {

    private val folderButton by lazy { background.findViewById<Button>(R.id.get_folder_button) }

    init {
        //background layout controller
        folderButton.setOnClickListener {
            //request permission using fragment
            fragmentManager.beginTransaction().add(PermissionFragment(), String()).commit()
        }
    }
}