package com.example.tamz2mar0906withoutcompose

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tamz2mar0906withoutcompose.Http.ApiClient
import com.example.tamz2mar0906withoutcompose.Http.GeneralResponse
import com.example.tamz2mar0906withoutcompose.Http.LoginRequest
import com.example.tamz2mar0906withoutcompose.Services.UserService
import com.example.tamz2mar0906withoutcompose.Utilities.showCustomSnackbar
import retrofit2.Call

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login)

        val usernameEditText = findViewById<EditText>(R.id.usernameEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)

        val registerLink = findViewById<TextView>(R.id.registerLink)

        registerLink.setOnClickListener{
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                showCustomSnackbar(findViewById(android.R.id.content),this,"Uživatelské jméno nebo heslo nesmí být prázdné.")
            }
            else if(username.length > 50){
                showCustomSnackbar(findViewById(android.R.id.content),this,"Uživatelské jméno nesmí být větší jak 50 znaků.")
            }
            else if(password.length > 70){
                showCustomSnackbar(findViewById(android.R.id.content), this,"Heslo nesmí být větší jak 70 znaků.")
            }
            else {
                handleLogin(username, password)
            }
        }
    }

    private fun handleLogin(username: String, password: String) {
        UserService.loginUser(this, findViewById(android.R.id.content), username, password) { success ->
            if (success) {
                val intent = Intent(this@LoginActivity, CalendarActivity::class.java)
                intent.putExtra("login_name", username)
                startActivity(intent)
                finish()
            }
        }
    }
}