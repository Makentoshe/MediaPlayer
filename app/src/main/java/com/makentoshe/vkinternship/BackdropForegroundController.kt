package com.makentoshe.vkinternship

import android.graphics.Point
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerControlView
import com.makentoshe.vkinternship.layout.backdrop.BackdropBehavior
import com.makentoshe.vkinternship.player.PlayerService
import com.makentoshe.vkinternship.player.PlayerServiceController
import com.makentoshe.vkinternship.player.PlayerServiceListener
import com.makentoshe.vkinternship.player.PlayerServiceListenerController
import java.io.File

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
        val context = foreground.context
        behavior.addOnDropListener { state ->
            if (state == BackdropBehavior.DropState.OPEN) collapsedForegroundController.display()
            if (state == BackdropBehavior.DropState.CLOSE) expandedForegroundController.display()
        }
        /* special mechanism used after device rotation
           just requests the current player state (play or pause)
           and returns data using broadcast callback. */
        PlayerServiceController(context).requestStateCallbacks()

        controller.addListener(ForegroundServiceListener())

        PlayerService.mediaPlayerRef.get()?.let { bindPlayer(it) }
    }

    private fun bindPlayer(player: Player) {
        foreground.findViewById<PlayerControlView>(R.id.activity_main_foreground_show_controller).player = player
        foreground.findViewById<PlayerControlView>(R.id.activity_main_foreground_hide_controller).player = player
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

    private inner class ForegroundServiceListener: PlayerServiceListener {
        override fun onPlayerPause() = updateLayoutParams()
        override fun onPlayerPlay() = updateLayoutParams()
        override fun onNextMedia(prev: File, curr: File, next: File) = PlayerService.mediaPlayerRef.get()?.let { bindPlayer(it) } ?: Unit
        override fun onPlayerIdle() = foreground.setVisibility(View.GONE)
    }

}
