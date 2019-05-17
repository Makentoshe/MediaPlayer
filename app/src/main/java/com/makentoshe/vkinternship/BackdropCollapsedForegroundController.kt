package com.makentoshe.vkinternship

import android.media.MediaMetadataRetriever
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.makentoshe.vkinternship.layout.backdrop.BackdropBehavior
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
        val extractor = MetadataExtractor(MediaMetadataRetriever())
        controller.addListener(object : PlayerServiceListener {
            override fun onPlayerPause() = Unit
            override fun onPlayerPlay() = Unit
            override fun onPlayerIdle() = Unit
            override fun onNextMedia(file: File) {
                extractor.extract(file)

                val titleView = primaryLayout.findViewById<TextView>(R.id.activity_main_foreground_hide_title)
                extractor.setTitle(titleView)

                val coverView = primaryLayout.findViewById<ImageView>(R.id.activity_main_foreground_hide_cover_image)
                extractor.setCover(coverView)
            }
        })

        val nextButton = primaryLayout.findViewById<View>(R.id.activity_main_foreground_hide_next)
        nextButton.setOnClickListener { PlayerServiceController(primaryLayout.context).selectNextFile() }
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