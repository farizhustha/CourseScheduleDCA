package com.dicoding.courseschedule.ui.add

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.ui.list.ListViewModelFactory
import com.dicoding.courseschedule.util.TAG_END_TIME
import com.dicoding.courseschedule.util.TAG_START_TIME
import com.dicoding.courseschedule.util.TimePickerFragment
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddCourseActivity : AppCompatActivity(), TimePickerFragment.DialogTimeListener {
    private val viewModel: AddCourseViewModel by viewModels {
        ListViewModelFactory.createFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course)

        setupView()
        setupObserver()
    }

    private fun setupView() {
        findViewById<ImageButton>(R.id.ib_start_time).setOnClickListener {
            showTimePicker(TAG_START_TIME)
        }
        findViewById<ImageButton>(R.id.ib_end_time).setOnClickListener {
            showTimePicker(TAG_END_TIME)
        }
    }

    private fun setupObserver() {
        viewModel.saved.observe(this) { result ->
            result.getContentIfNotHandled()?.let { state ->
                if (state) {
                    Toast.makeText(
                        this,
                        getString(R.string.added_success_message),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.input_empty_message),
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_insert -> {
                val courseName =
                    findViewById<TextInputEditText>(R.id.ed_course_name).text.toString()
                val day = findViewById<Spinner>(R.id.spinner_day).selectedItemPosition
                val startTime = findViewById<TextView>(R.id.tv_start_time).text.toString()
                val endTime = findViewById<TextView>(R.id.tv_end_time).text.toString()
                val lecturer = findViewById<TextInputEditText>(R.id.ed_lecturer).text.toString()
                val note = findViewById<TextInputEditText>(R.id.ed_note).text.toString()

                viewModel.insertCourse(courseName, day, startTime, endTime, lecturer, note)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showTimePicker(tag: String?) {
        val dialogFragment = TimePickerFragment()
        dialogFragment.show(supportFragmentManager, tag)
    }

    override fun onDialogTimeSet(tag: String?, hour: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        when (tag) {
            TAG_START_TIME -> {
                findViewById<TextView>(R.id.tv_start_time).text = timeFormat.format(calendar.time)
            }

            TAG_END_TIME -> {
                findViewById<TextView>(R.id.tv_end_time).text = timeFormat.format(calendar.time)
            }
        }
    }

}