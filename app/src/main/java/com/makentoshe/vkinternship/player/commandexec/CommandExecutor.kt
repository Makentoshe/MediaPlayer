package com.makentoshe.vkinternship.player.commandexec

import com.google.android.exoplayer2.ExoPlayer

interface CommandExecutor {
    fun exec(mediaPlayer: ExoPlayer)
}