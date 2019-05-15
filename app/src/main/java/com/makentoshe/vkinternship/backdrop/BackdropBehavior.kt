package com.makentoshe.vkinternship.backdrop

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.coordinatorlayout.widget.CoordinatorLayout

class BackdropBehavior(context: Context, attrs: AttributeSet) : CoordinatorLayout.Behavior<View>(context, attrs) {

    private val saveRestoreMechanism = BackdropSaveRestoreMechanism(DropState.CLOSE)

    private val backdropListenerController = BackdropListenerController()

    private lateinit var backdropLayoutsController: BackdropLayoutsController

    fun setLayouts(foreground: View, background: View) {
        backdropLayoutsController = BackdropLayoutsController(foreground, background)
    }

    enum class DropState {
        OPEN, CLOSE
    }

    companion object {
        private const val DEFAULT_DURATION = 300L
        private const val WITHOUT_DURATION = 0L
    }

    private var backLayoutId: Int? = null

    private var backLayout: ViewGroup? = null
    private var frontLayout: View? = null

//    private var closedIconId: Int = R.drawable.ic_menu
//    private var openedIconRes: Int = R.drawable.ic_close

    private var dropState: DropState = DropState.CLOSE

    private var needToInitializing = true


    override fun onSaveInstanceState(parent: CoordinatorLayout, child: View): Parcelable {
        return saveRestoreMechanism.onSave(dropState)
    }

    override fun onRestoreInstanceState(parent: CoordinatorLayout, child: View, state: Parcelable) {
        super.onRestoreInstanceState(parent, child, state)
        dropState = saveRestoreMechanism.onRestore(state)
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        if (backLayoutId == null) return false

        return dependency.id == backLayoutId
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        //tries to match the foreground layout
        matchForegroundLayout(child)
        //tries to match the background layout
        matchBackgroundLayout(dependency)

        if (frontLayout != null && backLayout != null && needToInitializing) {
            initViews(parent, frontLayout!!, backLayout!!)
        }

        return super.onDependentViewChanged(parent, child, dependency)
    }

    /**
     * Matches [view] to the [frontLayout] or throws an exception.
     */
    private fun matchForegroundLayout(view: View) {
        frontLayout = view as? ViewGroup ?: throw IllegalArgumentException("BackLayout must extend a ViewGroup")
    }

    private fun matchBackgroundLayout(view: View) {
        if (view.id == backLayoutId) {
            backLayout = view as? ViewGroup ?: throw IllegalAccessException("backLayoutId doesn't match back layout")
        }
    }

//    fun setOpenedIcon(@IdRes iconRes: Int) {
//        this.openedIconRes = iconRes
//    }
//
//    fun setClosedIcon(@IdRes iconRes: Int) {
//        this.closedIconId = iconRes
//    }

    fun attachBackLayout(@IdRes appBarLayoutId: Int) {
        this.backLayoutId = appBarLayoutId
    }

    fun addOnDropListener(listener: (DropState) -> Unit) {
        backdropListenerController.addListener(listener)
    }

    fun removeDropListener(listener: (DropState) -> Unit) {
        backdropListenerController.removeListener(listener)
    }

    fun clearAllListeners() {
        backdropListenerController.clear()
    }

    fun open(withAnimation: Boolean = true): Boolean = if (dropState == DropState.OPEN) {
        false
    } else {
        dropState = DropState.OPEN
        if (backLayout != null && frontLayout != null) {
            drawDropState(frontLayout!!, backLayout!!, withAnimation)
        } else {
            throw IllegalArgumentException("Toolbar and backContainer must be initialized")
        }
        backdropListenerController.notify(DropState.OPEN)
        true
    }

    fun close(withAnimation: Boolean = true): Boolean = if (dropState == DropState.CLOSE) {
        false
    } else {
        dropState = DropState.CLOSE
        if (backLayout != null && frontLayout != null) {
            drawDropState(frontLayout!!, backLayout!!, withAnimation)
        } else {
            throw IllegalArgumentException("Toolbar and backContainer must be initialized")
        }
        backdropListenerController.notify(DropState.CLOSE)
        true
    }

    private fun initViews(parent: CoordinatorLayout, frontLayout: View, backLayout: View) {
        frontLayout.layoutParams.height = parent.height - calculateTopPosition(backLayout).toInt()
        drawDropState(frontLayout, backLayout, false)

        needToInitializing = false
    }

    private fun drawDropState(frontLayout: View, backContainer: View, withAnimation: Boolean = true) {
        when (dropState) {
            DropState.CLOSE -> {
                drawClosedState(frontLayout, backContainer, withAnimation)
//                toolbar.setNavigationIcon(closedIconId)
            }
            DropState.OPEN -> {
                drawOpenedState(frontLayout, backContainer, withAnimation)
//                toolbar.setNavigationIcon(openedIconRes)
            }
        }
    }

    private fun drawClosedState(frontLayout: View, backLayout: View, withAnimation: Boolean = true) {
        val position = calculateTopPosition(backLayout)
        val duration = if (withAnimation) DEFAULT_DURATION else WITHOUT_DURATION

        frontLayout.animate().y(position).setDuration(duration).start()
    }

    private fun drawOpenedState(frontLayout: View, backLayout: View, withAnimation: Boolean = true) {
        val position = calculateBottomPosition(backLayout)
        val duration = if (withAnimation) DEFAULT_DURATION else WITHOUT_DURATION

        frontLayout.animate().y(position).setDuration(duration).start()
    }

    private fun calculateTopPosition(backLayout: View): Float {
        return backLayout.y
    }

    private fun calculateBottomPosition(backLayout: View): Float {
        return backLayout.y + backLayout.height - 200
    }
}

