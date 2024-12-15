package com.example.tamz2mar0906withoutcompose

import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.tamz2mar0906withoutcompose.Http.EventResponseModel
import com.example.tamz2mar0906withoutcompose.Http.GroupResponseObject
import com.example.tamz2mar0906withoutcompose.Http.UserInfoResponseObject
import com.example.tamz2mar0906withoutcompose.Services.EventService
import com.example.tamz2mar0906withoutcompose.Services.GroupService
import com.example.tamz2mar0906withoutcompose.Services.StorageService
import com.example.tamz2mar0906withoutcompose.Services.UserService
import com.example.tamz2mar0906withoutcompose.Utilities.showCustomSnackbar
import com.google.gson.Gson
import java.util.Locale
import kotlin.system.exitProcess

class CalendarActivity : AppCompatActivity() {
    private var currentUser: UserInfoResponseObject? = null
    private var currentGroups: List<GroupResponseObject>? = null
    private var groupEvents: List<EventResponseModel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.calendar)
        val login_name = intent.getStringExtra("login_name") ?: exitProcess(-1)

        val profileImageView = findViewById<ImageView>(R.id.profileImageView)
        handleUserInfo(login_name,profileImageView)

        val spinner: Spinner = findViewById(R.id.apiDataSpinner)

        getGroups(login_name, spinner)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabAddEvent).setOnClickListener {
            val selectedPosition = spinner.selectedItemPosition
            val selectedGroup = currentGroups?.getOrNull(selectedPosition)

            val intent = Intent(this, OptionsActivity::class.java)

            if (selectedGroup != null) {
                intent.putExtra("current_group_id", selectedGroup.id.toString())
            } else {
                intent.putExtra("current_group_id", null as Int?)
            }

            startActivity(intent)
        }
    }

    private fun getGroups(loginName: String, spinner: Spinner) {
        GroupService.getGroups(this, findViewById(android.R.id.content), loginName) { groups ->
            currentGroups = groups ?: emptyList()
            StorageService.saveCurrentGroups(this,currentGroups)
            val groupNames = groups.map { it.groupName }
            updateSpinner(groupNames)
            currentGroups = StorageService.loadCurrentGroups(this)

            val selectedPosition = spinner.selectedItemPosition
            val selectedGroup = currentGroups?.getOrNull(selectedPosition)

            if (selectedGroup != null) {
                getEventsForGroup(selectedGroup.id)
            }
        }
    }

    private fun handleUserInfo(username: String, profileImageView: ImageView) {
        UserService.getUser(this, findViewById(android.R.id.content), username) { user ->
            if (user != null) {
                currentUser = user
                StorageService.saveCurrentUser(this,user)
                user.photo?.let {
                    setProfileImage(it, profileImageView)
                }
                currentUser = StorageService.loadCurrentUser(this)
            }
        }
    }

    private fun updateSpinner(groupNames: List<String>) {
        val spinner: Spinner = findViewById(R.id.apiDataSpinner)

        val options = groupNames

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            options
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter
    }

    private fun setProfileImage(base64Photo: String, profileImageView: ImageView) {
        try {
            val decodedBytes = Base64.decode(base64Photo, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            profileImageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            showCustomSnackbar(
                findViewById(android.R.id.content),
                this,
                getString(R.string.load_photo_error))
        }
    }

    private fun getEventsForGroup(groupId: Int) {
        EventService.getEventsByGroup(this, findViewById(android.R.id.content), groupId) { events ->
            groupEvents = events
        }
    }

}