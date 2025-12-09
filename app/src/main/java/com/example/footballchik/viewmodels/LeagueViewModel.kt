package com.example.footballchik.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballchik.models.Team
import com.example.footballchik.models.Match
import com.example.footballchik.models.Player
import com.example.footballchik.repository.SupabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LeagueViewModel(
    private val repository: SupabaseRepository
) : ViewModel() {

    private val _teams = MutableStateFlow<List<Team>>(emptyList())
    val teams: StateFlow<List<Team>> = _teams.asStateFlow()

    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    val matches: StateFlow<List<Match>> = _matches.asStateFlow()

    private val _players = MutableStateFlow<List<Player>>(emptyList())
    val players: StateFlow<List<Player>> = _players.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadTeams() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAllTeams().collect { teams ->
                _teams.value = teams.sortedByDescending { it.points }
                _isLoading.value = false
            }
        }
    }

    fun loadMatches() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAllMatches().collect { matches ->
                _matches.value = matches
                _isLoading.value = false
            }
        }
    }

    // ✅ ИСПРАВИТЬ: teamId теперь Long
    fun loadPlayersByTeam(teamId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getPlayersByTeam(teamId).collect { players ->
                _players.value = players
                _isLoading.value = false
            }
        }
    }

    fun addTeam(team: Team) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.addTeam(team).collect { success ->
                if (success) {
                    loadTeams()
                } else {
                    _error.value = "Ошибка при добавлении команды"
                }
                _isLoading.value = false
            }
        }
    }

    fun addMatch(match: Match) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.addMatch(match).collect { success ->
                if (success) {
                    loadMatches()
                } else {
                    _error.value = "Ошибка при добавлении матча"
                }
                _isLoading.value = false
            }
        }
    }

    // ✅ ИСПРАВИТЬ: matchId теперь Int, но может быть Long
    fun updateMatchResult(matchId: Long, homeScore: Int, awayScore: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateMatch(matchId, homeScore, awayScore).collect { success ->
                if (success) {
                    loadMatches()
                    loadTeams()
                } else {
                    _error.value = "Ошибка при сохранении результата"
                }
                _isLoading.value = false
            }
        }
    }

    fun addPlayer(player: Player) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.addPlayer(player).collect { success ->
                if (success) {
                    // ✅ ИСПРАВИТЬ: теперь player.teamId типа Long
                    loadPlayersByTeam(player.teamId)
                } else {
                    _error.value = "Ошибка при добавлении игрока"
                }
                _isLoading.value = false
            }
        }
    }

    // ✅ ИСПРАВИТЬ: playerId и teamId теперь Long
    fun deletePlayer(playerId: Long, teamId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.deletePlayer(playerId).collect { success ->
                if (success) {
                    loadPlayersByTeam(teamId)
                } else {
                    _error.value = "Ошибка при удалении игрока"
                }
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
