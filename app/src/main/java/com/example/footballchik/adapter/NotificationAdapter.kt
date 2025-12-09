package com.example.footballchik.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.footballchik.databinding.ItemNotificationBinding
import com.example.footballchik.models.Match
import com.example.footballchik.repository.SupabaseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationAdapter(
    private val userId: String = "user_123",
    private val teamNameCache: Map<Long, String> = emptyMap()
) : ListAdapter<Match, NotificationAdapter.NotificationViewHolder>(NotificationDiffCallback()) {

    private val repository = SupabaseRepository()
    private val subscribedMatches = mutableSetOf<Long>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationViewHolder(binding, repository, userId, subscribedMatches, teamNameCache)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class NotificationViewHolder(
        private val binding: ItemNotificationBinding,
        private val repository: SupabaseRepository,
        private val userId: String,
        private val subscribedMatches: MutableSet<Long>,
        private val teamNameCache: Map<Long, String>
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(match: Match) {
            with(binding) {
                // Получаем названия из кеша ИЛИ используем ID как fallback
                val homeTeamName = teamNameCache[match.home_team_id] ?: "Team ${match.home_team_id}"
                val awayTeamName = teamNameCache[match.away_team_id] ?: "Team ${match.away_team_id}"

                Log.d("NotificationViewHolder", "Binding match: $homeTeamName vs $awayTeamName (Cache size: ${teamNameCache.size})")

                notificationTitle.text = "🔔 Грядущий матч"
                notificationMessage.text = "$homeTeamName vs $awayTeamName"
                notificationDate.text = "📅 ${match.date}"

                val isSubscribed = subscribedMatches.contains(match.id)
                notificationAction.text = if (isSubscribed) "✅ Подписан" else "📢 Подписаться"
                notificationAction.isEnabled = !isSubscribed

                notificationAction.setOnClickListener {
                    if (!isSubscribed) {
                        subscribeToMatch(match.id)
                    }
                }
            }
        }

        private fun subscribeToMatch(matchId: Long) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    Log.d("NotificationViewHolder", "Subscribing user $userId to match $matchId")

                    repository.subscribeToMatch(userId, matchId).collect { success ->
                        if (success) {
                            subscribedMatches.add(matchId)
                            binding.notificationAction.text = "✅ Подписан"
                            binding.notificationAction.isEnabled = false

                            Log.d("NotificationViewHolder", "Successfully subscribed to match $matchId")

                            Toast.makeText(
                                itemView.context,
                                "Вы подписались на матч!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Log.e("NotificationViewHolder", "Failed to subscribe to match $matchId")
                            Toast.makeText(
                                itemView.context,
                                "Ошибка при подписке",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("NotificationViewHolder", "Error subscribing", e)
                    Toast.makeText(
                        itemView.context,
                        "Ошибка: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private class NotificationDiffCallback : DiffUtil.ItemCallback<Match>() {
        override fun areItemsTheSame(oldItem: Match, newItem: Match) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Match, newItem: Match) = oldItem == newItem
    }
}
