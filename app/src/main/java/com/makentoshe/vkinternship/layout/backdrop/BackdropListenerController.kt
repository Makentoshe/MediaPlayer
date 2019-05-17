package com.makentoshe.vkinternship.layout.backdrop

/**
 * Class for management backdrop events.
 */
class BackdropListenerController {

    private val listeners = ArrayList<(BackdropBehavior.DropState) -> Unit>()

    fun addListener(l: (BackdropBehavior.DropState) -> Unit) {
        listeners.add(l)
    }

    fun removeListener(l: (BackdropBehavior.DropState) -> Unit) {
        listeners.remove(l)
    }

    fun clear() = listeners.clear()

    fun notify(newState: BackdropBehavior.DropState) {
        listeners.forEach { it.invoke(newState) }
    }
}