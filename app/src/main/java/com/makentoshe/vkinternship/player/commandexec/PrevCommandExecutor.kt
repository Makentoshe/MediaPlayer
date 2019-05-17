package com.makentoshe.vkinternship.player.commandexec

import com.google.android.exoplayer2.ExoPlayer
import com.makentoshe.vkinternship.Mp3FilesHolder
import com.makentoshe.vkinternship.player.CallbackSender

class PrevCommandExecutor(private val holder: Mp3FilesHolder, private val callback: CallbackSender) : CommandExecutor {
    override fun exec(mediaPlayer: ExoPlayer) {
        val current = holder.listToPrev()
        val prev = holder.prev
        val next = holder.next
        SourceCommandExecutor(prev, current, next, callback).exec(mediaPlayer)
    }
}
