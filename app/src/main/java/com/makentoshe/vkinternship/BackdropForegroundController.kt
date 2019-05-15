package com.makentoshe.vkinternship

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.PorterDuff
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import com.google.android.material.card.MaterialCardView
import com.makentoshe.vkinternship.backdrop.BackdropBehavior
import kotlin.math.roundToInt

/**
 * Controller for the foreground layout of the backdrop layout
 */
class BackdropForegroundController(private val behavior: BackdropBehavior, private val foreground: View) {

    /**
     * Displayed layout when a foreground is expanded and a background is not visible.
     */
    private val foregroundShowLayout by lazy {
        foreground.findViewById<View>(R.id.activity_main_foreground_show)
    }

    /**
     * Displayed layout when a foreground is collapsed and a background is visible
     */
    private val foregroundHideLayout by lazy {
        foreground.findViewById<View>(R.id.activity_main_foreground_hide)
    }

    /**
     * Calls when a foreground layout must be initialized
     */
    fun init(context: Context) {
        initForegroundShow(context)
        initForegroundHide(context)
        behavior.addOnDropListener { state ->
            if (state == BackdropBehavior.DropState.OPEN) onForegroundHide(behavior, context)
            if (state == BackdropBehavior.DropState.CLOSE) onForegroundShow(behavior, context)
        }
    }

    private fun initForegroundShow(context: Context) {
        val color = ContextCompat.getColor(context, R.color.MaterialLightBlue700)

        foreground.findViewById<ImageView>(R.id.activity_main_foreground_show_repeat_icon)
            .setColorFilter(color, PorterDuff.Mode.SRC_ATOP)

        //set color filter for white dropdown arrow
        foreground.findViewById<ImageView>(R.id.activity_main_foreground_show_dropdown_icon)
            .setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP)
        //set on click listener for dropdown arrow
        foreground.findViewById<View>(R.id.activity_main_foreground_show_dropdown).setOnClickListener {
            behavior.open(true)
        }
    }

    private fun initForegroundHide(context: Context) {
        val color = ContextCompat.getColor(context, R.color.MaterialLightBlue700)

        foreground.findViewById<ImageView>(R.id.pause).apply {
            setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            setOnClickListener {
                println("Pause")
            }
        }
        foreground.findViewById<ImageView>(R.id.skip).apply {
            setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            setOnClickListener {
                println("Skip")
            }
        }
    }

    /**
     * Calls when new directory was choose
     */
    fun onDisplay(context: Context) {
        //show foreground
        behavior.close(false)
        //set new params for height to avoid rounded corners in the bottom
        updateLayoutParams(context)
        //set foreground visible
        foreground.visibility = View.VISIBLE

        onForegroundShow(behavior, context)
    }

    /**
     * Set a new layout params for a foreground layout
     */
    private fun updateLayoutParams(context: Context) = foreground.updateLayoutParams<ViewGroup.LayoutParams> {
        val point = Point()
        (context as AppCompatActivity).windowManager.defaultDisplay.getSize(point)
        height = point.y
    }

    /**
     * Listener for a top-to-bottom swipe action.
     */
    private fun setOnTouchListenerForeground(context: Context) {
        foreground.setOnTouchListener(object : OnSwipeTouchListener(context) {
            override fun onSwipeRight() = Unit
            override fun onSwipeLeft() = Unit
            override fun onSwipeTop() = Unit
            override fun onSwipeBottom() = behavior.open(true)
        })
    }

    /**
     * Calls when a dropdown layout state was changed from OPEN to CLOSE
     */
    private fun onForegroundHide(behavior: BackdropBehavior, context: Context) {
        foreground.setOnTouchListener(null)
        foreground.setOnClickListener { behavior.close(true) }
        foregroundHideLayout.visibility = View.VISIBLE
        foregroundShowLayout.visibility = View.GONE
        //remove corners
        (foreground as MaterialCardView).radius = 0f
    }

    /**
     * Calls when a dropdown layout state was changed from CLOSE to OPEN
     */
    private fun onForegroundShow(behavior: BackdropBehavior, context: Context) {
        foreground.setOnClickListener(null)
        setOnTouchListenerForeground(context)
        foregroundHideLayout.visibility = View.GONE
        foregroundShowLayout.visibility = View.VISIBLE
        //set corners
        (foreground as MaterialCardView).radius = context.dip(16).toFloat()
    }
}

fun Context.dip(dp: Int): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics).roundToInt()
}