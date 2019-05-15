package com.makentoshe.vkinternship.player

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.makentoshe.vkinternship.Mp3FilesHolder


/**
 * Service for starting and holding audio player.
 */
class PlayerService : Service() {

    /**
     * Contains all audio files.
     */
    private lateinit var filesHolder: Mp3FilesHolder

    private lateinit var mediaPlayer: ExoPlayer

    override fun onCreate() {
        mediaPlayer = ExoPlayerFactory.newSimpleInstance(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //stop service if intent is null or does not contains command
        if (intent == null || !intent.hasExtra(Commands::class.java.simpleName)) {
            stopSelf(startId)
            return super.onStartCommand(intent, flags, startId)
        }
        //extract command
        val command = intent.getSerializableExtra(Commands::class.java.simpleName) as Commands
        //execute command
        when (command) {
            is Commands.NewCommand -> onNewCommand(command)
            is Commands.PlayCommand -> onPlayCommand(command)
            is Commands.PauseCommand -> onPauseCommand(command)
            is Commands.NextCommand -> onNextCommand()
            is Commands.PrevCommand -> onPrevCommand()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?) = null

    private fun onNewCommand(command: Commands.NewCommand) {
        filesHolder = Mp3FilesHolder(command.directory)
        val bytes = filesHolder.current.readBytes()
        val mediaSource = ByteArrayMediaSourceFactory(bytes).build()
        mediaPlayer.prepare(mediaSource)

        onPlayCommand(Commands.PlayCommand())
    }

    private fun onPlayCommand(command: Commands.PlayCommand) {
        mediaPlayer.playWhenReady = true
        sendCallback(command)
    }

    private fun onPauseCommand(command: Commands.PauseCommand) {
        mediaPlayer.playWhenReady = false
        sendCallback(command)
    }

    private fun onNextCommand() {
        println("Next")
    }

    private fun onPrevCommand() {
        println("Prev")
    }

    /**
     * Send a [commands] callback to the [PlayerBroadcastReceiver].
     */
    private fun sendCallback(commands: Commands) {
        val intent = Intent(Commands::class.java.simpleName)
        intent.putExtra(Commands::class.java.simpleName, commands)
        sendBroadcast(intent)
    }
}
