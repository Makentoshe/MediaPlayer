package com.makentoshe.vkinternship.player

import android.content.Context
import android.content.Intent
import java.io.File

class PlayerServiceController(private val context: Context) {

    fun selectNewDirectory(directory: File) {
        val intent = Intent(context, PlayerService::class.java)
        intent.putExtra(Commands::class.java.simpleName, Commands.SourceCommand(directory))
        context.startService(intent)
    }

    fun selectNextFile() {
        val intent = Intent(context, PlayerService::class.java)
        intent.putExtra(Commands::class.java.simpleName, Commands.NextCommand)
        context.startService(intent)
    }

    fun selectPrevFile() {
        val intent = Intent(context, PlayerService::class.java)
        intent.putExtra(Commands::class.java.simpleName, Commands.PrevCommand)
        context.startService(intent)
    }

    /**
     * Method calls a PlayerService to return a current player state using broadcast callbacks.
     */
    fun requestStateCallbacks() {
        val intent = Intent(context, PlayerService::class.java)
        intent.putExtra(Commands::class.java.simpleName, Commands.CallbackCommand)
        context.startService(intent)
    }
}