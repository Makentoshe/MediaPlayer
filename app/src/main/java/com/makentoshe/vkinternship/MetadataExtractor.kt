package com.makentoshe.vkinternship

import android.content.res.Resources
import android.media.MediaMetadataRetriever
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
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
        val bytes = metadataRetriever.embeddedPicture ?: return
        val bitmap = RoundedBitmapDrawableFactory.create(
            Resources.getSystem(),
            ByteArrayInputStream(bytes)
        )
        bitmap.cornerRadius = coverView.context.dip(6).toFloat()
        coverView.setImageBitmap(bitmap.bitmap)
    }
}