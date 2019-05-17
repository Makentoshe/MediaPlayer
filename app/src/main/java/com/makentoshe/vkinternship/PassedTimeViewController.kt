package com.makentoshe.vkinternship

import android.widget.TextView
import com.makentoshe.vkinternship.layout.CustomTimeBar
import java.util.concurrent.TimeUnit

class PassedTimeViewController(private val timeView: TextView) {

    fun bindToTimeBar(timeBar: CustomTimeBar) {
        timeBar.addPositionChangedListener { milliseconds ->
            val seconds = calcSeconds(milliseconds)
            val minutes = calcMinutes(milliseconds)
            val total = StringBuilder().append(minutes).append(":").append(seconds)
            timeView.text = total
        }
    }

    private fun calcSeconds(milliseconds: Long): String {
        val seconds = (TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60).toString()
        return if (seconds.length == 1) "0".plus(seconds) else seconds
    }

    private fun calcMinutes(milliseconds: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds).toString()
        return if (minutes.length == 1) "0".plus(minutes) else minutes
    }
}