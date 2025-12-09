package com.example.footballchik.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Subscription(
    val id: Long = 0,
    @SerialName("user_id")
    val userId: String,
    @SerialName("match_id")
    val matchId: Long,
    @SerialName("created_at")
    val createdAt: String? = null
)
