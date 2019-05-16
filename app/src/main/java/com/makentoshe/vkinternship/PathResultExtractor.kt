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

    fun start(context: Context, data: Intent?) {
        if (data == null || !data.hasExtra(String::class.java.simpleName)) return

        val directory = File(data.getStringExtra(String::class.java.simpleName))

        if (!directory.isDirectory) {
            val message = "Выбраной директории не существует, или она является файлом"
            return Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        if (!hasAtLeastOneMP3File(directory)) {
            val message = "В выбранной директории отсутствуют mp3 файлы"
            return Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        //start service
        val controller = PlayerServiceController()
        val file = File(data.getStringExtra(String::class.java.simpleName))
        controller.selectNewDirectory(context, file)
        //update foreground layout
        foregroundController.onUpdate()
    }

    private fun hasAtLeastOneMP3File(directory: File): Boolean {
        return directory.listFiles().any { it.extension == "mp3" }
    }
}