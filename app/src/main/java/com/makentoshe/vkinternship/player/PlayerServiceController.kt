package com.makentoshe.vkinternship.player

import android.content.Context
import android.content.Intent
import java.io.File

class PlayerServiceController {

    fun selectNewDirectory(context: Context, directory: File) {
        val intent = Intent(context, PlayerService::class.java)
        intent.putExtra(Commands::class.java.simpleName, Commands.NewCommand(directory))
        context.startService(intent)
    }

    fun startPlaying(context: Context) {
        val intent = Intent(context, PlayerService::class.java)
        intent.putExtra(Commands::class.java.simpleName, Commands.PlayCommand())
        context.startService(intent)
    }

    fun pausePlaying(context: Context) {
        val intent = Intent(context, PlayerService::class.java)
        intent.putExtra(Commands::class.java.simpleName, Commands.PauseCommand())
        context.startService(intent)
    }
}