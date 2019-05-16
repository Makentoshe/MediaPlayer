package com.makentoshe.vkinternship.player

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
}