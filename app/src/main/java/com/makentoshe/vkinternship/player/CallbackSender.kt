package com.makentoshe.vkinternship.player

import android.content.Context
import android.content.Intent

class CallbackSender(private val context: Context) {

    fun send(commands: Commands) {
        val intent = Intent(Commands::class.java.simpleName)
        intent.putExtra(Commands::class.java.simpleName, commands)
        context.sendBroadcast(intent)
    }
}