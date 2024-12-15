package com.example.tamz2mar0906withoutcompose

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.tamz2mar0906withoutcompose.Http.GroupResponseObject
import com.example.tamz2mar0906withoutcompose.Http.UserInfoResponseObject
import com.example.tamz2mar0906withoutcompose.Services.GroupService
import com.example.tamz2mar0906withoutcompose.Services.StorageService
import com.example.tamz2mar0906withoutcompose.Services.UserService
import com.example.tamz2mar0906withoutcompose.Utilities.showCustomSnackbar
import com.google.gson.Gson
import kotlin.system.exitProcess

class CalendarActivity : AppCompatActivity() {
    private var currentUser: UserInfoResponseObject? = null
    private var currentGroups: List<GroupResponseObject>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.calendar)
        val login_name = intent.getStringExtra("login_name") ?: exitProcess(-1)

        val profileImageView = findViewById<ImageView>(R.id.profileImageView)
        handleUserInfo(login_name,profileImageView)

        val spinner: Spinner = findViewById(R.id.apiDataSpinner)

        getGroups(login_name)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabAddEvent).setOnClickListener {
            val intent = Intent(this, OptionsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getGroups(loginName: String) {
        GroupService.getGroups(this, findViewById(android.R.id.content), loginName) { groups ->
            currentGroups = groups ?: emptyList()
            StorageService.saveCurrentGroups(this,currentGroups)
            val groupNames = groups.map { it.groupName }
            updateSpinner(groupNames)
            currentGroups = StorageService.loadCurrentGroups(this)
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
}