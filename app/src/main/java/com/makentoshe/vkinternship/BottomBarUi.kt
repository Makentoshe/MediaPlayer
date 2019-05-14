package com.makentoshe.vkinternship

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat

class BottomBarUi(root: View) {

    private val pauseButton by lazy { root.findViewById<ImageView>(R.id.pause) }

    private val skipNext by lazy { root.findViewById<ImageView>(R.id.skip) }

    private val bottomBar by lazy { root.findViewById<View>(R.id.bottomBar) }

    fun setBottomBarState(context: Context) {
        val buttonsColor = ContextCompat.getColor(context, R.color.MaterialLightBlue700)
        pauseButton.setColorFilter(buttonsColor)
        skipNext.setColorFilter(buttonsColor)
        bottomBar.visibility = View.GONE
    }
}