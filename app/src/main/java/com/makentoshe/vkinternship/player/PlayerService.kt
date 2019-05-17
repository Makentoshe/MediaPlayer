package com.makentoshe.vkinternship.player

import android.app.Service
import android.content.Intent
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.makentoshe.vkinternship.Mp3FilesHolder
import com.makentoshe.vkinternship.player.commandexec.CallbackCommandExecutor
import com.makentoshe.vkinternship.player.commandexec.NextCommandExecutor
import com.makentoshe.vkinternship.player.commandexec.PrevCommandExecutor
import com.makentoshe.vkinternship.player.commandexec.SourceCommandExecutor


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

        Companion.mediaPlayer = mediaPlayer

        mediaPlayer.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playWhenReady) callback.send(Commands.PlayCommand) else callback.send(Commands.PauseCommand)
                if (playbackState == Player.STATE_IDLE) callback.send(Commands.IdleStateCommand)
                if (playbackState == Player.STATE_ENDED) callback.send(Commands.PauseCommand)
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
            SourceCommandExecutor(filesHolder.prev, filesHolder.current, filesHolder.next, callback).exec(mediaPlayer)
        }
        /* Next element */
        is Commands.NextCommand -> {
            NextCommandExecutor(filesHolder, callback).exec(mediaPlayer)
        }
        /* Prev element */
        is Commands.PrevCommand -> {
            PrevCommandExecutor(filesHolder, callback).exec(mediaPlayer)
        }
        /* Request a current state */
        is Commands.CallbackCommand -> {
            val holder = if (!this::filesHolder.isInitialized) null else filesHolder
            CallbackCommandExecutor(holder, callback).exec(mediaPlayer)
        }

        else -> Unit
    }

    override fun onBind(intent: Intent?) = null

    override fun onDestroy() {
        Companion.mediaPlayer = null
        mediaPlayer.release()
    }

    companion object {
        var mediaPlayer: Player? = null
    }
}
