package com.makentoshe.vkinternship

import android.graphics.PorterDuff
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.makentoshe.vkinternship.backdrop.BackdropBehavior
import com.makentoshe.vkinternship.player.PlayerServiceController
import com.makentoshe.vkinternship.player.PlayerServiceListener
import com.makentoshe.vkinternship.player.PlayerServiceListenerController

/**
 * Controller for the foreground layout while it is collapsed.
 */
class BackdropCollapsedForegroundController(
    private val behavior: BackdropBehavior,
    private val foreground: View,
    controller: PlayerServiceListenerController
) {

    private val context = foreground.context

    /**
     * Main collapsed layout displays when the foreground layout is in collapsed state.
     */
    private val primaryLayout by lazy {
        foreground.findViewById<View>(R.id.activity_main_foreground_hide)
    }

    /**
     * Displays when the foreground layout is in expanded state.
     */
    private val secondaryLayout by lazy {
        foreground.findViewById<View>(R.id.activity_main_foreground_show)
    }

    init {
        PlayPauseButtonController(foreground).bindController(controller)
        //get color for buttons
        val color = ContextCompat.getColor(context, R.color.MaterialLightBlue700)
        //set a color filter and a listener for the skip button
        foreground.findViewById<ImageView>(R.id.activity_main_foreground_hide_next_icon).apply {
            setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            setOnClickListener { println("Skip") }
        }
    }

    /**
     * Displays main layout and hides secondary. Also updates listeners.
     */
    fun display() {
        foreground.setOnTouchListener(null)
        foreground.setOnClickListener { behavior.close(true) }
        primaryLayout.visibility = View.VISIBLE
        secondaryLayout.visibility = View.GONE
        //remove corners
        (foreground as MaterialCardView).radius = 0f
    }

    private class PlayPauseButtonController(foreground: View) {
        private val view = foreground.findViewById<View>(R.id.activity_main_foreground_hide_play)
        private val icon = foreground.findViewById<ImageView>(R.id.activity_main_foreground_hide_play_icon)
        private val context = foreground.context
        private val playerServiceController = PlayerServiceController(context)

        init {
            val color = ContextCompat.getColor(context, R.color.MaterialLightBlue700)
            icon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }

        fun bindController(controller: PlayerServiceListenerController) {
            controller.addListener(object : PlayerServiceListener {
                override fun onPlayerPause() = this@PlayPauseButtonController.onPlayerPause()
                override fun onPlayerPlay() = this@PlayPauseButtonController.onPlayerPlay()
            })
        }

        private fun onPlayerPause() {
            icon.setImageDrawable(context.getDrawable(R.drawable.ic_play_48))
            view.setOnClickListener { playerServiceController.startPlaying() }
        }

        private fun onPlayerPlay() {
            icon.setImageDrawable(context.getDrawable(R.drawable.ic_pause_48))
            view.setOnClickListener { playerServiceController.pausePlaying() }
        }
    }
}