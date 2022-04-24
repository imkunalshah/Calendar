package com.kunal.calendar.data.network.models

import com.google.gson.annotations.SerializedName

data class StoreDeleteTaskResponse(
    @SerializedName("status")
    var status: String
)
