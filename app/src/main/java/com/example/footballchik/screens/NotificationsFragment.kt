package com.example.footballchik.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.footballchik.R
import com.example.footballchik.adapter.NotificationAdapter
import com.example.footballchik.repository.SupabaseRepository
import com.example.footballchik.utils.AuthManager
import com.example.footballchik.viewmodels.LeagueViewModel
import com.example.footballchik.viewmodels.LeagueViewModelFactory
import kotlinx.coroutines.launch

class NotificationsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationAdapter
    private val viewModel: LeagueViewModel by activityViewModels {
        LeagueViewModelFactory(SupabaseRepository())
    }
    private val repository = SupabaseRepository()
    private val teamNameCache = mutableMapOf<Long, String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.notifications_recycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Получаем реальный ID пользователя из AuthManager
        val authManager = AuthManager(requireContext())
        val userId = authManager.getUserId()

        Log.d("NotificationsFragment", "User ID: $userId")

        // Сначала загружаем кеш команд ДО создания адаптера
        lifecycleScope.launch {
            loadTeamsCache()

            // ПОСЛЕ загрузки кеша создаём адаптер
            adapter = NotificationAdapter(userId, teamNameCache)
            recyclerView.adapter = adapter

            // Загружаем матчи
            viewModel.loadMatches()

            // Подписываемся на изменения матчей
            viewModel.matches.collect { matches ->
                Log.d("NotificationsFragment", "Matches received: ${matches.size}")
                Log.d("NotificationsFragment", "Team cache size: ${teamNameCache.size}")

                val upcomingMatches = matches.filter {
                    it.status != "completed"
                }

                Log.d("NotificationsFragment", "Upcoming: ${upcomingMatches.size}")
                adapter.submitList(upcomingMatches)
            }
        }
    }

    private suspend fun loadTeamsCache() {
        try {
            repository.getAllTeams().collect { teams ->
                teams.forEach { team ->
                    teamNameCache[team.id] = team.name
                    Log.d("NotificationsFragment", "Cached team ${team.id}: ${team.name}")
                }
                Log.d("NotificationsFragment", "Teams cache loaded: ${teams.size}")
            }
        } catch (e: Exception) {
            Log.e("NotificationsFragment", "Error loading teams", e)
        }
    }
}
