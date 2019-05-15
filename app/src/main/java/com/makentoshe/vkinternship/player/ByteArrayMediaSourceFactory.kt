package com.makentoshe.vkinternship.player

import android.net.Uri
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.ByteArrayDataSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import java.io.ByteArrayInputStream
import java.net.URL
import java.net.URLConnection
import java.net.URLStreamHandler

/**
 * Creates MediaSource from ByteArray
 */
class ByteArrayMediaSourceFactory(private val bytes: ByteArray) {

    private val dataSource = ByteArrayDataSource(bytes)

    private val uri by lazy {
        val url = URL(null, "bytes:///" + "audio", ByteHandler(bytes))
        return@lazy Uri.parse(url.toURI().toString())
    }

    fun build(): MediaSource {
        val dataSpec = DataSpec(uri)
        dataSource.open(dataSpec)
        val factory = DataSource.Factory { dataSource }
        return ExtractorMediaSource.Factory(factory).createMediaSource(uri)
    }

    private class ByteHandler(private val bytes: ByteArray) : URLStreamHandler() {
        override fun openConnection(u: URL) = ByteConnection(u, bytes)
    }

    private class ByteConnection(url: URL, private val bytes: ByteArray) : URLConnection(url) {
        override fun connect() = Unit
        override fun getInputStream() = ByteArrayInputStream(bytes)
    }

}