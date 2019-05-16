package com.makentoshe.vkinternship

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.makentoshe.vkinternship.backdrop.getBackdropBehavior
import com.makentoshe.vkinternship.player.Commands
import com.makentoshe.vkinternship.player.PlayerBroadcastReceiver
import com.makentoshe.vkinternship.player.PlayerServiceController
import java.io.File

class MainActivity : AppCompatActivity() {

    private val folderButton by lazy { findViewById<Button>(R.id.get_folder_button) }

    private val foreground by lazy { findViewById<CardView>(R.id.activity_main_foreground) }

    private lateinit var foregroundController: BackdropForegroundController

    private val playerBroadcastReceiver = PlayerBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //foreground layout controller
        val coordinatorLayout = findViewById<CoordinatorLayout>(R.id.activity_main_coordinator)
        val behavior = coordinatorLayout.getBackdropBehavior()
        foregroundController = BackdropForegroundController(behavior, foreground, playerBroadcastReceiver)

        //is first creation?
        if (savedInstanceState == null) foreground.visibility = View.GONE

        //background layout controller
        folderButton.setOnClickListener {
            //request permission using fragment
            supportFragmentManager.beginTransaction().add(PermissionFragment(), String()).commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PermissionFragment.REQUEST_CODE) {
            onDirectoryPathChooseResult(resultCode, data!!)
        }
    }

    private fun onDirectoryPathChooseResult(resultCode: Int, data: Intent) {
        if (!data.hasExtra(String::class.java.simpleName)) return
        val path = data.getStringExtra(String::class.java.simpleName)

        val directory = File(path)

        //returns from method if current path is not a directory
        if (!directory.isDirectory) {
            val message = "Выбраной директории не существует, или она является файлом"
            return Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
        if (!hasAtLeastOneMP3File(directory)) {
            val message = "В выбранной директории отсутствуют mp3 файлы"
            return Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

        displayPlayer(data)
    }

    private fun hasAtLeastOneMP3File(directory: File): Boolean {
        return directory.listFiles().any { it.extension == "mp3" }
    }

    private fun displayPlayer(data: Intent) {
        val controller = PlayerServiceController()
        val file = File(data.getStringExtra(String::class.java.simpleName))
        controller.selectNewDirectory(this, file)
        foregroundController.onUpdate()
    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter(Commands::class.java.simpleName)
        registerReceiver(playerBroadcastReceiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(playerBroadcastReceiver)
    }
}

