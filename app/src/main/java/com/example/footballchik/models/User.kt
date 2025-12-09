package com.example.footballchik.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val email: String = "",
    @SerialName("password_hash")
    val password_hash: String = "",
    val role: String = "viewer",
    @SerialName("team_id")
    val team_id: Long? = null,
    @SerialName("created_at")
    val created_at: String = ""
)
