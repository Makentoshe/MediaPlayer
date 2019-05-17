package com.makentoshe.vkinternship.layout.backdrop

import android.os.Bundle
import android.os.Parcelable

/**
 * Class for saving and restoring backdrop state.
 */
class BackdropSaveRestoreMechanism(private val defaultState: BackdropBehavior.DropState) {

    fun onSave(state: BackdropBehavior.DropState): Parcelable {
        val bundle = Bundle()
        bundle.putSerializable(STATE, state)
        return bundle
    }

    fun onRestore(parcelable: Parcelable?): BackdropBehavior.DropState {
        val bundle: Bundle? = parcelable as? Bundle
        val state: BackdropBehavior.DropState? = bundle?.getSerializable(STATE) as? BackdropBehavior.DropState
        return state ?: defaultState
    }

    companion object {
        private const val STATE = "BackdropSaveRestoreMechanism"
    }
}