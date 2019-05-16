package com.makentoshe.vkinternship.player

import java.io.File
import java.io.Serializable

sealed class Commands: Serializable {

    data class SourceCommand(val directory: File): Commands()
    object PlayCommand : Commands()
    object PauseCommand : Commands()
    object NextCommand : Commands()
    object PrevCommand : Commands()

    /** Command for requesting a current player state */
    object CallbackCommand : Commands()

    /**
     * A Callback-Command indicates that the player has not any media to play
     */
    object IdleStateCommand: Commands()

    /** A callback-command returns a current media file */
    data class FileCommand(val file: File): Commands()
}