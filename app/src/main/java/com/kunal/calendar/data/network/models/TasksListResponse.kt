package com.kunal.calendar.data.network.models

import com.google.gson.annotations.SerializedName

data class TasksListResponse(
    @SerializedName("tasks")
    var taskList:List<Task>
)
