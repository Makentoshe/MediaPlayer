package com.makentoshe.vkinternship.player

import java.io.File
import java.io.Serializable

sealed class Commands: Serializable {

    data class NewCommand(val directory: File): Commands()
    object PlayCommand : Commands()
    object PauseCommand : Commands()
    object NextCommand : Commands()
    object PrevCommand : Commands()

    /** Command for requesting a current player state */
    object CallbackCommand : Commands()
}