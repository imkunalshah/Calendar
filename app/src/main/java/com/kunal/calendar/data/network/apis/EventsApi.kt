package com.kunal.calendar.data.network.apis

import com.kunal.calendar.data.network.models.StoreDeleteTaskResponse
import com.kunal.calendar.data.network.models.TasksListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import kotlin.collections.HashMap

interface EventsApi {

    @POST("storeCalendarTask")
    suspend fun storeCalendarTask(@Body body: HashMap<String, Any?>): Response<StoreDeleteTaskResponse>

    @POST("getCalendarTaskLists")
    suspend fun fetchCalendarTasks(@Body body: HashMap<String, String?>): Response<TasksListResponse>

    @POST("deleteCalendarTask")
    suspend fun deleteCalendarTask(@Body body: HashMap<String, String?>): Response<StoreDeleteTaskResponse>
}