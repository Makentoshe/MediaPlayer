package com.makentoshe.vkinternship.player.commandexec

import com.google.android.exoplayer2.ExoPlayer
import com.makentoshe.vkinternship.Mp3FilesHolder
import com.makentoshe.vkinternship.player.CallbackSender

class PrevCommandExecutor(private val holder: Mp3FilesHolder, private val callback: CallbackSender) : CommandExecutor {
    override fun exec(mediaPlayer: ExoPlayer) {
        val file = holder.listToPrev()
        SourceCommandExecutor(file, callback).exec(mediaPlayer)
    }
}
