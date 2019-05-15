package com.makentoshe.vkinternship.player

/**
 * Controller for [PlayerServiceListener]
 */
interface PlayerServiceListenerController {

    fun addListener(l: PlayerServiceListener)

    fun removeListener(l: PlayerServiceListener)
}