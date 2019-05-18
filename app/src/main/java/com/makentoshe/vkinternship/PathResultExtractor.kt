package com.makentoshe.vkinternship

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.makentoshe.vkinternship.player.PlayerServiceController
import java.io.File

/**
 * Class performs directory extracting from intent's extra and sends it to the PlayerService.
 */
class PathResultExtractor(private val foregroundController: BackdropForegroundController) {

    fun start(context: Context, data: Intent?): Boolean {
        if (data == null || !data.hasExtra(String::class.java.simpleName)) return false

        val directory = File(data.getStringExtra(String::class.java.simpleName))

        if (!directory.isDirectory) {
            Toast.makeText(context, R.string.selected_directory_does_not_exist, Toast.LENGTH_LONG).show()
            return false
        }

        if (!hasAtLeastOneMP3File(directory)) {
            Toast.makeText(context, R.string.selected_directory_is_empty, Toast.LENGTH_LONG).show()
            return false
        }

        //start service
        val controller = PlayerServiceController(context)
        val file = File(data.getStringExtra(String::class.java.simpleName))
        controller.selectNewDirectory(file)
        //update foreground layout
        foregroundController.onUpdate()
        return true
    }

    private fun hasAtLeastOneMP3File(directory: File): Boolean {
        return directory.listFiles().any { it.extension == "mp3" }
    }
}