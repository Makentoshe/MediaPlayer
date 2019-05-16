package com.makentoshe.vkinternship

import android.graphics.PorterDuff
import android.media.MediaMetadataRetriever
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.Player
import com.google.android.material.card.MaterialCardView
import com.makentoshe.vkinternship.backdrop.BackdropBehavior
import com.makentoshe.vkinternship.player.PlayerServiceController
import com.makentoshe.vkinternship.player.PlayerServiceListener
import com.makentoshe.vkinternship.player.PlayerServiceListenerController
import java.io.File

/**
 * Controller for the foreground layout while it is collapsed.
 */
class BackdropCollapsedForegroundController(
    private val behavior: BackdropBehavior, private val foreground: View, controller: PlayerServiceListenerController
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
        val playerServiceController = PlayerServiceController(context)
        val extractor = MetadataExtractor(MediaMetadataRetriever())
        PlayPauseButtonController(foreground, extractor, playerServiceController).bindController(controller)
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

    private class PlayPauseButtonController(
        private val foreground: View,
        private val extractor: MetadataExtractor,
        private val controller: PlayerServiceController
    ) {
        private val view = foreground.findViewById<View>(R.id.activity_main_foreground_hide_play)
        private val icon = foreground.findViewById<ImageView>(R.id.activity_main_foreground_hide_play_icon)
        private val context = foreground.context

        init {
            val color = ContextCompat.getColor(context, R.color.MaterialLightBlue700)
            icon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }

        fun bindController(controller: PlayerServiceListenerController) {
            controller.addListener(PlayerListener())
        }

        private inner class PlayerListener : PlayerServiceListener {

            override fun onPlayerPause() {
                icon.setImageDrawable(context.getDrawable(R.drawable.ic_play_48))
                view.setOnClickListener { controller.startPlaying() }
            }

            override fun onPlayerPlay() {
                icon.setImageDrawable(context.getDrawable(R.drawable.ic_pause_48))
                view.setOnClickListener { controller.pausePlaying() }
            }

            override fun onNextMedia(file: File, player: Player?) {
                extractor.extract(file)

                val titleView = foreground.findViewById<TextView>(R.id.activity_main_foreground_hide_title)
                extractor.setTitle(titleView)

                val coverView = foreground.findViewById<ImageView>(R.id.activity_main_foreground_hide_cover_image)
                extractor.setCover(coverView)
            }

            override fun onPlayerIdle() = Unit
        }
    }
}