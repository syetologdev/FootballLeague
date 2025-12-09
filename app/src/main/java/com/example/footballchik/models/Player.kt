package com.example.footballchik.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Player(
    @SerialName("id")
    val id: Long = 0,
    @SerialName("team_id")
    val teamId: Long = 0,
    @SerialName("name")
    val name: String = "",
    @SerialName("number")
    val number: Int = 0,
    @SerialName("position")
    val position: String = "",
    @SerialName("goals")
    val goals: Int = 0,
    @SerialName("assists")
    val assists: Int = 0,
    @SerialName("created_at")
    val createdAt: String = ""
)
