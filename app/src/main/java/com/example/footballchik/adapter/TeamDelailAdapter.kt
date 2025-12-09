package com.example.footballchik.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.footballchik.databinding.ItemTeamDetailBinding
import com.example.footballchik.models.Team

class TeamDetailAdapter : ListAdapter<Team, TeamDetailAdapter.TeamDetailViewHolder>(TeamDetailDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamDetailViewHolder {
        val binding = ItemTeamDetailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TeamDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TeamDetailViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    class TeamDetailViewHolder(private val binding: ItemTeamDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(team: Team, position: Int) {
            with(binding) {
                // ✅ Используй свойства как они определены в Team data class
                teamRank.text = "${position + 1}"
                teamName.text = team.name
                teamPoints.text = "${team.points} очков"

                // Используй goals_for и goals_against (как в БД)
                teamStats.text = "Матчи: ${team.matches} | Голы: ${team.goals_for}:${team.goals_against}"

                // Защита от деления на ноль
                val winRatePercent = if (team.matches > 0) {
                    (team.points / (team.matches * 3) * 100).toInt()
                } else {
                    0
                }
                teamWinRate.text = "Процент побед: $winRatePercent%"
            }
        }
    }

    private class TeamDetailDiffCallback : DiffUtil.ItemCallback<Team>() {
        override fun areItemsTheSame(oldItem: Team, newItem: Team) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Team, newItem: Team) = oldItem == newItem
    }
}
