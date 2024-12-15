package com.example.tamz2mar0906withoutcompose.Http

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

data class LoginRequest(
    val loginName: String,
    val password: String
)

data class RegisterRequest(
    val loginName: String,
    val password: String,
    val phoneNumber: String,
    val gender: String,
    val photo: String? = null
)

data class UserInfoResponseObject(
    val id: Int,
    val loginName: String,
    val phoneNumber: String,
    val gender: String,
    val photo: String? = null
)

data class UserInfoResponse(
    val users: List<UserInfoResponseObject>?,
    val message: String,
    val success: Boolean
)

data class GroupResponseObject(
    val id: Int,
    val groupName: String,
    val groupCreator: String,
    val users: MutableList<UserInfoResponseObject>?
)

data class GroupResponse(
    val groups: List<GroupResponseObject>?,
    val message: String,
    val success: Boolean
)

data class GeneralResponse(
    val message: String,
    val success: Boolean
)

data class EventRequest(
    val groupId: Int,
    val creatorId: Int,
    val eventName: String,
    val eventDate: String,
    val location: String,
    val accommodationProvided: Int,
    val description: String? = null
)

data class EventResponseModel(
    val groupId: Int,
    val creatorId: Int,
    val eventName: String,
    val eventDate: String,
    val location: String,
    val accommodationProvided: Int,
    val description: String? = null
)

data class EventResponse(
    val events: List<EventResponseModel>?,
    val message: String,
    val success: Boolean
)


interface IApiClient {
    @Headers("Content-Type: application/json")
    @POST("login")
    fun loginUser(@Body request: LoginRequest): Call<GeneralResponse>

    @Headers("Content-Type: application/json")
    @POST("register")
    fun registerUser(@Body request: RegisterRequest): Call<GeneralResponse>

    @Headers("Content-Type: application/json")
    @POST("getUserDatas")
    fun getUserDatas(@Body request: List<String?>): Call<UserInfoResponse>

    @Headers("Content-Type: application/json")
    @PUT("createGroup")
    fun createGroup(
        @Query("group_name") groupName: String,
        @Query("creator_login_name") creatorLoginName: String
    ): Call<GeneralResponse>

    @Headers("Content-Type: application/json")
    @GET("getGroupsWithUsers")
    fun getGroups(@Query("login") login: String): Call<GroupResponse>

    @Headers("Content-Type: application/json")
    @GET("getUsersByLoginLike")
    fun searchUsers(@Query("login") login: String): Call<UserInfoResponse>

    @Headers("Content-Type: application/json")
    @PUT("insertUserToGroup")
    fun insertUserToGroup(
        @Query("group_id") groupId: String,
        @Query("user_id") userId: String
    ): Call<GeneralResponse>

    @Headers("Content-Type: application/json")
    @POST("insertEvent")
    fun insertEvent(@Body request: EventRequest): Call<GeneralResponse>

    @Headers("Content-Type: application/json")
    @POST("updateEvent")
    fun updateEvent(@Body request: EventRequest): Call<GeneralResponse>

    @Headers("Content-Type: application/json")
    @GET("getEventsByGroup")
    fun getEventsByGroup(@Query("group_id") groupId: Int): Call<EventResponse>
}