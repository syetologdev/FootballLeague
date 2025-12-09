package com.example.footballchik.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.footballchik.databinding.ItemMatchBinding
import com.example.footballchik.models.Match
import com.example.footballchik.repository.SupabaseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MatchAdapter : ListAdapter<Match, MatchAdapter.MatchViewHolder>(MatchDiffCallback()) {

    private val repository = SupabaseRepository()
    private val teamNameCache = mutableMapOf<Long, String>()
    private var teamsLoaded = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val binding = ItemMatchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MatchViewHolder(binding, teamNameCache)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // Загружаем все команды один раз при инициализации
    fun loadTeamsCache() {
        if (teamsLoaded) return

        CoroutineScope(Dispatchers.Main).launch {
            try {
                repository.getAllTeams().collect { teamsList ->
                    teamsList.forEach { team ->
                        teamNameCache[team.id] = team.name
                        Log.d("MatchAdapter", "Cached: ${team.id} -> ${team.name}")
                    }
                    teamsLoaded = true
                    notifyDataSetChanged() // Обновляем адаптер после загрузки
                }
            } catch (e: Exception) {
                Log.e("MatchAdapter", "Error loading teams cache", e)
            }
        }
    }

    class MatchViewHolder(
        private val binding: ItemMatchBinding,
        private val teamNameCache: Map<Long, String>
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(match: Match) {
            with(binding) {
                // Используем кешированные названия
                val homeTeamName = teamNameCache[match.home_team_id] ?: "Team ${match.home_team_id}"
                val awayTeamName = teamNameCache[match.away_team_id] ?: "Team ${match.away_team_id}"

                this.homeTeamName.text = homeTeamName
                this.awayTeamName.text = awayTeamName

                matchDate.text = "📅 ${match.date}"
                matchStatus.text = "Статус: ${match.status}"

                if (match.status == "completed" && match.home_score != null && match.away_score != null) {
                    matchScore.text = "${match.home_score} : ${match.away_score}"
                    matchScore.visibility = android.view.View.VISIBLE
                } else {
                    matchScore.text = "⏳ Грядущий матч"
                    matchScore.visibility = android.view.View.VISIBLE
                }

                Log.d("MatchAdapter", "Binding match: $homeTeamName vs $awayTeamName")
            }
        }
    }

    private class MatchDiffCallback : DiffUtil.ItemCallback<Match>() {
        override fun areItemsTheSame(oldItem: Match, newItem: Match) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Match, newItem: Match) = oldItem == newItem
    }
}
