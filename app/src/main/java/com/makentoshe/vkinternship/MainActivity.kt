package com.makentoshe.vkinternship

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.makentoshe.vkinternship.backdrop.getBackdropBehavior
import com.makentoshe.vkinternship.player.Commands
import com.makentoshe.vkinternship.player.PlayerBroadcastReceiver

class MainActivity : AppCompatActivity() {

    private lateinit var foregroundController: BackdropForegroundController

    private lateinit var backgroundController: BackdropBackgroundController

    private val playerBroadcastReceiver = PlayerBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val coordinatorLayout = findViewById<CoordinatorLayout>(R.id.activity_main_coordinator)
        val behavior = coordinatorLayout.getBackdropBehavior()

        //foreground layout controller
        val foreground = findViewById<CardView>(R.id.activity_main_foreground)
        foregroundController = BackdropForegroundController(behavior, foreground, playerBroadcastReceiver)

        //background layout controller
        val background = findViewById<View>(R.id.activity_main_background)
        backgroundController = BackdropBackgroundController(behavior, background, supportFragmentManager)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PermissionFragment.REQUEST_CODE) {
            PathResultExtractor(foregroundController).start(this, data)
        }
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

