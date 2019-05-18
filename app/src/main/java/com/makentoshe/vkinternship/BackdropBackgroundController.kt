package com.makentoshe.vkinternship

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Button
import androidx.fragment.app.FragmentManager
import com.makentoshe.vkinternship.layout.backdrop.BackdropBehavior

class BackdropBackgroundController(
    private val behavior: BackdropBehavior, private val background: View, private val fragmentManager: FragmentManager
) {

    private val folderButton by lazy { background.findViewById<Button>(R.id.activity_main_background_button) }

    init {
        //background layout controller
        folderButton.setOnClickListener {
            //request permission using fragment
            fragmentManager.beginTransaction().add(PermissionFragment(), String()).commit()
        }

        background.setOnTouchListener(OnSwipeTouchListenerImpl(background.context))
    }

    private class OnSwipeTouchListenerImpl(private val context: Context): OnSwipeTouchListener(context) {
        override fun onSwipeBottom() {
            (context as Activity).moveTaskToBack(false)
        }
    }
}