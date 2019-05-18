package com.makentoshe.vkinternship

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import java.io.File

/**
 * Activity for choosing the directory with the music.
 * If request was canceled by back button press the default music directory will be returned.
 */
class DirectoryChooseActivity : AppCompatActivity() {

    private val button by lazy { findViewById<Button>(R.id.dialog_button) }

    private val listview by lazy { findViewById<ListView>(R.id.directory_select_listview) }

    private val editText by lazy { findViewById<EditText>(R.id.directory_select_pathview) }

    private var dir = Environment.getExternalStorageDirectory()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.directory_select_layout)

        if (savedInstanceState != null) {
            dir = savedInstanceState.get(File::class.java.simpleName) as File
        }

        updateList()

        button.setOnClickListener {
            setResult(RESULT_OK, putPathToTheResultIntent(editText.text.toString()))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        button.requestFocus()
    }

    private fun updateList() {
        //get all directories
        val files = dir.listFiles().filter { it.isDirectory || it.extension == "mp3" }
        val list = files.map { it.name }
        listview.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list)
        listview.setOnItemClickListener { _, _, position, _ ->
            val file = files[position]
            if (!file.isDirectory) return@setOnItemClickListener
            stepTo(file)
        }
    }

    private fun stepTo(file: File) {
        dir = file
        updateList()
        editText.setText(file.path)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(File::class.java.simpleName, dir)
    }

    override fun onBackPressed() {
        try {
            //step down
            stepTo(dir.parentFile)
        } catch (e: Exception) {
            //default path is a Music directory
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).toString()
            setResult(Activity.RESULT_CANCELED, putPathToTheResultIntent(path))
            super.onBackPressed()
        }
    }

    private fun putPathToTheResultIntent(path: String): Intent {
        return Intent().putExtra(String::class.java.simpleName, path)
    }
}