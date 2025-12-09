package com.example.footballchik.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Team(
    val id: Long = 0,
    val name: String = "",
    val points: Int = 0,
    val matches: Int = 0,
    @SerialName("goals_for")
    val goals_for: Int = 0,
    @SerialName("goals_against")
    val goals_against: Int = 0,
    @SerialName("created_at")
    val created_at: String = ""
) {
    val goalsFor: Int get() = goals_for
    val goalsAgainst: Int get() = goals_against
    val createdAt: String get() = created_at
}
