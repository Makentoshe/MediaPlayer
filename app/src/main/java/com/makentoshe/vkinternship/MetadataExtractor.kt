package com.makentoshe.vkinternship

import android.content.res.Resources
import android.media.MediaMetadataRetriever
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.graphics.drawable.toBitmap
import java.io.ByteArrayInputStream
import java.io.File

/**
 * Extracts data from file using [MediaMetadataRetriever].
 */
class MetadataExtractor(private val metadataRetriever: MediaMetadataRetriever) {

    fun extract(file: File) {
        metadataRetriever.setDataSource(file.path)
    }

    fun setTitle(titleView: TextView) {
        val title = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        titleView.text = title
    }

    fun setCover(coverView: ImageView) {
        val bytes = metadataRetriever.embeddedPicture
        val drawable = if (bytes != null) {
            RoundedBitmapDrawableFactory.create(Resources.getSystem(), ByteArrayInputStream(bytes))
        } else {
            val bitmap = ContextCompat.getDrawable(coverView.context, R.mipmap.ic_launcher)!!.toBitmap()
            RoundedBitmapDrawableFactory.create(Resources.getSystem(), bitmap)
        }
        drawable.cornerRadius = coverView.context.dip(12).toFloat()
        coverView.setImageDrawable(drawable)
    }

    fun setAuthor(textView: TextView) {
        val artist = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        textView.text = artist
    }
}