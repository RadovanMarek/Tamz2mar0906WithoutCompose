package com.example.tamz2mar0906withoutcompose

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.tamz2mar0906withoutcompose.Http.ApiClient
import com.example.tamz2mar0906withoutcompose.Http.GeneralResponse
import com.example.tamz2mar0906withoutcompose.Http.LoginRequest
import com.example.tamz2mar0906withoutcompose.Http.RegisterRequest
import com.example.tamz2mar0906withoutcompose.Services.UserService
import com.example.tamz2mar0906withoutcompose.Utilities.showCustomSnackbar
import retrofit2.Call

class RegisterActivity : AppCompatActivity() {
    private var selectedPhoto: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.register)

        val genderSpinner: Spinner = findViewById(R.id.genderSpinner)
        val adapter = ArrayAdapter.createFromResource(this, R.array.gender_options, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner.adapter = adapter


        val singupLink = findViewById<TextView>(R.id.singUpLink)

        singupLink.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        val photoButton = findViewById<TextView>(R.id.photoButton)

        photoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            startActivityForResult(intent, 1001)
        }

        val registerButton = findViewById<Button>(R.id.registerButton)

        registerButton.setOnClickListener{
            val usernameEditText = findViewById<EditText>(R.id.usernameEditText)
            val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
            val phoneNumberEditText = findViewById<EditText>(R.id.phoneNumberEditText)

            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val phoneNumber = phoneNumberEditText.text.toString().trim()
            val gender = genderSpinner.selectedItem.toString()

            if (username.isEmpty() || password.isEmpty() || phoneNumber.isEmpty()) {
                showCustomSnackbar(findViewById(android.R.id.content),this,"Uživatelské jméno nebo heslo nebo telefonní číslo nesmí být prázdné.")
            }
            else if(username.length > 50){
                showCustomSnackbar(findViewById(android.R.id.content),this,"Uživatelské jméno nesmí být větší jak 50 znaků.")
            }
            else if(password.length > 70){
                showCustomSnackbar(findViewById(android.R.id.content), this,"Heslo nesmí být větší jak 70 znaků.")
            }
            else if(phoneNumber.length > 13){
                showCustomSnackbar(findViewById(android.R.id.content), this,"Telefonní číslo nemůže mít více jak 13 znaků")
            } else if(phoneNumber.length < 9){
                showCustomSnackbar(findViewById(android.R.id.content), this,"Telefonní číslo nemůže mít méně jak 9 znaků")
            }else if (!phoneNumber.matches(Regex("^\\+?[0-9]*\$"))) {
                showCustomSnackbar(findViewById(android.R.id.content), this, "Telefonní číslo může obsahovat pouze čísla a znak + na začátku.")
            }
            else {
                handleRegister(username,password, phoneNumber, gender, selectedPhoto)
            }
        }
    }

    private fun handleRegister(username: String, password: String, phoneNumber: String, gender: String, photo: String?) {
        val registerRequest = RegisterRequest(
            loginName = username,
            password = password,
            phoneNumber = phoneNumber,
            gender = gender,
            photo = photo
        )

        UserService.registerUser(this, findViewById(android.R.id.content), registerRequest) { success ->
            if (success) {
                val intent = Intent(this@RegisterActivity, CalendarActivity::class.java)
                intent.putExtra("login_name", username)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001 && resultCode == RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                try {
                    val inputStream = contentResolver.openInputStream(uri)
                    val photoBytes = inputStream?.readBytes()

                    if (photoBytes != null) {
                        selectedPhoto = Base64.encodeToString(photoBytes, Base64.NO_WRAP)
                        showCustomSnackbar(findViewById(android.R.id.content), this, "Fotografie úspěšně načtena.")
                    } else {
                        showCustomSnackbar(findViewById(android.R.id.content), this, "Nepodařilo se načíst fotografii.")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showCustomSnackbar(findViewById(android.R.id.content), this, "Chyba při načítání fotografie: ${e.message}")
                }
            } else {
                showCustomSnackbar(findViewById(android.R.id.content), this, "Nebyla vybrána žádná fotografie.")
            }
        }
    }

}