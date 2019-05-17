package com.makentoshe.vkinternship

import android.content.Context
import android.content.res.Configuration
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.card.MaterialCardView
import com.makentoshe.vkinternship.layout.CustomTimeBar
import com.makentoshe.vkinternship.layout.backdrop.BackdropBehavior
import com.makentoshe.vkinternship.player.PlayerServiceController
import com.makentoshe.vkinternship.player.PlayerServiceListenerController
import com.makentoshe.vkinternship.player.SimplePlayerServiceListener
import java.io.File


/**
 * Controller for the foreground layout while it is expanded
 */
class BackdropExpandedForegroundController(
    private val behavior: BackdropBehavior,
    private val foreground: View,
    private val controller: PlayerServiceListenerController
) {

    private val context = foreground.context

    private val serviceController = PlayerServiceController(context)

    /**
     * Main layout displays when the foreground layout is in expanded state.
     */
    private val primaryLayout by lazy {
        foreground.findViewById<View>(R.id.activity_main_foreground_show)
    }

    /**
     * Secondary layout should not displays when the foreground layout is in expanded state.
     */
    private val secondaryLayout by lazy {
        foreground.findViewById<View>(R.id.activity_main_foreground_hide)
    }

    init {
        val fragmentManager = (context as AppCompatActivity).supportFragmentManager
        val viewpager = primaryLayout.findViewById<ViewPager>(R.id.activity_main_foreground_show_viewpager)
        val viewPagerController = AlbumArtViewPagerController(viewpager, fragmentManager, serviceController)

        //set on click listener for dropdown arrow
        foreground.findViewById<View>(R.id.activity_main_foreground_show_dropdown).setOnClickListener {
            behavior.open(true)
        }

        val extractor = MetadataExtractor(MediaMetadataRetriever())
        controller.addListener(ExpandedPlayerServiceListener(primaryLayout, extractor, viewPagerController))

        val nextButton = primaryLayout.findViewById<View>(R.id.activity_main_foreground_show_next)
        nextButton.setOnClickListener { serviceController.selectNextFile() }

        val prevButton = primaryLayout.findViewById<View>(R.id.activity_main_foreground_show_prev_icon)
        prevButton.setOnClickListener { serviceController.selectPrevFile() }


        val timeBar = foreground.findViewById<CustomTimeBar>(R.id.exo_progress)

        val remainedTimeView =
            primaryLayout.findViewById<TextView>(R.id.activity_main_foreground_show_controller_remainedtime)
        RemainedTimeViewController(remainedTimeView).bindToTimeBar(timeBar)

        val passedTimeView =
            primaryLayout.findViewById<TextView>(R.id.activity_main_foreground_show_controller_passedtime)
        PassedTimeViewController(passedTimeView).bindToTimeBar(timeBar)

    }

    /**
     * Displays main layout and hides secondary. Also updates listeners.
     */
    fun display() {
        foreground.setOnClickListener(null)
        primaryLayout.setOnTouchListener(OnSwipeTouchListenerBackdrop(context, behavior))
        primaryLayout.visibility = View.VISIBLE
        secondaryLayout.visibility = View.GONE
        //set corners
        (foreground as MaterialCardView).radius = context.dip(16).toFloat()
    }

    private class OnSwipeTouchListenerBackdrop(
        context: Context, private val behavior: BackdropBehavior
    ) : OnSwipeTouchListener(context) {
        override fun onSwipeBottom() = behavior.open(true)
    }

}

class ExpandedPlayerServiceListener(
    private val primaryLayout: View,
    private val extractor: MetadataExtractor,
    private val controller: AlbumArtViewPagerController
) : SimplePlayerServiceListener() {

    override fun onNextMedia(prev: File, curr: File, next: File) {
        extractor.extract(curr)

        val authorView = primaryLayout.findViewById<TextView>(R.id.activity_main_foreground_show_author)
        extractor.setAuthor(authorView)

        val titleView = primaryLayout.findViewById<TextView>(R.id.activity_main_foreground_show_title)
        extractor.setTitle(titleView)

        controller.updateAdapter(prev, curr, next)
    }
}

class AlbumArtViewPagerController(
    private val viewPager: ViewPager,
    private val fragmentManager: FragmentManager,
    private val controller: PlayerServiceController
) {

    private val context = viewPager.context
    private val centerPageIndex = 2

    init {
        if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            viewPager.visibility = View.GONE
        }

        viewPager.addOnPageChangeListener(object : SimpleOnPageChangeListener() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (positionOffset != 0f) return

                when {
                    position < centerPageIndex -> {
                        if (viewPager.currentItem == centerPageIndex) return
                        controller.selectPrevFile()
                    }
                    position > centerPageIndex -> {
                        if (viewPager.currentItem == centerPageIndex) return
                        controller.selectNextFile()
                    }
                }
            }
        })
    }

    fun updateAdapter(prev: File, curr: File, next: File) {
        val array = arrayOf(prev, prev, curr, next, curr)
        viewPager.isFocusable = false
        viewPager.isClickable = false
        viewPager.adapter = AlbumArtViewPagerAdapter(fragmentManager, array)
        viewPager.setCurrentItem(centerPageIndex, false)
        viewPager.isFocusable = true
        viewPager.isClickable = true
    }

}

private class AlbumArtViewPagerAdapter(
    fragmentManager: FragmentManager, private val array: Array<File>
) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int) = AlbumArtFragment.create(array[position], position)

    override fun getCount() = array.size
}

class AlbumArtFragment : Fragment() {

    private var file: File
        get() = arguments!!.get(FILE) as File
        set(value) = (if (arguments == null) Bundle().also { arguments = it } else arguments).let {
            it!!.putSerializable(FILE, value)
        }

    private var position: Int
        get() = arguments!!.get(POSITION) as Int
        set(value) = (if (arguments == null) Bundle().also { arguments = it } else arguments).let {
            it!!.putSerializable(POSITION, value)
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_main_foreground_show_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val imageview = view.findViewById<ImageView>(R.id.activity_main_foreground_show_viewpager_page_art)
        val extractor = MetadataExtractor(MediaMetadataRetriever())
        extractor.extract(file)
        extractor.setCover(imageview)
    }

    companion object {
        private const val FILE = "File"
        private const val POSITION = "Position"
        fun create(file: File, position: Int) = AlbumArtFragment().apply {
            this.file = file
            this.position = position
        }
    }

}

private open class SimpleOnPageChangeListener : ViewPager.OnPageChangeListener {
    override fun onPageScrollStateChanged(state: Int) = Unit
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit
    override fun onPageSelected(position: Int) = Unit
}
