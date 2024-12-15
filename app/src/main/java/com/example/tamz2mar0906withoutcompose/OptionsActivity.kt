package com.example.tamz2mar0906withoutcompose

import android.content.Intent
import android.os.Bundle
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tamz2mar0906withoutcompose.Adapters.UserAddAdapter
import com.example.tamz2mar0906withoutcompose.Http.EventRequest
import com.example.tamz2mar0906withoutcompose.Http.GroupResponseObject
import com.example.tamz2mar0906withoutcompose.Http.UserInfoResponseObject
import com.example.tamz2mar0906withoutcompose.Services.EventService
import com.example.tamz2mar0906withoutcompose.Services.GroupService
import com.example.tamz2mar0906withoutcompose.Services.StorageService
import com.example.tamz2mar0906withoutcompose.Services.UserService
import com.example.tamz2mar0906withoutcompose.Utilities.showCustomSnackbar
import java.text.SimpleDateFormat
import java.util.*

class OptionsActivity : AppCompatActivity() {

    private var currentUser: UserInfoResponseObject? = null
    private var currentGroups: MutableList<GroupResponseObject>? = null
    private var groupId: Int? = null

    private lateinit var createEventButton: Button
    private lateinit var createGroupButton: Button
    private lateinit var addUserToGroupButton: Button
    private lateinit var groupNameEditText: EditText
    private lateinit var confirmCreateGroupButton: Button
    private lateinit var searchUserEditText: EditText
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var eventNameEditText: EditText
    private lateinit var eventDateButton: Button
    private lateinit var eventLocationEditText: EditText
    private lateinit var eventDescriptionEditText: EditText
    private lateinit var eventAccommodationCheckBox: CheckBox
    private lateinit var confirmCreateEventButton: Button

