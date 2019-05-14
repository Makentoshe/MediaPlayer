package com.makentoshe.vkinternship

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

/**
 * Activity for choosing the directory with the music.
 * If request was canceled by back button press the default music directory will be returned.
 */
class DirectoryChooseActivity : AppCompatActivity() {

    private val button by lazy { findViewById<Button>(R.id.dialog_button) }

    private val editText by lazy { findViewById<EditText>(R.id.dialog_edit_text) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_directory_choose)

        button.setOnClickListener {
            val path = Environment.getExternalStorageDirectory().path.plus("/").plus(editText.text)
            setResult(RESULT_OK, putPathToTheResultIntent(path))
            finish()
        }
    }

    override fun onBackPressed() {
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).toString()
        setResult(Activity.RESULT_CANCELED, putPathToTheResultIntent(path))
        super.onBackPressed()
    }

    private fun putPathToTheResultIntent(path: String): Intent {
        return Intent().putExtra(String::class.java.simpleName, path)
    }
}