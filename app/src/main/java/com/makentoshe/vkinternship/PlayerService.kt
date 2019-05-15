package com.makentoshe.vkinternship

import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.io.File

/**
 * Service for starting and holding audio player.
 */
class PlayerService : Service() {

    /**
     * Contains all audio files.
     */
    private lateinit var filesHolder: Mp3FilesHolder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //stop service if intent is null or does not contains directory path
        if (intent == null || !intent.hasExtra(String::class.java.simpleName)) return startId.also { stopSelf(it) }
        //extract directory path and create a file instance
        val directory = File(intent.getStringExtra(String::class.java.simpleName))

        filesHolder = Mp3FilesHolder(directory)

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}