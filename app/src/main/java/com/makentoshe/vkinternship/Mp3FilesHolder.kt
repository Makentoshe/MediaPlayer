package com.makentoshe.vkinternship

import java.io.File
import java.util.*

/**
 * Contains all mp3 files in the queue.
 */
class Mp3FilesHolder(directory: File) {

    private val dequeue = ArrayDeque<File>()

    /**
     * Returns the current file
     */
    var current: File
        private set

    init {
        //fill dequeue with the files
        directory.listFiles().filter { it.extension == "mp3" }.forEach(dequeue::addLast)
        //put first element to the current
        current = dequeue.removeFirst()
    }

    /**
     * Returns the next file
     */
    val next: File
        get() = if (dequeue.size == 0) current else try {
            dequeue.first
        } catch (e: NoSuchElementException) {
            dequeue.last
        }


    fun listToNext(): File {
        //put current file to the end of the queue
        dequeue.addLast(current)
        //get file from the head of the queue
        current = dequeue.removeFirst()
        return current
    }

    /**
     * Returns the previous file
     */
    val prev: File
        get() = if (dequeue.size == 0) current else try {
            dequeue.last
        } catch (e: NoSuchElementException) {
            dequeue.first
        }

    fun listToPrev(): File {
        //put current file to the head of the queue
        dequeue.addFirst(current)
        //get file from the tail of the queue
        current = dequeue.removeLast()
        return current
    }
}