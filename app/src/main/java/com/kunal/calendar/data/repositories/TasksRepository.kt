package com.kunal.calendar.data.repositories

import androidx.lifecycle.MutableLiveData
import com.kunal.calendar.data.network.SafeApiRequest
import com.kunal.calendar.data.network.apis.EventsApi
import com.kunal.calendar.data.network.datastore.DatastoreManager
import com.kunal.calendar.data.network.models.Task
import com.kunal.calendar.utils.AppConstants.Remote.TASK_ID
import com.kunal.calendar.utils.AppConstants.Remote.USER_ID
import kotlinx.coroutines.flow.first

class TasksRepository(
    private val api: EventsApi,
    private val datastoreManager: DatastoreManager
) : SafeApiRequest() {

    private var taskList = MutableLiveData<List<Task>>()

    suspend fun saveEvent(body: HashMap<String, Any?>): String {
        return createEvent(body)
    }

    private suspend fun createEvent(body: HashMap<String, Any?>): String {
        val response = apiRequest { api.storeCalendarTask(body) }
        return response.status
    }

    suspend fun getEvents(): MutableLiveData<List<Task>> {
        fetchEvents()
        return taskList
    }

    private suspend fun fetchEvents() {
        val userId = datastoreManager.userId.first()
        val requestBody = hashMapOf(
            USER_ID to userId
        )
        val response = apiRequest { api.fetchCalendarTasks(requestBody) }
        taskList.postValue(response.taskList)
    }

    suspend fun removeEvent(taskId: String): String {
        return deleteEvent(taskId)
    }

    private suspend fun deleteEvent(taskId: String): String {
        val userId = datastoreManager.userId.first()
        val requestBody = hashMapOf(
            USER_ID to userId,
            TASK_ID to taskId
        )
        val response = apiRequest { api.deleteCalendarTask(requestBody) }
        return response.status
    }
}