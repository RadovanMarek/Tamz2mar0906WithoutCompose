package com.example.tamz2mar0906withoutcompose.Services

import android.content.Context
import android.view.View
import com.example.tamz2mar0906withoutcompose.Http.ApiClient
import com.example.tamz2mar0906withoutcompose.Http.EventRequest
import com.example.tamz2mar0906withoutcompose.Http.EventResponse
import com.example.tamz2mar0906withoutcompose.Http.EventResponseModel
import com.example.tamz2mar0906withoutcompose.Http.GeneralResponse
import com.example.tamz2mar0906withoutcompose.R
import com.example.tamz2mar0906withoutcompose.Utilities.showCustomSnackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object EventService {
    fun insertEvent(
        context: Context,
        rootView: View,
        eventRequest: EventRequest,
        callback: (Boolean) -> Unit
    ) {
        ApiClient.apiService.insertEvent(eventRequest).enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    showCustomSnackbar(
                        rootView,
                        context,
                        context.getString(R.string.event_created_successfully, eventRequest.eventName)
                    )
                    callback(true)
                } else {
                    showCustomSnackbar(
                        rootView,
                        context,
                        context.getString(R.string.error_creating_event, response.body()?.message)
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

    fun updateEvent(
        context: Context,
        rootView: View,
        eventRequest: EventRequest,
        callback: (Boolean) -> Unit
    ) {
        ApiClient.apiService.updateEvent(eventRequest).enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    showCustomSnackbar(
                        rootView,
                        context,
                        context.getString(R.string.event_updated_successfully, eventRequest.eventName)
                    )
                    callback(true)
                } else {
                    showCustomSnackbar(
                        rootView,
                        context,
                        context.getString(R.string.error_updating_event, response.body()?.message)
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

    fun getEventsByGroup(
        context: Context,
        rootView: View,
        groupId: Int,
        callback: (List<EventResponseModel>) -> Unit
    ) {
        ApiClient.apiService.getEventsByGroup(groupId).enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val events = response.body()?.events ?: emptyList()
                    callback(events)
                } else {
                    showCustomSnackbar(
                        rootView,
                        context,
                        context.getString(R.string.error_loading_events, response.body()?.message)
                    )
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                showCustomSnackbar(
                    rootView,
                    context,
                    context.getString(R.string.unexpected_error, t.message)
                )
            }
        })
    }
}
