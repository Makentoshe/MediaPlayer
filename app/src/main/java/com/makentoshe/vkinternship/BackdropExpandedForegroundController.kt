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
import kotlin.math.roundToInt

/**
 * Controller for the foreground layout while it is expanded
 */
class BackdropExpandedForegroundController(private val behavior: BackdropBehavior, private val foreground: View) {

    private val context = foreground.context

    private val playerServiceController = PlayerServiceController()

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
        foreground.findViewById<View>(R.id.activity_main_foreground_show_play).setOnClickListener {
            playerServiceController.pausePlaying(context)
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

    private fun Context.dip(dp: Int): Int {
        return TypedValue
            .applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics)
            .roundToInt()
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