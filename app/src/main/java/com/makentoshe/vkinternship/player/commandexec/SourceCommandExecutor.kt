package com.makentoshe.vkinternship.player.commandexec

import com.google.android.exoplayer2.ExoPlayer
import com.makentoshe.vkinternship.player.ByteArrayMediaSourceFactory
import com.makentoshe.vkinternship.player.CallbackSender
import com.makentoshe.vkinternship.player.Commands
import java.io.File

class SourceCommandExecutor(private val file: File, private val callback: CallbackSender) : CommandExecutor {

    override fun exec(mediaPlayer: ExoPlayer) {
        //create mediasource from byte array
        val mediaSource = ByteArrayMediaSourceFactory(file.readBytes()).build()
        //put source to the player
        mediaPlayer.prepare(mediaSource)
        //start playing
        mediaPlayer.playWhenReady = true
        //send callback - media file
        callback.send(Commands.FileCommand(file))
        //send callback - playing was started
        callback.send(Commands.PlayCommand)
    }
}