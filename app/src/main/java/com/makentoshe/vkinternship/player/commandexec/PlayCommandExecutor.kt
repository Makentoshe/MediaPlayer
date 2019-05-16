package com.makentoshe.vkinternship.player.commandexec

import com.google.android.exoplayer2.ExoPlayer

class PlayCommandExecutor : CommandExecutor {
    override fun exec(mediaPlayer: ExoPlayer) {
        mediaPlayer.playWhenReady = true
    }
}