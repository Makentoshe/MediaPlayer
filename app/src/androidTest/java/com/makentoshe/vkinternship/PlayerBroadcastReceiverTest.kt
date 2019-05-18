package com.makentoshe.vkinternship

import android.content.Intent
import androidx.test.platform.app.InstrumentationRegistry
import com.makentoshe.vkinternship.player.Commands
import com.makentoshe.vkinternship.player.PlayerBroadcastReceiver
import com.makentoshe.vkinternship.player.PlayerServiceListener
import com.makentoshe.vkinternship.player.PlayerServiceListenerController
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class PlayerBroadcastReceiverTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()

    private lateinit var receiver: PlayerBroadcastReceiver
    private lateinit var listener: PlayerServiceListenerController

    @Before
    fun init() {
        receiver = PlayerBroadcastReceiver()
        listener = receiver
    }

    @Test
    fun shouldCallOnPlayerIdle() {
        var condition = false
        listener.addListener(object : PlayerServiceListener {
            override fun onPlayerPause() = Unit
            override fun onPlayerPlay() = Unit
            override fun onPlayerIdle() {
                condition = true
            }

            override fun onNextMedia(prev: File, curr: File, next: File) = Unit
        })

        val intent = Intent().putExtra(Commands::class.java.simpleName, Commands.IdleStateCommand)
        receiver.onReceive(instrumentation.targetContext, intent)

        Thread.sleep(500)
        assertTrue(condition)
    }

    @Test
    fun shouldCallOnPlayerPause() {
        var condition = false
        listener.addListener(object : PlayerServiceListener {
            override fun onPlayerPause() {
                condition = true
            }
            override fun onPlayerPlay() = Unit
            override fun onPlayerIdle() = Unit

            override fun onNextMedia(prev: File, curr: File, next: File) = Unit
        })

        val intent = Intent().putExtra(Commands::class.java.simpleName, Commands.PauseCommand)
        receiver.onReceive(instrumentation.targetContext, intent)

        Thread.sleep(500)
        assertTrue(condition)
    }

    @Test
    fun shouldCallOnPlayerPlay() {
        var condition = false
        listener.addListener(object : PlayerServiceListener {
            override fun onPlayerPause() = Unit
            override fun onPlayerPlay() {
                condition = true
            }
            override fun onPlayerIdle() = Unit

            override fun onNextMedia(prev: File, curr: File, next: File) = Unit
        })

        val intent = Intent().putExtra(Commands::class.java.simpleName, Commands.PlayCommand)
        receiver.onReceive(instrumentation.targetContext, intent)

        Thread.sleep(500)
        assertTrue(condition)
    }

    @Test
    fun shouldCallOnNextMedia() {
        val file1 = File("a")
        val file2 = File("b")
        val file3 = File("c")
        var condition = false

        listener.addListener(object : PlayerServiceListener {
            override fun onPlayerPause() = Unit
            override fun onPlayerPlay() = Unit
            override fun onPlayerIdle() = Unit

            override fun onNextMedia(prev: File, curr: File, next: File) {
                assertEquals(prev, file1)
                assertEquals(curr, file2)
                assertEquals(next, file3)
                condition = true
            }
        })

        val intent = Intent().putExtra(Commands::class.java.simpleName, Commands.FileCommand(file1, file2, file3))
        receiver.onReceive(instrumentation.targetContext, intent)

        Thread.sleep(500)
        assertTrue(condition)
    }
}