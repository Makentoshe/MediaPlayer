package com.makentoshe.vkinternship

import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Environment
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.google.android.exoplayer2.ui.TimeBar
import com.makentoshe.vkinternship.player.PlayerService
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

class BackdropForegroundExpandedTest {

    @get:Rule
    val rule = ActivityTestRule<MainActivity>(MainActivity::class.java, false, false)
    @get:Rule
    val readPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    @get:Rule
    val writePermissionRule = GrantPermissionRule.grant(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private lateinit var activity: MainActivity

    private lateinit var directory: File
    private lateinit var file1: File
    private lateinit var file2: File

    @Before
    fun init() {
        activity = rule.launchActivity(null)
        val extDir = Environment.getExternalStorageDirectory()
        val dirPath = extDir.path.plus("/testdirectory")
        directory = File(dirPath)
        directory.mkdirs()
        //write 1 file
        val ins1 = rule.activity.resources.openRawResource(
            rule.activity.resources.getIdentifier("test_media_file1", "raw", rule.activity.packageName)
        )
        file1 = File(directory, "testMediaFile1.mp3")
        file1.createNewFile()
        ins1.use { file1.writeBytes(ins1.readBytes()) }
        //write 2 file
        val ins2 = rule.activity.resources.openRawResource(
            rule.activity.resources.getIdentifier("test_media_file2", "raw", rule.activity.packageName)
        )
        file2 = File(directory, "testMediaFile2.mp3")
        file2.createNewFile()
        ins2.use { file2.writeBytes(ins2.readBytes()) }

        onView(withId(R.id.activity_main_background_button)).perform(click())
        onView(withId(R.id.dialog_edit_text)).perform(typeText("testdirectory/"))
        onView(withId(R.id.dialog_button)).perform(click()).noActivity()
        //pause player
        onView(
            allOf(
                withId(R.id.exo_pause), isDescendantOfA(withId(R.id.activity_main_foreground_show))
            )
        ).perform(click())
    }

    @Test
    fun shouldContainTitle() {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file1.path)
        val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        onView(withId(R.id.activity_main_foreground_show_title)).check(matches(withText(title)))
    }

