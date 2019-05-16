package com.makentoshe.vkinternship.player.commandexec

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player.STATE_ENDED
import com.makentoshe.vkinternship.Mp3FilesHolder
import com.makentoshe.vkinternship.player.CallbackSender

class PlayCommandExecutor(mp3FilesHolder: Mp3FilesHolder, callback: CallbackSender) : CommandExecutor {

    private val sourceExecutor = SourceCommandExecutor(mp3FilesHolder.current, callback)

    override fun exec(mediaPlayer: ExoPlayer) {
        //if media file playing was ended
        if (mediaPlayer.playbackState == STATE_ENDED) {
            //repeat current media file
            sourceExecutor.exec(mediaPlayer)
        } else {
            //resume playing
            mediaPlayer.playWhenReady = true
        }
    }
}