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
     * Calls when player is playing.
     */
    fun onPlayerPlay()
}