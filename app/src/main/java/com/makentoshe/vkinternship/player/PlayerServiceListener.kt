package com.makentoshe.vkinternship.player

import com.google.android.exoplayer2.Player
import java.io.File

/**
 * Listener for player events.
 */
interface PlayerServiceListener {

    /**
     * Calls when player is paused.
     */
    fun onPlayerPause()

    /**
     * Calls when player is starts playing.
     */
    fun onPlayerPlay()

    /**
     * Calls when player has not any media to play
     */
    fun onPlayerIdle()

    /**
     * Calls when a new media file was started to play
     */
    fun onNextMedia(file: File, player: Player?)
}