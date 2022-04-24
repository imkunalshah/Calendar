package com.kunal.calendar.data.network.models

import com.google.gson.annotations.SerializedName

data class TaskDetail(
    @SerializedName("title")
    var title: String,
    @SerializedName("description")
    var description: String,
    @SerializedName("date")
    var date: String?,
    @SerializedName("location")
    var location: String,
    @SerializedName("time")
    var time: String?,
)
