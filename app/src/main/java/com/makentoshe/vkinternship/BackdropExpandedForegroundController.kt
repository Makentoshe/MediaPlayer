package com.makentoshe.vkinternship

import android.content.Context
import android.view.View
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.material.card.MaterialCardView
import com.makentoshe.vkinternship.backdrop.BackdropBehavior
import com.makentoshe.vkinternship.player.Commands
import com.makentoshe.vkinternship.player.PlayerServiceController
import com.makentoshe.vkinternship.player.PlayerServiceListener
import com.makentoshe.vkinternship.player.PlayerServiceListenerController
import java.io.File

/**
 * Controller for the foreground layout while it is expanded
 */
class BackdropExpandedForegroundController(
    private val behavior: BackdropBehavior,
    private val foreground: View,
    private val controller: PlayerServiceListenerController
) {

    private val context = foreground.context

    /**
     * Main layout displays when the foreground layout is in expanded state.
     */
    private val primaryLayout by lazy {
        foreground.findViewById<View>(R.id.activity_main_foreground_show)
    }

    /**
     * Secondary layout should not displays when the foreground layout is in expanded state.
     */
    private val secondaryLayout by lazy {
        foreground.findViewById<View>(R.id.activity_main_foreground_hide)
    }

    init {
        //set on click listener for dropdown arrow
        foreground.findViewById<View>(R.id.activity_main_foreground_show_dropdown).setOnClickListener {
            behavior.open(true)
        }
    }

    /**
     * Displays main layout and hides secondary. Also updates listeners.
     */
    fun display() {
        foreground.setOnClickListener(null)
        primaryLayout.setOnTouchListener(OnSwipeTouchListenerBackdrop(context, behavior))
        primaryLayout.visibility = View.VISIBLE
        secondaryLayout.visibility = View.GONE
        //set corners
        (foreground as MaterialCardView).radius = context.dip(16).toFloat()
    }

    private class OnSwipeTouchListenerBackdrop(
        context: Context, private val behavior: BackdropBehavior
    ) : OnSwipeTouchListener(context) {
        override fun onSwipeRight() = Unit
        override fun onSwipeLeft() = Unit
        override fun onSwipeTop() = Unit
        override fun onSwipeBottom() = behavior.open(true)
    }

}
