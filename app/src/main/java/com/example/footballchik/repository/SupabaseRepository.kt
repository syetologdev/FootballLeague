package com.example.footballchik.repository

import android.util.Log
import com.example.footballchik.models.Match
import com.example.footballchik.models.Player
import com.example.footballchik.models.Subscription
import com.example.footballchik.models.Team
import com.example.footballchik.models.User
import com.example.footballchik.utils.Constants
import com.example.footballchik.utils.PasswordHasher
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.Realtime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

class SupabaseRepository {

    private val supabase: SupabaseClient = createSupabaseClient(
        supabaseUrl = Constants.SUPABASE_URL,
        supabaseKey = Constants.SUPABASE_KEY
    ) {
        install(Postgrest)
        install(Realtime)
    }


    fun registerUser(email: String, password: String): Flow<Pair<Boolean, String>> = flow {
        try {
            val hash = PasswordHasher.hashPassword(password)
            val userId = UUID.randomUUID().toString()

            val userMap = mapOf(
                "id" to userId,
                "email" to email,
                "password_hash" to hash,
                "role" to "viewer"
            )

            supabase.from("users").insert(userMap)
            emit(Pair(true, userId))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Pair(false, "Ошибка регистрации: ${e.message}"))
        }
    }

    fun loginUser(email: String, password: String): Flow<Pair<Boolean, User?>> = flow {
        try {
            val users = supabase.from("users").select {
                filter {
                    eq("email", email)
                }
            }.decodeList<User>()

            if (users.isNotEmpty()) {
                val user = users[0]
                if (PasswordHasher.verifyPassword(password, user.password_hash)) {
                    emit(Pair(true, user))
                } else {
                    emit(Pair(false, null)) // Неверный пароль
                }
            } else {
                emit(Pair(false, null)) // Пользователь не найден
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Pair(false, null))
        }
    }


    fun subscribeToMatch(userId: String, matchId: Long): Flow<Boolean> = flow {
        try {
            supabase.from("subscriptions").insert(
                mapOf(
                    "user_id" to userId,
                    "match_id" to matchId.toString()
                )
            )
            emit(true)
        } catch (e: Exception) {
            Log.e("SupabaseRepo", "Error subscribing: ${e.message}")
            e.printStackTrace()
            emit(false)
        }
    }

    fun getSubscriptionsByUser(userId: String): Flow<List<Subscription>> = flow {
        try {
            val subs = supabase.from("subscriptions").select {
                filter {
                    eq("user_id", userId)
                }
            }.decodeList<Subscription>()
            emit(subs)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // ===========================
    // ⚽ TEAMS
    // ===========================

    fun getAllTeams(): Flow<List<Team>> = flow {
        try {
            val teams = supabase.from(Constants.TEAMS_TABLE).select().decodeList<Team>()
            emit(teams)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }

    fun addTeam(team: Team): Flow<Boolean> = flow {
        try {
            supabase.from(Constants.TEAMS_TABLE).insert(team)
            emit(true)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(false)
        }
    }

    // ===========================
    // 📅 MATCHES
    // ===========================

    fun getAllMatches(): Flow<List<Match>> = flow {
        try {
            val matches = supabase.from(Constants.MATCHES_TABLE).select().decodeList<Match>()
            emit(matches)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }

    fun addMatch(match: Match): Flow<Boolean> = flow {
        try {
            supabase.from(Constants.MATCHES_TABLE).insert(match)
            emit(true)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(false)
        }
    }

    fun updateMatch(matchId: Long, homeScore: Int, awayScore: Int): Flow<Boolean> = flow {
        try {
            supabase.from(Constants.MATCHES_TABLE).update(mapOf(
                "home_score" to homeScore,
                "away_score" to awayScore,
                "status" to "completed"
            )) {
                filter { eq("id", matchId) }
            }
            emit(true)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(false)
        }
    }

    // ===========================
    // 🏃 PLAYERS
    // ===========================

    fun getPlayersByTeam(teamId: Long): Flow<List<Player>> = flow {
        try {
            val players = supabase.from(Constants.PLAYERS_TABLE).select {
                filter { eq("team_id", teamId) }
            }.decodeList<Player>()
            emit(players)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }

    fun addPlayer(player: Player): Flow<Boolean> = flow {
        try {
            supabase.from(Constants.PLAYERS_TABLE).insert(player)
            emit(true)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(false)
        }
    }

    fun deletePlayer(playerId: Long): Flow<Boolean> = flow {
        try {
            supabase.from(Constants.PLAYERS_TABLE).delete {
                filter { eq("id", playerId) }
            }
            emit(true)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(false)
        }
    }
}
