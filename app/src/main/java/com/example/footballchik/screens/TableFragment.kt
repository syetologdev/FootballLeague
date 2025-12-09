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
import com.example.footballchik.adapter.TeamAdapter
import com.example.footballchik.repository.SupabaseRepository
import com.example.footballchik.viewmodels.LeagueViewModel
import com.example.footballchik.viewmodels.LeagueViewModelFactory
import kotlinx.coroutines.launch

class TableFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TeamAdapter
    private val viewModel: LeagueViewModel by activityViewModels {
        LeagueViewModelFactory(SupabaseRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_table, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.teams_recycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = TeamAdapter()
        recyclerView.adapter = adapter

        // Подписаться на данные из ViewModel
        lifecycleScope.launch {
            viewModel.teams.collect { teams ->
                Log.d("TableFragment", "Teams received: ${teams.size}")
                teams.forEach { team ->
                    Log.d("TableFragment", "Team: ${team.name} - Points: ${team.points}")
                }
                adapter.submitList(teams)
            }
        }

        // Подписаться на ошибки
        lifecycleScope.launch {
            viewModel.error.collect { error ->
                if (error != null) {
                    Log.e("TableFragment", "Error: $error")
                }
            }
        }

        // Загрузить данные
        Log.d("TableFragment", "Loading teams...")
        viewModel.loadTeams()
    }
}
