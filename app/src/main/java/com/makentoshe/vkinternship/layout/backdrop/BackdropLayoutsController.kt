package com.makentoshe.vkinternship.layout.backdrop

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.makentoshe.vkinternship.dip

/**
 * todo add animator class
 * Performs controlling foreground and background views from [layouts] instance.
 */
class BackdropLayoutsController(private val layouts: BackdropLayouts) {

    companion object {
        private const val DEFAULT_DURATION = 300L
        private const val WITHOUT_DURATION = 0L
    }

    private var isInitialized = false

    /**
     * Compares [dependency] id and background layout id.
     */
    fun isDependsOn(dependency: View): Boolean {
        return dependency.id == layouts.background.id
    }

    /**
     * Performs views initialization. Calls at once.
     */
    fun onDependentViewChanged(parent: CoordinatorLayout, state: BackdropBehavior.DropState) {
        if (isInitialized.not()) {
            layouts.foreground.layoutParams.height = parent.height - layouts.background.y.toInt()
            drawDropState(state, false)
            isInitialized = true
        }
    }

    /**
     * Sets a new state for the layouts.
     */
    fun drawDropState(newState: BackdropBehavior.DropState, withAnimation: Boolean = true) {
        when (newState) {
            BackdropBehavior.DropState.CLOSE -> drawClosedState(withAnimation)
            BackdropBehavior.DropState.OPEN -> drawOpenedState(withAnimation)
        }
    }

    /**
     * Performs layouts manipulations and animates it.
     */
    private fun drawClosedState(withAnimation: Boolean = true) {
        val foreground = layouts.foreground
        val background = layouts.background

        val position = background.y
        val duration = if (withAnimation) DEFAULT_DURATION else WITHOUT_DURATION

        foreground.animate().y(position).setDuration(duration).start()
    }

    /**
     * Performs layouts manipulations and animates it.
     */
    private fun drawOpenedState(withAnimation: Boolean = true) {
        val foreground = layouts.foreground
        val background = layouts.background
        val bottomOffsetPx = foreground.context.dip(60)

        val position = background.y + background.height - bottomOffsetPx
        val duration = if (withAnimation) DEFAULT_DURATION else WITHOUT_DURATION

        foreground.animate().y(position).setDuration(duration).start()
    }
}