package com.example.tamz2mar0906withoutcompose

import EventsAdapter
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tamz2mar0906withoutcompose.Http.EventResponseModel
import com.example.tamz2mar0906withoutcompose.Http.GroupResponseObject
import com.example.tamz2mar0906withoutcompose.Http.UserInfoResponseObject
import com.example.tamz2mar0906withoutcompose.Services.EventService
import com.example.tamz2mar0906withoutcompose.Services.GroupService
import com.example.tamz2mar0906withoutcompose.Services.StorageService
import com.example.tamz2mar0906withoutcompose.Services.UserService
import com.example.tamz2mar0906withoutcompose.Utilities.showCustomSnackbar
import java.util.Locale
import kotlin.system.exitProcess

class CalendarActivity : AppCompatActivity() {
    private var currentUser: UserInfoResponseObject? = null
    private var currentGroups: List<GroupResponseObject>? = null
    private var groupEvents: List<EventResponseModel> = emptyList()

    private lateinit var eventsRecyclerView: RecyclerView
    private lateinit var eventsAdapter: EventsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.calendar)

        val login_name = intent.getStringExtra("login_name") ?: exitProcess(-1)

        val profileImageView = findViewById<ImageView>(R.id.profileImageView)
        handleUserInfo(login_name, profileImageView)

        val spinner: Spinner = findViewById(R.id.apiDataSpinner)
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView)

        eventsAdapter = EventsAdapter(this, emptyList()) { event ->
            showEventDetails(event)
        }
        eventsRecyclerView.layoutManager = LinearLayoutManager(this)
        eventsRecyclerView.adapter = eventsAdapter


        getGroups(login_name, spinner)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedGroup = currentGroups?.getOrNull(position)
                selectedGroup?.id?.let { getEventsForGroup(it) }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabAddEvent).setOnClickListener {
            val selectedPosition = spinner.selectedItemPosition
            val selectedGroup = currentGroups?.getOrNull(selectedPosition)

            val intent = Intent(this, OptionsActivity::class.java)
            intent.putExtra("current_group_id", selectedGroup?.id?.toString())
            startActivity(intent)
        }
    }

    private fun getGroups(loginName: String, spinner: Spinner) {
        GroupService.getGroups(this, findViewById(android.R.id.content), loginName) { groups ->
            currentGroups = groups ?: emptyList()
            StorageService.saveCurrentGroups(this, currentGroups)
            val groupNames = currentGroups?.map { it.groupName } ?: emptyList()
            updateSpinner(spinner, groupNames)

            val selectedGroup = currentGroups?.firstOrNull()
            selectedGroup?.id?.let { getEventsForGroup(it) }
        }
    }

    private fun getEventsForGroup(groupId: Int) {
        EventService.getEventsByGroup(this, findViewById(android.R.id.content), groupId) { events ->
            groupEvents = events

            val sortedEvents = events.sortedWith(compareByDescending<EventResponseModel> { event ->
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(event.eventDate)?.time
            })

            eventsAdapter.updateEvents(sortedEvents)
        }
    }


    private fun showEventDetails(event: EventResponseModel) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(event.eventName)
            .setMessage("Datum: ${event.eventDate}\nMísto: ${event.location}\nPopis: ${event.description}")
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    private fun handleUserInfo(username: String, profileImageView: ImageView) {
        UserService.getUser(this, findViewById(android.R.id.content), username) { user ->
            if (user != null) {
                currentUser = user
                StorageService.saveCurrentUser(this, user)
                user.photo?.let {
                    setProfileImage(it, profileImageView)
                }
            }
        }
    }

    private fun updateSpinner(spinner: Spinner, groupNames: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, groupNames)
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
                getString(R.string.load_photo_error)
            )
        }
    }
}
