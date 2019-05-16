package com.makentoshe.vkinternship.player

import android.app.Service
import android.content.Intent
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.PlayerControlView
import com.makentoshe.vkinternship.Mp3FilesHolder
import com.makentoshe.vkinternship.player.commandexec.*


/**
 * Service for starting and holding audio player.
 */
class PlayerService : Service() {

    private val callback = CallbackSender(this)

    /**
     * Contains all audio files.
     */
    private lateinit var filesHolder: Mp3FilesHolder

    private lateinit var mediaPlayer: ExoPlayer

    override fun onCreate() {
        mediaPlayer = ExoPlayerFactory.newSimpleInstance(this)

        mediaPlayer.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                CallbackCommandExecutor(callback).exec(playWhenReady, playbackState)
            }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //stop service if intent is null or does not contains command
        if (intent == null || !intent.hasExtra(Commands::class.java.simpleName)) {
            stopSelf(startId)
            return super.onStartCommand(intent, flags, startId)
        }
        //extract command
        val command = intent.getSerializableExtra(Commands::class.java.simpleName) as Commands

        execute(command)

        return super.onStartCommand(intent, flags, startId)
    }

    private fun execute(command: Commands) = when (command) {
        /* A new playlist added */
        is Commands.SourceCommand -> {
            filesHolder = Mp3FilesHolder(command.directory)
            SourceCommandExecutor(filesHolder, callback).exec(mediaPlayer)
        }
        /* Start playing */
        is Commands.PlayCommand -> {
            PlayCommandExecutor(filesHolder, callback).exec(mediaPlayer)
        }
        /* Pause playing */
        is Commands.PauseCommand -> {
            PauseCommandExecutor().exec(mediaPlayer)
        }
        /* Next element */
        is Commands.NextCommand -> {
            NextCommandExecutor().exec(mediaPlayer)
        }
        /* Prev element */
        is Commands.PrevCommand -> {
            PrevCommandExecutor().exec(mediaPlayer)
        }
        /* Request a current state */
        is Commands.CallbackCommand -> {
            CallbackCommandExecutor(callback).exec(mediaPlayer)
        }

        else -> Unit
    }

    override fun onBind(intent: Intent?) = null
}
