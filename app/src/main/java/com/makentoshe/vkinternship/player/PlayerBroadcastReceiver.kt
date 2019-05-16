package com.makentoshe.vkinternship.player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Class performs a callback function from the [PlayerService].
 */
class PlayerBroadcastReceiver : BroadcastReceiver(), PlayerServiceListenerController {

    private val listeners = ArrayList<PlayerServiceListener>()

    override fun addListener(l: PlayerServiceListener) {
        listeners.add(l)
    }

    override fun removeListener(l: PlayerServiceListener) {
        listeners.remove(l)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || !intent.hasExtra(Commands::class.java.simpleName)) return
        val command = intent.getSerializableExtra(Commands::class.java.simpleName) as Commands

        when (command) {
            is Commands.PlayCommand -> listeners.forEach { it.onPlayerPlay() }
            is Commands.PauseCommand -> listeners.forEach { it.onPlayerPause() }
            is Commands.IdleStateCommand -> listeners.forEach { it.onPlayerIdle() }
        }
    }
}