package com.example.tamz2mar0906withoutcompose

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.tamz2mar0906withoutcompose.Http.GroupResponseObject
import com.example.tamz2mar0906withoutcompose.Http.UserInfoResponseObject
import com.example.tamz2mar0906withoutcompose.Services.GroupService
import com.example.tamz2mar0906withoutcompose.Services.StorageService
import com.example.tamz2mar0906withoutcompose.Utilities.showCustomSnackbar

class OptionsActivity : AppCompatActivity() {

    private var currentUser: UserInfoResponseObject? = null
    private var currentGroups: List<GroupResponseObject>? = null

    private lateinit var createEventButton: Button
    private lateinit var createGroupButton: Button
    private lateinit var addUserToGroupButton: Button
    private lateinit var removeUserFromGroupButton: Button
    private lateinit var groupNameEditText: EditText
    private lateinit var confirmCreateGroupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.options)

        createEventButton = findViewById(R.id.btnCreateEvent)
        createGroupButton = findViewById(R.id.btnCreateGroup)
        addUserToGroupButton = findViewById(R.id.btnAddUserToGroup)
        removeUserFromGroupButton = findViewById(R.id.btnRemoveUserFromGroup)
        groupNameEditText = findViewById(R.id.groupNameEditText)
        confirmCreateGroupButton = findViewById(R.id.btnConfirmCreateGroup)

        currentUser = StorageService.loadCurrentUser(this)
        currentGroups = StorageService.loadCurrentGroups(this)

        makeGoneAllTemporary()

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
        }

        removeUserFromGroupButton.setOnClickListener {
        }
    }

    private fun makeGoneAllTemporary(){
        groupNameEditText.visibility = View.GONE
        confirmCreateGroupButton.visibility = View.GONE
    }

    private fun createGroup(groupName: String, loginName: String) {
        GroupService.createGroup(this, findViewById(android.R.id.content), groupName, loginName) { success ->
            if (success) {
                showCustomSnackbar(findViewById(android.R.id.content), this, getString(R.string.group_created_successfully, groupName))
                val intent = Intent(this, CalendarActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                showCustomSnackbar(findViewById(android.R.id.content), this, getString(R.string.error_creating_group, groupName))
            }
        }
    }
}