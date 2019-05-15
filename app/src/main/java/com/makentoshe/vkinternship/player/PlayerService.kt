package com.makentoshe.vkinternship.player

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.makentoshe.vkinternship.Mp3FilesHolder
import java.io.File

/**
 * Service for starting and holding audio player.
 */
class PlayerService : Service() {

    /**
     * Contains all audio files.
     */
    private lateinit var filesHolder: Mp3FilesHolder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //stop service if intent is null or does not contains command
        if (intent == null || !intent.hasExtra(Commands::class.java.simpleName)) {
            stopSelf(startId)
            return super.onStartCommand(intent, flags, startId)
        }
        //extract command
        val command = intent.getSerializableExtra(Commands::class.java.simpleName) as Commands

        when (command) {
            is Commands.NewCommand -> onNewCommand(command)
            is Commands.PlayCommand -> onPlayCommand(command)
            is Commands.PauseCommand -> onPauseCommand(command)
            is Commands.NextCommand -> onNextCommand(command)
            is Commands.PrevCommand -> onPrevCommand(command)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun onNewCommand(command: Commands.NewCommand) {
        filesHolder = Mp3FilesHolder(command.directory)
        println("New directory ${command.directory}")
    }

    private fun onPlayCommand(command: Commands.PlayCommand) {
        println("Play")
    }

    private fun onPauseCommand(command: Commands.PauseCommand) {
        println("Pause")
    }

    private fun onNextCommand(command: Commands.NextCommand) {
        println("Next")
    }

    private fun onPrevCommand(command: Commands.PrevCommand) {
        println("Prev")
    }
}