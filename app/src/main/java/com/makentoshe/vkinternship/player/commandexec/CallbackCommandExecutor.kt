package com.makentoshe.vkinternship.player.commandexec

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.makentoshe.vkinternship.Mp3FilesHolder
import com.makentoshe.vkinternship.player.CallbackSender
import com.makentoshe.vkinternship.player.Commands

class CallbackCommandExecutor(
    private val holder: Mp3FilesHolder?, private val callback: CallbackSender
) : CommandExecutor {

    override fun exec(mediaPlayer: ExoPlayer) {
        if (mediaPlayer.playWhenReady) callback.send(Commands.PlayCommand) else callback.send(Commands.PauseCommand)
        if (mediaPlayer.playbackState == Player.STATE_IDLE) callback.send(Commands.IdleStateCommand)
        if (mediaPlayer.playbackState == Player.STATE_ENDED) callback.send(Commands.PauseCommand)
        if (holder != null) callback.send(Commands.FileCommand(holder.prev, holder.current, holder.next))
    }
}