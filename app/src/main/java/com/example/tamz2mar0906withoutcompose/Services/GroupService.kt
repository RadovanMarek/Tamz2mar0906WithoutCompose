package com.example.tamz2mar0906withoutcompose.Services

import android.content.Context
import android.view.View
import com.example.tamz2mar0906withoutcompose.Http.ApiClient
import com.example.tamz2mar0906withoutcompose.Http.GeneralResponse
import com.example.tamz2mar0906withoutcompose.Http.GroupResponse
import com.example.tamz2mar0906withoutcompose.Http.GroupResponseObject
import com.example.tamz2mar0906withoutcompose.R
import com.example.tamz2mar0906withoutcompose.Utilities.showCustomSnackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object GroupService {
    fun getGroups(
        context: Context,
        rootView: View,
        loginName: String,
        callback: (List<GroupResponseObject>) -> Unit
    ) {
        ApiClient.apiService.getGroups(loginName).enqueue(object : Callback<GroupResponse> {
            override fun onResponse(call: Call<GroupResponse>, response: Response<GroupResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val groups = response.body()?.groups ?: emptyList()
                    callback(groups)
                } else {
                    showCustomSnackbar(
                        rootView,
                        context,
                        context.getString(R.string.error_loading_groups, response.body()?.message)
                    )
                }
            }

            override fun onFailure(call: Call<GroupResponse>, t: Throwable) {
                showCustomSnackbar(
                    rootView,
                    context,
                    context.getString(R.string.unexpected_error, t.message)
                )
            }
        })
    }

    fun createGroup(
        context: Context,
        rootView: View,
        groupName: String,
        loginName: String,
        callback: (Boolean) -> Unit
    ) {
        ApiClient.apiService.createGroup(groupName, loginName).enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    showCustomSnackbar(
                        rootView,
                        context,
                        context.getString(R.string.group_created_successfully, groupName)
                    )
                    callback(true)
                } else {
                    showCustomSnackbar(
                        rootView,
                        context,
                        context.getString(R.string.error_creating_group, response.body()?.message)
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
}
