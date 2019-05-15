package com.makentoshe.vkinternship.backdrop

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.View.NO_ID
import androidx.coordinatorlayout.widget.CoordinatorLayout

/**
 * Main class imitates really backdrop layout using [CoordinatorLayout] and two child layouts.
 */
class BackdropBehavior(context: Context, attrs: AttributeSet) : CoordinatorLayout.Behavior<View>(context, attrs) {

    /**
     * Class for saving and restoring backdrop layout state.
     */
    private val saveRestoreMechanism = BackdropSaveRestoreMechanism(DropState.CLOSE)

    /**
     * Controller for listeners management.
     */
    private val backdropListenerController = BackdropListenerController()

    /**
     * Data class contains foreground and background layouts
     */
    private lateinit var backdropLayouts: BackdropLayouts

    /**
     * Backdrop layouts controller. Performs any actions with them such as close or open.
     */
    private lateinit var backdropLayoutsController: BackdropLayoutsController

    /**
     * Main method than must be called before starting work with the instance.
     */
    fun setLayouts(foreground: View, background: View) {
        if (foreground.id == NO_ID) throw IllegalArgumentException()
        if (background.id == NO_ID) throw IllegalArgumentException()

        backdropLayouts = BackdropLayouts(foreground, background)
        backdropLayoutsController = BackdropLayoutsController(backdropLayouts)
    }

    /**
     * Possible backdrop layout states.
     * [OPEN] - the background is visible
     * [CLOSE] - the background hides below the foreground.
     */
    enum class DropState {
        OPEN, CLOSE
    }

    /**
     * Contains current backdrop layout state. At start initializes with [DropState.CLOSE] value.
     */
    private var dropState: DropState = DropState.CLOSE

    override fun onSaveInstanceState(parent: CoordinatorLayout, child: View): Parcelable {
        return saveRestoreMechanism.onSave(dropState)
    }

    override fun onRestoreInstanceState(parent: CoordinatorLayout, child: View, state: Parcelable) {
        super.onRestoreInstanceState(parent, child, state)
        dropState = saveRestoreMechanism.onRestore(state)
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return backdropLayoutsController.isDependsOn(dependency)
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        backdropLayoutsController.onDependentViewChanged(parent, dropState)
        return super.onDependentViewChanged(parent, child, dependency)
    }

    /**
     * Adds a listener for the backdrop events.
     */
    fun addOnDropListener(listener: (DropState) -> Unit) {
        backdropListenerController.addListener(listener)
    }

    /**
     * Removes a listener for the backdrop events.
     */
    fun removeDropListener(listener: (DropState) -> Unit) {
        backdropListenerController.removeListener(listener)
    }

    /**
     * Remove all listeners for the backdrop events.
     */
    fun clearAllListeners() {
        backdropListenerController.clear()
    }

    /**
     * Method for displaying background layout.
     */
    fun open(withAnimation: Boolean = true) {
        if (dropState == DropState.OPEN) return
        dropState = DropState.OPEN
        backdropLayoutsController.drawDropState(DropState.OPEN, withAnimation)
        backdropListenerController.notify(DropState.OPEN)
    }

    /**
     * Method for hiding background layout below foreground
     */
    fun close(withAnimation: Boolean = true) {
        if (dropState == DropState.CLOSE) return
        dropState = DropState.CLOSE
        backdropLayoutsController.drawDropState(DropState.CLOSE, withAnimation)
        backdropListenerController.notify(DropState.CLOSE)
    }
}

fun CoordinatorLayout.getBackdropBehavior(): BackdropBehavior {
    val backgroundChild = getChildAt(0)
    val foregroundChild = getChildAt(1)
    val params = foregroundChild.layoutParams as CoordinatorLayout.LayoutParams
    val behavior = params.behavior as BackdropBehavior
    behavior.setLayouts(foregroundChild, backgroundChild)
    return behavior
}