    @Test
    fun shouldContainAuthor() {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file1.path)
        val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        onView(withId(R.id.activity_main_foreground_show_artist)).check(matches(withText(artist)))
    }

    @Test
    fun shouldContainAddIcon() {
        onView(withId(R.id.activity_main_foreground_show_add)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldDrawAddIcon() {
        onView(withId(R.id.activity_main_foreground_show_add)).check { view, _ ->
            val src = (view as ImageView).drawable.toBitmap()
            val expectedSrc = ContextCompat.getDrawable(rule.activity, R.drawable.ic_add_outline_24)!!.toBitmap()

            assertArrayEquals(expectedSrc.toByteArray(), src.toByteArray())
        }
    }

    @Test
    fun shouldContainOverflowIcon() {
        onView(withId(R.id.activity_main_foreground_show_more)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldDrawOverflowIcon() {
        onView(withId(R.id.activity_main_foreground_show_more)).check { view, _ ->
            val src = (view as ImageView).drawable.toBitmap()
            val expectedSrc = ContextCompat.getDrawable(rule.activity, R.drawable.ic_ic_more_24dp)!!.toBitmap()

            assertArrayEquals(expectedSrc.toByteArray(), src.toByteArray())
        }
    }

    @Test
    fun shouldContainTimeBar() {
        onView(withId(R.id.exo_progress)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldContainPassedTime() {
        onView(withId(R.id.activity_main_foreground_show_controller_passedtime)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldColorPassedTime() {
        onView(withId(R.id.activity_main_foreground_show_controller_passedtime)).check(matches(hasTextColor(R.color.MaterialLightBlue700)))
    }

    @Test
    fun shouldBindPassedTimeToTimeBar1() {
        //set progress to 0ms
        onView(withId(R.id.exo_progress)).check { view, _ ->
            view as TimeBar
            view.setPosition(0L)
        }

        onView(withId(R.id.activity_main_foreground_show_controller_passedtime)).check(matches(withText("00:00")))
    }

    @Test
    fun shouldBindPassedTimeToTimeBar2() {
        //set progress to 0ms
        onView(withId(R.id.exo_progress)).check { view, _ ->
            view as TimeBar
            view.setPosition(5123)
        }

        onView(withId(R.id.activity_main_foreground_show_controller_passedtime)).check(matches(withText("00:05")))
    }

    @Test
    fun shouldContainRemainedTime() {
        onView(withId(R.id.activity_main_foreground_show_controller_remainedtime)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldBindRemainedTimeToTimeBar1() {
        //get media duration
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file1.path)
        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLong()
        //set position to start
        onView(withId(R.id.exo_progress)).check { view, _ ->
            (view as TimeBar).setPosition(0)
        }
        //parse time from string to minutes and seconds
        onView(withId(R.id.activity_main_foreground_show_controller_remainedtime)).check { view, _ ->
            val text = (view as TextView).text.toString()
            val split = text.split(":")

            val minutes = split[0].toLong().absoluteValue
            val expectedMinutes = TimeUnit.MILLISECONDS.toMinutes(duration)
            assertEquals(expectedMinutes, minutes)

            val seconds = split[1].toLong().absoluteValue
            val expectedSeconds = (TimeUnit.MILLISECONDS.toSeconds(duration) % 60)
            assertEquals(expectedSeconds, seconds)
        }
    }

    @Test
    fun shouldBindRemainedTimeToTimeBar2() {
        val passedDuration = 47932L
        //get media duration
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file1.path)
        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLong()
        //set position to start
        onView(withId(R.id.exo_progress)).check { view, _ ->
            (view as TimeBar).setPosition(passedDuration)
        }
        //parse time from string to minutes and seconds
        onView(withId(R.id.activity_main_foreground_show_controller_remainedtime)).check { view, _ ->
            val text = (view as TextView).text.toString()
            val split = text.split(":")

            val minutes = split[0].toLong().absoluteValue
            val expectedMinutes = TimeUnit.MILLISECONDS.toMinutes(duration - passedDuration)
            assertEquals(expectedMinutes, minutes)

            val seconds = split[1].toLong().absoluteValue
            val expectedSeconds = (TimeUnit.MILLISECONDS.toSeconds(duration - passedDuration) % 60)
            assertEquals(expectedSeconds, seconds)
        }
    }

    @Test
    fun shouldContainPrevButton() {
        onView(withId(R.id.activity_main_foreground_show_prev_icon)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldDrawPrevButton() {
        onView(withId(R.id.activity_main_foreground_show_prev_icon)).check { view, _ ->
            val src = (view as ImageView).drawable.toBitmap()
            val expectedSrc = ContextCompat.getDrawable(rule.activity, R.drawable.ic_skip_previous_48)!!.toBitmap()

            assertArrayEquals(expectedSrc.toByteArray(), src.toByteArray())
        }
    }

    @Test
    fun shouldChangeMediaToPreviousOnPrevButtonClick() {
        onView(withId(R.id.activity_main_foreground_show_prev_icon)).perform(click())

        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file2.path)
        //check the title was changed
        val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        onView(withId(R.id.activity_main_foreground_show_title)).check(matches(withText(title)))
        //check the artist was changed
        val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        onView(withId(R.id.activity_main_foreground_show_artist)).check(matches(withText(artist)))
    }

    @Test
    fun shouldContainNextButton() {
        onView(withId(R.id.activity_main_foreground_show_next_icon)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldDrawNextButton() {
        onView(withId(R.id.activity_main_foreground_show_next_icon)).check { view, _ ->
            val src = (view as ImageView).drawable.toBitmap()
            val expectedSrc = ContextCompat.getDrawable(rule.activity, R.drawable.ic_skip_next_48)!!.toBitmap()

            assertArrayEquals(expectedSrc.toByteArray(), src.toByteArray())
        }
    }

    @Test
    fun shouldChangeMediaToNextOnNextButtonClick() {
        onView(withId(R.id.activity_main_foreground_show_next)).perform(click())

        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file2.path)
        //check the title was changed
        val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        onView(withId(R.id.activity_main_foreground_show_title)).check(matches(withText(title)))
        //check the artist was changed
        val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        onView(withId(R.id.activity_main_foreground_show_artist)).check(matches(withText(artist)))
    }

    @Test
    fun shouldContainPausePlayButton() {
        onView(
            allOf(
                withId(R.id.exo_pause), isDescendantOfA(withId(R.id.activity_main_foreground_show))
            )
        ).check(matches(isEnabled()))

        onView(
            allOf(
                withId(R.id.exo_play), isDescendantOfA(withId(R.id.activity_main_foreground_show))
            )
        ).check(matches(isEnabled()))
    }

    @Test
    fun shouldDrawPausePlayButton() {
        onView(
            allOf(withId(R.id.exo_pause), isDescendantOfA(withId(R.id.activity_main_foreground_show)))
        ).check { view, _ ->
            val src = (view as ImageView).drawable.toBitmap()
            val expectedSrc = ContextCompat.getDrawable(rule.activity, R.drawable.ic_pause_48)!!.toBitmap()

            assertArrayEquals(expectedSrc.toByteArray(), src.toByteArray())
        }

        onView(
            allOf(withId(R.id.exo_play), isDescendantOfA(withId(R.id.activity_main_foreground_show)))
        ).check { view, _ ->
            val src = (view as ImageView).drawable.toBitmap()
            val expectedSrc = ContextCompat.getDrawable(rule.activity, R.drawable.ic_play_48)!!.toBitmap()

            assertArrayEquals(expectedSrc.toByteArray(), src.toByteArray())
        }
    }

    @Test
    fun shouldPlayAndPausePlayerOnButtonClick() {
        //resume play
        onView(
            allOf(withId(R.id.exo_play), isDescendantOfA(withId(R.id.activity_main_foreground_show)))
        ).perform(click())
        assertTrue(PlayerService.mediaPlayer!!.playWhenReady)
        //pause play
        onView(
            allOf(withId(R.id.exo_pause), isDescendantOfA(withId(R.id.activity_main_foreground_show)))
        ).perform(click())
        assertFalse(PlayerService.mediaPlayer!!.playWhenReady)
    }

    @Test
    fun shouldContainRepeatButton() {
        onView(withId(R.id.activity_main_foreground_show_repeat)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldDrawRepeatButton() {
        onView(withId(R.id.activity_main_foreground_show_repeat_icon)).check { view, _ ->
            val src = (view as ImageView).drawable.toBitmap()
            val expectedSrc = ContextCompat.getDrawable(rule.activity, R.drawable.ic_repeat_24)!!.toBitmap()

            assertArrayEquals(expectedSrc.toByteArray(), src.toByteArray())
        }
    }

    @Test
    fun shouldContainShuffleButton() {
        onView(withId(R.id.activity_main_foreground_show_shuffle)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldDrawShuffleButton() {
        onView(withId(R.id.activity_main_foreground_show_shuffle_icon)).check { view, _ ->
            val src = (view as ImageView).drawable.toBitmap()
            val expectedSrc = ContextCompat.getDrawable(rule.activity, R.drawable.ic_shuffle_24)!!.toBitmap()

            assertArrayEquals(expectedSrc.toByteArray(), src.toByteArray())
        }
    }

    @Test
    fun shouldContainDropDownButton() {
        onView(withId(R.id.activity_main_foreground_show_dropdown)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldDrawDropDownButton() {
        onView(withId(R.id.activity_main_foreground_show_dropdown)).check { view, _ ->
            val src = (view as ImageView).drawable.toBitmap()
            val expectedSrc = ContextCompat.getDrawable(rule.activity, R.drawable.ic_dropdown_24)!!.toBitmap()

            assertArrayEquals(expectedSrc.toByteArray(), src.toByteArray())
        }
    }

    @Test
    fun shouldCollapseBackdropForeground() {
        onView(withId(R.id.activity_main_foreground_hide)).check(matches(not(isDisplayed())))
        onView(allOf(withId(R.id.activity_main_foreground_show))).check(matches(isDisplayed()))
        onView(withId(R.id.activity_main_foreground_show_dropdown)).perform(click())
        onView(withId(R.id.activity_main_foreground_hide)).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.activity_main_foreground_show))).check(matches(not(isDisplayed())))
    }

    @After
    fun after() {
        activity.stopService(Intent(activity, PlayerService::class.java))
        directory.deleteRecursively()
    }
}

fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, 100, stream)
    return stream.toByteArray()
}
