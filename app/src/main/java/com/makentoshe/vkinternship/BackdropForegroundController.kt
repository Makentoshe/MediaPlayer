package com.makentoshe.vkinternship

import android.graphics.Point
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import com.makentoshe.vkinternship.backdrop.BackdropBehavior
import com.makentoshe.vkinternship.player.PlayerServiceListenerController

/**
 * Controller for the foreground layout of the backdrop layout
 */
class BackdropForegroundController(
    private val behavior: BackdropBehavior,
    private val foreground: View,
    private val controller: PlayerServiceListenerController
) {

    /**
     * Controller for the foreground layout while it is expanded
     */
    private val expandedForegroundController = BackdropExpandedForegroundController(behavior, foreground, controller)

    /**
     * Controller for the foreground layout while it is collapsed
     */
    private val collapsedForegroundController = BackdropCollapsedForegroundController(behavior, foreground, controller)

    init {
        behavior.addOnDropListener { state ->
            if (state == BackdropBehavior.DropState.OPEN) collapsedForegroundController.display()
            if (state == BackdropBehavior.DropState.CLOSE) expandedForegroundController.display()
        }
    }

    /**
     * Calls when a new directory was choose.
     */
    fun onUpdate() {
        //show foreground
        behavior.close(false)
        //set foreground visible
        foreground.visibility = View.VISIBLE
        updateLayoutParams()

        expandedForegroundController.display()
    }

    /**
     * Set a new layout params for a foreground layout
     */
    private fun updateLayoutParams() = foreground.updateLayoutParams<ViewGroup.LayoutParams> {
        val point = Point()
        (foreground.context as AppCompatActivity).windowManager.defaultDisplay.getSize(point)
        height = point.y
    }

}
