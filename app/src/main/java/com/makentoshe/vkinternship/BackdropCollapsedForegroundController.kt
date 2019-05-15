package com.makentoshe.vkinternship

import android.graphics.PorterDuff
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.makentoshe.vkinternship.backdrop.BackdropBehavior
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
        //get color for buttons
        val color = ContextCompat.getColor(context, R.color.MaterialLightBlue700)
        //set a color filter and a listener for the play/pause button
        foreground.findViewById<ImageView>(R.id.pause).apply {
            setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            setOnClickListener { println("Pause") }
        }
        //set a color filter and a listener for the skip button
        foreground.findViewById<ImageView>(R.id.skip).apply {
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
}