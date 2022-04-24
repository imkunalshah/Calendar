package com.kunal.calendar.data.network.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Task(
    @SerializedName("task_id")
    var taskId:Int,
    @SerializedName("task_detail")
    var taskDetails:TaskDetail
) : Serializable
