package com.makentoshe.vkinternship

import android.content.Context
import android.media.MediaMetadataRetriever
import android.view.View
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.makentoshe.vkinternship.layout.CustomTimeBar
import com.makentoshe.vkinternship.layout.backdrop.BackdropBehavior
import com.makentoshe.vkinternship.player.PlayerServiceController
import com.makentoshe.vkinternship.player.PlayerServiceListenerController
import com.makentoshe.vkinternship.player.SimplePlayerServiceListener
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
        val serviceController = PlayerServiceController(context)

        //set on click listener for dropdown arrow
        foreground.findViewById<View>(R.id.activity_main_foreground_show_dropdown).setOnClickListener {
            behavior.open(true)
        }

        val extractor = MetadataExtractor(MediaMetadataRetriever())
        controller.addListener(ExpandedPlayerServiceListener(primaryLayout, extractor))

        val nextButton = primaryLayout.findViewById<View>(R.id.activity_main_foreground_show_next)
        nextButton.setOnClickListener { serviceController.selectNextFile() }

        val prevButton = primaryLayout.findViewById<View>(R.id.activity_main_foreground_show_prev_icon)
        prevButton.setOnClickListener { serviceController.selectPrevFile() }


        val timeBar = foreground.findViewById<CustomTimeBar>(R.id.exo_progress)

        val remainedTimeView =
            primaryLayout.findViewById<TextView>(R.id.activity_main_foreground_show_controller_remainedtime)
        RemainedTimeViewController(remainedTimeView).bindToTimeBar(timeBar)

        val passedTimeView =
            primaryLayout.findViewById<TextView>(R.id.activity_main_foreground_show_controller_passedtime)
        PassedTimeViewController(passedTimeView).bindToTimeBar(timeBar)

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
        override fun onSwipeBottom() = behavior.open(true)
    }

    private class ExpandedPlayerServiceListener(
        private val primaryLayout: View, private val extractor: MetadataExtractor
    ) : SimplePlayerServiceListener() {

        override fun onNextMedia(file: File) {
            extractor.extract(file)

            val authorView = primaryLayout.findViewById<TextView>(R.id.activity_main_foreground_show_author)
            extractor.setAuthor(authorView)

            val titleView = primaryLayout.findViewById<TextView>(R.id.activity_main_foreground_show_title)
            extractor.setTitle(titleView)
        }
    }
}
