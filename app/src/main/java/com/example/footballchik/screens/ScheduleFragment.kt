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
import com.example.footballchik.adapter.MatchAdapter
import com.example.footballchik.repository.SupabaseRepository
import com.example.footballchik.viewmodels.LeagueViewModel
import com.example.footballchik.viewmodels.LeagueViewModelFactory
import kotlinx.coroutines.launch

class ScheduleFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MatchAdapter
    private val viewModel: LeagueViewModel by activityViewModels {
        LeagueViewModelFactory(SupabaseRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.schedule_recycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = MatchAdapter()
        recyclerView.adapter = adapter

        // Загружаем кеш команд один раз
        adapter.loadTeamsCache()  // ✅ ДОБАВЬ ЭТУ СТРОКУ

        viewModel.loadMatches()

        lifecycleScope.launch {
            viewModel.matches.collect { matches ->
                Log.d("ScheduleFragment", "Matches received: ${matches.size}")
                adapter.submitList(matches)
            }
        }
    }

}
