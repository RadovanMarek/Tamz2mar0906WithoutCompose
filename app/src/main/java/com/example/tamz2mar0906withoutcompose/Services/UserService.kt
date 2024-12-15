package com.example.tamz2mar0906withoutcompose.Services

import android.content.Context
import android.view.View
import com.example.tamz2mar0906withoutcompose.Http.ApiClient
import com.example.tamz2mar0906withoutcompose.Http.GeneralResponse
import com.example.tamz2mar0906withoutcompose.Http.LoginRequest
import com.example.tamz2mar0906withoutcompose.Http.RegisterRequest
import com.example.tamz2mar0906withoutcompose.Http.UserInfoResponse
import com.example.tamz2mar0906withoutcompose.Http.UserInfoResponseObject
import com.example.tamz2mar0906withoutcompose.R
import com.example.tamz2mar0906withoutcompose.Utilities.showCustomSnackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object UserService {
    fun getUser(
        context: Context,
        rootView: View,
        username: String,
        callback: (UserInfoResponseObject?) -> Unit
    ) {
        ApiClient.apiService.getUserDatas(listOf(username)).enqueue(object : Callback<UserInfoResponse> {
            override fun onResponse(call: Call<UserInfoResponse>, response: Response<UserInfoResponse>) {
                if (response.isSuccessful) {
                    val userInfoResponse = response.body()
                    if (userInfoResponse != null && userInfoResponse.success && !userInfoResponse.users.isNullOrEmpty()) {
                        val user = userInfoResponse.users.find { it.loginName == username }
                        callback(user)
                    } else {
                        showCustomSnackbar(
                            rootView,
                            context,
                            userInfoResponse?.message ?: context.getString(R.string.error_user_not_found)
                        )
                        callback(null)
                    }
                } else {
                    showCustomSnackbar(
                        rootView,
                        context,
                        context.getString(R.string.http_error, response.code(), response.message())
                    )
                    callback(null)
                }
            }

            override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                showCustomSnackbar(
                    rootView,
                    context,
                    context.getString(R.string.unexpected_error, t.message)
                )
                callback(null)
            }
        })
    }

    fun loginUser(
        context: Context,
        rootView: View,
        username: String,
        password: String,
        callback: (Boolean) -> Unit
    ) {
        val loginRequest = LoginRequest(loginName = username, password = password)

        ApiClient.apiService.loginUser(loginRequest).enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                if (response.isSuccessful) {
                    val generalResponse = response.body()
                    if (generalResponse != null && generalResponse.success) {
                        callback(true)
                    } else {
                        showCustomSnackbar(
                            rootView,
                            context,
                            context.getString(R.string.unexpected_error, "Neplatné jméno nebo heslo")
                        )
                        callback(false)
                    }
                } else {
                    showCustomSnackbar(
                        rootView,
                        context,
                        context.getString(
                            R.string.http_error,
                            response.code(),
                            response.message()
                        )
                    )
                    callback(false)
                }
            }

            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                showCustomSnackbar(
                    rootView,
                    context,
                    context.getString(R.string.unexpected_error, t.message)
                )
                callback(false)
            }
        })
    }

    fun registerUser(
        context: Context,
        rootView: View,
        registerRequest: RegisterRequest,
        callback: (Boolean) -> Unit
    ) {
        ApiClient.apiService.registerUser(registerRequest).enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                if (response.isSuccessful) {
                    val generalResponse = response.body()
                    if (generalResponse != null && generalResponse.success) {
                        callback(true)
                    } else {
                        showCustomSnackbar(
                            rootView,
                            context,
                            context.getString(R.string.error_user_already_exists)
                        )
                        callback(false)
                    }
                } else {
                    showCustomSnackbar(
                        rootView,
                        context,
                        context.getString(
                            R.string.http_error,
                            response.code(),
                            response.message()
                        )
                    )
                    callback(false)
                }
            }

            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                showCustomSnackbar(
                    rootView,
                    context,
                    context.getString(R.string.unexpected_error, t.message)
                )
                callback(false)
            }
        })
    }

    fun searchUsers(
        context: Context,
        rootView: View,
        login: String,
        callback: (List<UserInfoResponseObject>?) -> Unit
    ) {
        ApiClient.apiService.searchUsers(login).enqueue(object : Callback<UserInfoResponse> {
            override fun onResponse(
                call: Call<UserInfoResponse>,
                response: Response<UserInfoResponse>
            ) {
                if (response.isSuccessful) {
                    val userInfoResponse = response.body()
                    if (userInfoResponse != null && userInfoResponse.success && !userInfoResponse.users.isNullOrEmpty()) {
                        callback(userInfoResponse.users.take(5))
                    } else {
                        showCustomSnackbar(
                            rootView,
                            context,
                            context.getString(R.string.not_found_any_users)
                        )
                        callback(emptyList())
                    }
                } else {
                    showCustomSnackbar(
                        rootView,
                        context,
                        context.getString(R.string.http_error, response.code(), response.message())
                    )
                    callback(null)
                }
            }

            override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                showCustomSnackbar(
                    rootView,
                    context,
                    context.getString(R.string.unexpected_error, t.message)
                )
                callback(null)
            }
        })
    }
}
