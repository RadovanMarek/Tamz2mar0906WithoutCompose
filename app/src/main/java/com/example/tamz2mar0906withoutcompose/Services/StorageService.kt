package com.example.tamz2mar0906withoutcompose.Services

import android.content.Context
import com.example.tamz2mar0906withoutcompose.Http.GroupResponseObject
import com.example.tamz2mar0906withoutcompose.Http.UserInfoResponseObject
import com.google.gson.Gson

object StorageService {

    fun saveCurrentUser(context: Context, user: UserInfoResponseObject?) {
        val sharedPreferences = context.getSharedPreferences("UserStorage", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val userJson = Gson().toJson(user)
        editor.putString("currentUser", userJson)
        editor.apply()
    }

    fun loadCurrentUser(context: Context): UserInfoResponseObject? {
        val sharedPreferences = context.getSharedPreferences("UserStorage", Context.MODE_PRIVATE)
        val userJson = sharedPreferences.getString("currentUser", null)

        return userJson?.let { Gson().fromJson(it, UserInfoResponseObject::class.java) }
    }

    fun saveCurrentGroups(context: Context, groups: List<GroupResponseObject>?) {
        val sharedPreferences = context.getSharedPreferences("GroupStorage", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val groupsJson = Gson().toJson(groups)
        editor.putString("currentGroups", groupsJson)
        editor.apply()
    }

    fun loadCurrentGroups(context: Context): List<GroupResponseObject>? {
        val sharedPreferences = context.getSharedPreferences("GroupStorage", Context.MODE_PRIVATE)
        val groupsJson = sharedPreferences.getString("currentGroups", null)

        return groupsJson?.let { Gson().fromJson(it, Array<GroupResponseObject>::class.java).toList() }
    }
}
