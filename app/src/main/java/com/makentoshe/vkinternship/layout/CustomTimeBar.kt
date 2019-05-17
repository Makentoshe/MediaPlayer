package com.makentoshe.vkinternship.layout

import android.content.Context
import android.util.AttributeSet
import com.google.android.exoplayer2.ui.DefaultTimeBar

class CustomTimeBar(context: Context, attrs: AttributeSet) : DefaultTimeBar(context, attrs) {

    private val listeners = ArrayList<(Long) -> Unit>()

    fun addPositionChangedListener(l: (Long) -> Unit) = listeners.add(l)

    fun removePositionChangedListener(l: (Long) -> Unit) = listeners.remove(l)

    override fun setPosition(position: Long) {
        super.setPosition(position)
        listeners.forEach { it(position) }
    }
}