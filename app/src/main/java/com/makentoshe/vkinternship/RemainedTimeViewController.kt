package com.makentoshe.vkinternship

import android.widget.TextView
import com.google.android.exoplayer2.C
import com.makentoshe.vkinternship.layout.CustomTimeBar
import com.makentoshe.vkinternship.player.PlayerService
import java.util.concurrent.TimeUnit

class RemainedTimeViewController(private val timeView: TextView) {

    fun bindToTimeBar(timeBar: CustomTimeBar) {
        timeBar.addPositionChangedListener { milliseconds ->
            val player = PlayerService.mediaPlayer ?: return@addPositionChangedListener
            val duration = player.duration
            if (duration == C.TIME_UNSET) return@addPositionChangedListener

            val seconds = calcSeconds(duration, milliseconds)
            val minutes = calcMinutes(duration, milliseconds)
            val total = StringBuilder("-").append(minutes).append(":").append(seconds).toString()
            timeView.text = total
        }
    }

    private fun calcSeconds(duration: Long, milliseconds: Long): String {
        val seconds = (TimeUnit.MILLISECONDS.toSeconds(duration - milliseconds) % 60).toString()
        return if (seconds.length == 1) "0".plus(seconds) else seconds
    }

    private fun calcMinutes(duration: Long, milliseconds: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(duration - milliseconds).toString()
        return if (minutes.length == 1) "0".plus(minutes) else minutes
    }
}