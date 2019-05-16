package com.makentoshe.vkinternship.player.commandexec

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.makentoshe.vkinternship.player.CallbackSender
import com.makentoshe.vkinternship.player.Commands

class CallbackCommandExecutor(private val callback: CallbackSender) : CommandExecutor {

    override fun exec(mediaPlayer: ExoPlayer) {
        exec(mediaPlayer.playWhenReady, mediaPlayer.playbackState)
    }

    fun exec(isPlaying: Boolean, state: Int) {
        if (isPlaying) callback.send(Commands.PlayCommand) else callback.send(Commands.PauseCommand)
        if (state == Player.STATE_IDLE) callback.send(Commands.IdleStateCommand)
        if (state == Player.STATE_ENDED) callback.send(Commands.PauseCommand)
    }
}