    private var selectedDateTime: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.options)

        createEventButton = findViewById(R.id.btnCreateEvent)
        createGroupButton = findViewById(R.id.btnCreateGroup)
        addUserToGroupButton = findViewById(R.id.btnAddUserToGroup)
        groupNameEditText = findViewById(R.id.groupNameEditText)
        confirmCreateGroupButton = findViewById(R.id.btnConfirmCreateGroup)
        searchUserEditText = findViewById(R.id.searchUserEditText)
        userRecyclerView = findViewById(R.id.userRecyclerView)

        eventNameEditText = findViewById(R.id.eventNameEditText)
        eventDateButton = findViewById(R.id.eventDateButton)
        eventLocationEditText = findViewById(R.id.eventLocationEditText)
        eventDescriptionEditText = findViewById(R.id.eventDescriptionEditText)
        eventAccommodationCheckBox = findViewById(R.id.eventAccommodationCheckBox)
        confirmCreateEventButton = findViewById(R.id.confirmCreateEventButton)

        currentUser = StorageService.loadCurrentUser(this)
        if (currentUser == null) {
            finish()
            return
        }

        currentGroups = StorageService.loadCurrentGroups(this)?.toMutableList()

        groupId = intent.getStringExtra("current_group_id")?.toIntOrNull()

        makeGoneAllTemporary()

        userRecyclerView.layoutManager = LinearLayoutManager(this)

        createGroupButton.setOnClickListener {
            makeGoneAllTemporary()
            groupNameEditText.visibility = View.VISIBLE
            confirmCreateGroupButton.visibility = View.VISIBLE
            groupNameEditText.hint = getString(R.string.create_group_hint)
        }

        confirmCreateGroupButton.setOnClickListener {
            val groupName = groupNameEditText.text.toString().trim()
            if (groupName.isEmpty()) {
                showCustomSnackbar(findViewById(android.R.id.content), this, getString(R.string.create_group_error_empty_name))
            } else if (groupName.length > 50) {
                showCustomSnackbar(findViewById(android.R.id.content), this, getString(R.string.group_length_error))
            } else {
                currentUser?.loginName?.let { loginName ->
                    createGroup(groupName, loginName)
                } ?: showCustomSnackbar(findViewById(android.R.id.content), this, getString(R.string.error_user_not_found))
            }
        }

        addUserToGroupButton.setOnClickListener {
            makeGoneAllTemporary()
            searchUserEditText.visibility = View.VISIBLE
            userRecyclerView.visibility = View.VISIBLE

            searchUserEditText.setOnEditorActionListener { _, _, _ ->
                val query = searchUserEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    searchUsers(query)
                }
                false
            }
        }

        createEventButton.setOnClickListener {
            makeGoneAllTemporary()

            eventNameEditText.visibility = View.VISIBLE
            eventDateButton.visibility = View.VISIBLE
            eventLocationEditText.visibility = View.VISIBLE
            eventDescriptionEditText.visibility = View.VISIBLE
            eventAccommodationCheckBox.visibility = View.VISIBLE
            confirmCreateEventButton.visibility = View.VISIBLE
        }

        eventDateButton.setOnClickListener {
            showDateTimePicker()
        }

        confirmCreateEventButton.setOnClickListener {
            val eventName = eventNameEditText.text.toString().trim()
            val eventDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(selectedDateTime.time)
            val eventLocation = eventLocationEditText.text.toString().trim()
            val eventDescription = eventDescriptionEditText.text.toString().trim()
            val eventAccommodation = if (eventAccommodationCheckBox.isChecked) 1 else 0

            if (eventName.isEmpty() || eventDate.isEmpty() || eventLocation.isEmpty()) {
                showCustomSnackbar(findViewById(android.R.id.content), this, getString(R.string.error_creating_event))
                return@setOnClickListener
            }

            val groupId = groupId ?: return@setOnClickListener
            val creatorId = currentUser?.id ?: return@setOnClickListener

            val eventRequest = EventRequest(
                groupId = groupId,
                creatorId = creatorId,
                eventName = eventName,
                eventDate = eventDate,
                location = eventLocation,
                accommodationProvided = eventAccommodation,
                description = eventDescription
            )

            EventService.insertEvent(this, findViewById(android.R.id.content), eventRequest) { success ->
                if (success) {
                    showCustomSnackbar(findViewById(android.R.id.content), this, getString(R.string.event_created_successfully, eventName))
                    finish()
                } else {
                    showCustomSnackbar(findViewById(android.R.id.content), this, getString(R.string.error_creating_event))
                }
            }
        }
    }

    private fun showDateTimePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedDateTime.set(Calendar.YEAR, year)
                selectedDateTime.set(Calendar.MONTH, month)
                selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                TimePickerDialog(
                    this,
                    { _, hourOfDay, minute ->
                        selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        selectedDateTime.set(Calendar.MINUTE, minute)

                        val formattedDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                            .format(selectedDateTime.time)
                        eventDateButton.text = getString(R.string.event_date_selected, formattedDateTime)
                    },
                    selectedDateTime.get(Calendar.HOUR_OF_DAY),
                    selectedDateTime.get(Calendar.MINUTE),
                    true
                ).show()
            },
            selectedDateTime.get(Calendar.YEAR),
            selectedDateTime.get(Calendar.MONTH),
            selectedDateTime.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun makeGoneAllTemporary() {
        groupNameEditText.visibility = View.GONE
        confirmCreateGroupButton.visibility = View.GONE
        searchUserEditText.visibility = View.GONE
        userRecyclerView.visibility = View.GONE
        eventNameEditText.visibility = View.GONE
        eventDateButton.visibility = View.GONE
        eventLocationEditText.visibility = View.GONE
        eventDescriptionEditText.visibility = View.GONE
        eventAccommodationCheckBox.visibility = View.GONE
        confirmCreateEventButton.visibility = View.GONE
    }

    private fun createGroup(groupName: String, loginName: String) {
        GroupService.createGroup(this, findViewById(android.R.id.content), groupName, loginName) { success ->
            if (success) {
                showCustomSnackbar(findViewById(android.R.id.content), this, getString(R.string.group_created_successfully, groupName))
                val intent = Intent(this, CalendarActivity::class.java)
                intent.putExtra("login_name", currentUser?.loginName)
                startActivity(intent)
                finish()
            } else {
                showCustomSnackbar(findViewById(android.R.id.content), this, getString(R.string.error_creating_group, groupName))
            }
        }
    }

    private fun searchUsers(query: String) {
        UserService.searchUsers(this, findViewById(android.R.id.content), query) { users ->
            if (users != null) {
                val group = currentGroups?.find { it.id == groupId }
                if (group != null) {
                    val filteredUsers = users.filterNot { user ->
                        group.users?.any { groupUser -> groupUser.id == user.id } == true
                    }.toMutableList()
                    userRecyclerView.adapter = UserAddAdapter(this,group, filteredUsers) { user ->
                        currentGroups?.find { it.id == groupId }?.users?.add(user)
                        StorageService.saveCurrentGroups(this, currentGroups)
                    }
                } else {
                    showCustomSnackbar(
                        findViewById(android.R.id.content),
                        this,
                        getString(R.string.error_user_not_found)
                    )
                }
            }
        }
    }
}
