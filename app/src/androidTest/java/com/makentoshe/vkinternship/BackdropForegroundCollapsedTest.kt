package com.makentoshe.vkinternship

import android.content.Intent
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
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

class BackdropForegroundCollapsedTest {

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

        onView(withId(R.id.activity_main_background_button)).perform(click()).noActivity()
        onView(withId(R.id.directory_select_pathview)).perform(typeText("testdirectory/")).noActivity()
        onView(withId(R.id.dialog_button)).perform(click()).noActivity()
        //pause player
        onView(
            allOf(
                withId(R.id.exo_pause), isDescendantOfA(withId(R.id.activity_main_foreground_show))
            )
        ).perform(click())
        //collapse foreground
        onView(withId(R.id.activity_main_foreground_show_dropdown)).perform(click())
    }

    @Test
    fun shouldContainNextButton() {
        onView(withId(R.id.activity_main_foreground_hide_next)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldDrawNextButton() {
        onView(withId(R.id.activity_main_foreground_hide_next)).check{ view, _ ->
            val src = (view as ImageView).drawable.toBitmap()
            val expectedSrc = ContextCompat.getDrawable(rule.activity, R.drawable.ic_skip_next_48)!!.toBitmap()

            assertArrayEquals(expectedSrc.toByteArray(), src.toByteArray())
        }
    }

    @Test
    fun shouldChangeMediaOnNextButtonClick() {
        onView(withId(R.id.activity_main_foreground_hide_next)).perform(click())

        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file2.path)
        //check the title was changed
        val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        onView(withId(R.id.activity_main_foreground_hide_title)).check(matches(withText(title)))
    }

    @Test
    fun shouldContainPausePlayButton() {
        onView(
            allOf(
                withId(R.id.exo_pause), isDescendantOfA(withId(R.id.activity_main_foreground_hide))
            )
        ).check(matches(isEnabled()))

        onView(
            allOf(
                withId(R.id.exo_play), isDescendantOfA(withId(R.id.activity_main_foreground_hide))
            )
        ).check(matches(isEnabled()))
    }

    @Test
    fun shouldDrawPausePlayButton() {
        onView(
            allOf(withId(R.id.exo_pause), isDescendantOfA(withId(R.id.activity_main_foreground_hide)))
        ).check { view, _ ->
            val src = (view as ImageView).drawable.toBitmap()
            val expectedSrc = ContextCompat.getDrawable(rule.activity, R.drawable.ic_pause_48)!!.toBitmap()

            assertArrayEquals(expectedSrc.toByteArray(), src.toByteArray())
        }

        onView(
            allOf(withId(R.id.exo_play), isDescendantOfA(withId(R.id.activity_main_foreground_hide)))
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
            allOf(withId(R.id.exo_play), isDescendantOfA(withId(R.id.activity_main_foreground_hide)))
        ).perform(click())
        assertTrue(PlayerService.mediaPlayer!!.playWhenReady)
        //pause play
        onView(
            allOf(withId(R.id.exo_pause), isDescendantOfA(withId(R.id.activity_main_foreground_hide)))
        ).perform(click())
        assertFalse(PlayerService.mediaPlayer!!.playWhenReady)
    }

    @Test
    fun shouldContainRemainedTime() {
        onView(withId(R.id.activity_main_foreground_hide_time)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldBindRemainedTimeToTimeBar() {
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
        onView(withId(R.id.activity_main_foreground_hide_time)).check { view, _ ->
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
    fun shouldContainTitle() {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file1.path)
        val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        onView(withId(R.id.activity_main_foreground_hide_title)).check(matches(withText(title)))
    }

    @Test
    fun shouldContainAlbumArt() {
        onView(withId(R.id.activity_main_foreground_hide_album_art)).check(matches(isDisplayed()))
    }

    @After
    fun after() {
        activity.stopService(Intent(activity, PlayerService::class.java))
        directory.deleteRecursively()
    }
}