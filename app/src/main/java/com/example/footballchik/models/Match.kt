package com.example.footballchik.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Match(
    val id: Long = 0,
    @SerialName("home_team_id")
    val home_team_id: Long = 0,
    @SerialName("away_team_id")
    val away_team_id: Long = 0,
    @SerialName("home_score")
    val home_score: Int? = null,
    @SerialName("away_score")
    val away_score: Int? = null,
    val date: String = "",
    val status: String = "pending",
    @SerialName("created_at")
    val created_at: String = ""
) {
    val homeTeamId: Long get() = home_team_id
    val awayTeamId: Long get() = away_team_id
    val homeScore: Int? get() = home_score
    val awayScore: Int? get() = away_score
    val createdAt: String get() = created_at
}
