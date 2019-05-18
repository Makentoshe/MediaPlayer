package com.makentoshe.vkinternship

import android.content.Intent
import android.os.Environment
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.makentoshe.vkinternship.player.PlayerService
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File
import kotlin.random.Random

class BackdropBackgroundTest {

    @get:Rule
    val rule = ActivityTestRule<MainActivity>(MainActivity::class.java, false, true)
    @get:Rule
    val readPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    @get:Rule
    val writePermissionRule = GrantPermissionRule.grant(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private var directory: File? = null

    @Test
    fun shouldContainText() {
        onView(withText(R.string.directory_request_question)).check(matches(isDisplayed()))
        onView(withText(R.string.directory_request_question)).check(matches(hasTextColor(android.R.color.black)))
        onView(withText(R.string.directory_request_clarification)).check(matches(isDisplayed()))
    }

    @Test(expected = NoMatchingViewException::class)
    fun shouldContainButton() {
        onView(withId(R.id.activity_main_background_button)).check(matches(withText(R.string.directory_choose)))
        onView(withId(R.id.activity_main_background_button)).check(matches(hasTextColor(android.R.color.white)))
        onView(withId(R.id.activity_main_background_button)).perform(click())
        //should not be found
        onView(withId(R.id.activity_main)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldDisplayMessageWhenDirectoryDoesNotExists() {
        onView(withId(R.id.activity_main_background_button)).perform(click())
        onView(withId(R.id.dialog_edit_text)).perform(typeText("sas"))
        onView(withId(R.id.dialog_button)).perform(click()).noActivity()

        onView(withText(R.string.selected_directory_does_not_exist)).inRoot(withDecorView(not(`is`(rule.activity.window.decorView))))
            .check(matches(isDisplayed()))
    }

    @Test
    fun shouldDisplayMessageWhenSelectedDirectoryIsAFile() {
        val dirPath = Environment.getExternalStorageDirectory().path.plus("/testdirectory")
        directory = File(dirPath)
        directory!!.mkdirs()
        val file = File(directory, "testfile")
        file.createNewFile()

        onView(withId(R.id.activity_main_background_button)).perform(click())
        onView(withId(R.id.dialog_edit_text)).perform(typeText("testdirectory/testfile"))
        onView(withId(R.id.dialog_button)).perform(click()).noActivity()

        onView(withText(R.string.selected_directory_does_not_exist)).inRoot(withDecorView(not(`is`(rule.activity.window.decorView))))
            .check(matches(isDisplayed()))
    }

    @Test
    fun shouldDisplayMessageWhenSelectedDirectoryIsEmpty() {
        val extDir = Environment.getExternalStorageDirectory()
        val dirPath = extDir.path.plus("/testdirectory")
        directory = File(dirPath)
        directory!!.mkdirs()

        onView(withId(R.id.activity_main_background_button)).perform(click())
        onView(withId(R.id.dialog_edit_text)).perform(typeText("testdirectory/"))
        onView(withId(R.id.dialog_button)).perform(click()).noActivity()

        //init ui automator
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val device = UiDevice.getInstance(instrumentation)

        //wait for the toast to appear
        device.wait(Until.hasObject(By.text(context.getString(R.string.selected_directory_is_empty))), 2000)
    }

    @Test
    fun shouldDisplayForegroundIfAtLeastOneFileIsAudio() {
        val extDir = Environment.getExternalStorageDirectory()
        val dirPath = extDir.path.plus("/testdirectory")
        directory = File(dirPath)
        directory!!.mkdirs()
        val ins = rule.activity.resources.openRawResource(
            rule.activity.resources.getIdentifier("test_media_file1", "raw", rule.activity.packageName)
        )

        val file = File(directory, "testMediaFile1.mp3")
        file.createNewFile()
        ins.use { file.writeBytes(ins.readBytes()) }

        onView(withId(R.id.activity_main_background_button)).perform(click())
        onView(withId(R.id.dialog_edit_text)).perform(typeText("testdirectory/"))
        onView(withId(R.id.dialog_button)).perform(click()).noActivity()

        onView(withId(R.id.activity_main_foreground)).check(matches(isDisplayed()))

        //Check service was started
        assertTrue(rule.activity.stopService(Intent(rule.activity, PlayerService::class.java)))
    }

    @After
    fun after() {
        rule.activity.stopService(Intent(rule.activity, PlayerService::class.java))
        directory?.deleteRecursively()
    }
}

