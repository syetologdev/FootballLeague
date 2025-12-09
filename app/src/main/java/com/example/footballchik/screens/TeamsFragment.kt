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
import com.example.footballchik.adapter.TeamDetailAdapter
import com.example.footballchik.repository.SupabaseRepository
import com.example.footballchik.viewmodels.LeagueViewModel
import com.example.footballchik.viewmodels.LeagueViewModelFactory
import kotlinx.coroutines.launch

class TeamsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TeamDetailAdapter
    private val viewModel: LeagueViewModel by activityViewModels {
        LeagueViewModelFactory(SupabaseRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_teams, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.teams_detail_recycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = TeamDetailAdapter()
        recyclerView.adapter = adapter

        lifecycleScope.launch {
            viewModel.teams.collect { teams ->
                Log.d("TeamsFragment", "Teams received: ${teams.size}")
                adapter.submitList(teams)
            }
        }

        Log.d("TeamsFragment", "Loading teams...")
        viewModel.loadTeams()
    }
}
