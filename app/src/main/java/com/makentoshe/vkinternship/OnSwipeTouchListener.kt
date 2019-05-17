package com.makentoshe.vkinternship

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener

abstract class OnSwipeTouchListener(ctx: Context) : OnTouchListener {

    private val gestureDetector: GestureDetector = GestureDetector(ctx, GestureListener(this))

    override fun onTouch(v: View, event: MotionEvent) = gestureDetector.onTouchEvent(event)

    open fun onSwipeRight() = Unit
    open fun onSwipeLeft() = Unit
    open fun onSwipeTop() = Unit
    open fun onSwipeBottom() = Unit
}

class GestureListener(private val onSwipeTouchListener: OnSwipeTouchListener) : SimpleOnGestureListener() {

    override fun onDown(e: MotionEvent) = true

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        var result = false
        try {
            val diffY = e2.y - e1.y
            val diffX = e2.x - e1.x
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) onSwipeTouchListener.onSwipeRight() else onSwipeTouchListener.onSwipeLeft()
                    result = true
                }
            } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) onSwipeTouchListener.onSwipeBottom() else onSwipeTouchListener.onSwipeTop()
                result = true
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        return result
    }

    companion object {
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }
}
