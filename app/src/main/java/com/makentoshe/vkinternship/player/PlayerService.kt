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
            /* A new playlist added */
            is Commands.NewCommand -> onNewCommand(command)
            /* Start playing */
            is Commands.PlayCommand -> onPlayCommand(command)
            /* Pause playing */
            is Commands.PauseCommand -> onPauseCommand(command)
            /* Next element */
            is Commands.NextCommand -> onNextCommand()
            /* Prev element */
            is Commands.PrevCommand -> onPrevCommand()
            /* Request a current state */
            is Commands.CallbackCommand -> onCallbackCommand()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?) = null

    /**
     * Extracts media files from directory and starts to play a first file.
     */
    private fun onNewCommand(command: Commands.NewCommand) {
        //extract files
        filesHolder = Mp3FilesHolder(command.directory)
        //get current(first) file bytes
        val bytes = filesHolder.current.readBytes()
        //create mediasource from byte array
        val mediaSource = ByteArrayMediaSourceFactory(bytes).build()
        //put source to the player
        mediaPlayer.prepare(mediaSource)
        //start playing
        mediaPlayer.playWhenReady = true
        //send callback - playing was started
        sendCallback(Commands.PlayCommand)
    }

    /**
     * Starts playing and return a callback
     */
    private fun onPlayCommand(command: Commands.PlayCommand) {
        mediaPlayer.playWhenReady = true
        sendCallback(command)
    }

    /**
     * Pauses playing and return a callback
     */
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
     * Check a current state and returns it callback
     */
    private fun onCallbackCommand() {
        if (mediaPlayer.playWhenReady) {
            sendCallback(Commands.PlayCommand)
        } else {
            sendCallback(Commands.PauseCommand)
        }
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
