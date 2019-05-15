package com.makentoshe.vkinternship.player

import java.io.File
import java.io.Serializable

sealed class Commands: Serializable {
    data class NewCommand(val directory: File): Commands()
    class PlayCommand : Commands()
    class PauseCommand : Commands()
    class NextCommand : Commands()
    class PrevCommand: Commands()
}