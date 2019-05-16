package com.makentoshe.vkinternship.player.commandexec

import com.google.android.exoplayer2.ExoPlayer

class PauseCommandExecutor : CommandExecutor {
    override fun exec(mediaPlayer: ExoPlayer) {
        mediaPlayer.playWhenReady = false
    }
}