package com.makentoshe.vkinternship

import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.File

class Mp3FilesHolderTest {

    private lateinit var dir: File

    @Before
    fun init() {
        dir = File("test")
        dir.mkdirs()
    }

    @Test
    fun shouldReturnsWithOneFile() {
        val file = File(dir, "test.mp3")
        file.createNewFile()

        val holder = Mp3FilesHolder(dir)
        val cur = holder.current
        val prev = holder.prev
        val next = holder.next
        assertEquals(cur, file)
        assertEquals(next, file)
        assertEquals(prev, file)
    }

    @Test
    fun shouldReturnsWithTwoFiles() {
        val file1 = File(dir, "test1.mp3")
        file1.createNewFile()

        val file2 = File(dir, "test2.mp3")
        file2.createNewFile()

        val holder = Mp3FilesHolder(dir)
        val cur = holder.current
        val prev = holder.prev
        val next = holder.next

        assertEquals(cur, file1)
        assertEquals(next, file2)
        assertEquals(prev, file2)
    }

    @Test
    fun shouldReturnsWithThreeAndMoreFiles() {
        val file1 = File(dir, "test1.mp3")
        file1.createNewFile()

        val file2 = File(dir, "test2.mp3")
        file2.createNewFile()

        val file3 = File(dir, "test3.mp3")
        file3.createNewFile()

        val holder = Mp3FilesHolder(dir)
        val cur = holder.current
        val prev = holder.prev
        val next = holder.next

        assertEquals(cur, file1)
        assertEquals(next, file2)
        assertEquals(prev, file3)
    }

    @After
    fun after() {
        dir.deleteRecursively()
    }
}