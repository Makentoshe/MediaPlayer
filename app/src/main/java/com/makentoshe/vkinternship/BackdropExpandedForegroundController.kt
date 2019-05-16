package com.makentoshe.vkinternship

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.makentoshe.vkinternship.backdrop.BackdropBehavior
import com.makentoshe.vkinternship.player.PlayerServiceController
import com.makentoshe.vkinternship.player.PlayerServiceListener
import com.makentoshe.vkinternship.player.PlayerServiceListenerController
import kotlin.math.roundToInt

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
        val color = ContextCompat.getColor(context, R.color.MaterialLightBlue700)
        //set color filter for repeat icon
        foreground.findViewById<ImageView>(R.id.activity_main_foreground_show_repeat_icon)
            .setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        //set color filter for white dropdown arrow
        foreground.findViewById<ImageView>(R.id.activity_main_foreground_show_dropdown_icon)
            .setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP)
        //set on click listener for dropdown arrow
        foreground.findViewById<View>(R.id.activity_main_foreground_show_dropdown).setOnClickListener {
            behavior.open(true)
        }
        //controller for the play/pause button
        PlayPauseButtonController(foreground).bindController(controller)
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

    private fun Context.dip(dp: Int) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics).roundToInt()

    private class OnSwipeTouchListenerBackdrop(
        context: Context, private val behavior: BackdropBehavior
    ) : OnSwipeTouchListener(context) {
        override fun onSwipeRight() = Unit
        override fun onSwipeLeft() = Unit
        override fun onSwipeTop() = Unit
        override fun onSwipeBottom() = behavior.open(true)
    }

    private class PlayPauseButtonController(foreground: View) {
        private val view = foreground.findViewById<View>(R.id.activity_main_foreground_show_play)
        private val icon = foreground.findViewById<ImageView>(R.id.activity_main_foreground_show_play_icon)
        private val context = foreground.context
        private val playerServiceController = PlayerServiceController(context)

        fun bindController(controller: PlayerServiceListenerController) {
            controller.addListener(PlayerListener())
        }

        private inner class PlayerListener : PlayerServiceListener {

            override fun onPlayerPause() {
                icon.setImageDrawable(context.getDrawable(R.drawable.ic_play_48))
                view.setOnClickListener { playerServiceController.startPlaying() }
            }

            override fun onPlayerPlay() {
                icon.setImageDrawable(context.getDrawable(R.drawable.ic_pause_48))
                view.setOnClickListener { playerServiceController.pausePlaying() }
            }

            override fun onPlayerIdle() = Unit
        }
    }
}