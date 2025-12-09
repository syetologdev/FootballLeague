package com.example.footballchik.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val id: Long = 0,
    @SerialName("user_id")
    val user_id: String = "",
    @SerialName("match_id")
    val match_id: Long = 0,
    @SerialName("is_subscribed")
    val is_subscribed: Boolean = false,
    @SerialName("created_at")
    val created_at: String = ""
)
