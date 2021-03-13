package com.reynandeocampo.githubbrowser.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.reynandeocampo.data.api.Status
import com.reynandeocampo.githubbrowser.databinding.FragmentHomeBinding
import com.reynandeocampo.githubbrowser.presentation.adapter.RepoListAdapter

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var repoListAdapter: RepoListAdapter

    private var customTimer: CustomTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        binding = FragmentHomeBinding.inflate(layoutInflater)
        homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)

        initAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearchView()
        observeViewModels()
    }

    private fun initAdapter() {
        repoListAdapter = RepoListAdapter(OnClickListener { openUrlInBrowser(it.url) }) {
            homeViewModel.retry()
        }
        binding.recyclerViewRepo.adapter = repoListAdapter
    }

    private fun setupSearchView() {
        binding.searchView.queryHint = "Search repositories"
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isNotBlank()) {
                    homeViewModel.searchRepo(query)
                } else {
                    resetToIdle()
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (customTimer != null) {
                    customTimer!!.cancel()
                    customTimer = null
                }

                if (newText.isNotBlank()) {
                    customTimer = CustomTimer(1000, 500) {
                        homeViewModel.searchRepo(newText)
                    }
                    customTimer!!.start()
                } else {
                    resetToIdle()
                }
                return false
            }
        })
    }

    fun resetToIdle() {
        showToastMessage("closed")
        homeViewModel.updateViewStatus(Status.IDLE)
    }

    private fun observeViewModels() {
        homeViewModel.gitHubRepoList.observe(viewLifecycleOwner, {
            repoListAdapter.submitList(it)
        })

        homeViewModel.networkStatus.observe(viewLifecycleOwner, {
            it?.let { resource ->
                resource.message?.let { message ->
                    showToastMessage(message)
                }

                binding.layoutLoading.root.visibility =
                    if (homeViewModel.listIsEmpty() && resource.status == Status.LOADING) View.VISIBLE else View.GONE
                binding.layoutNoResult.root.visibility =
                    if (homeViewModel.listIsEmpty() && resource.status == Status.ERROR) View.VISIBLE else View.GONE
                if (!homeViewModel.listIsEmpty()) {
                    repoListAdapter.setStatus(resource.status)
                }
            }
        })

        homeViewModel.viewStatus.observe(viewLifecycleOwner, {
            it?.let { status ->
                when (status) {
                    Status.IDLE -> showIdleView()
                    Status.LOADING -> showLoadingView()
                    Status.SUCCESS -> showResultView()
                    Status.ERROR -> showNoResultView()
                }
            }
        })
    }

    private fun openUrlInBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun showIdleView() {
        binding.recyclerViewRepo.visibility = View.GONE
        binding.layoutLoading.root.visibility = View.GONE
        binding.layoutNoResult.root.visibility = View.GONE
        binding.layoutDefault.root.visibility = View.VISIBLE
    }

    private fun showLoadingView() {
        binding.recyclerViewRepo.visibility = View.GONE
        binding.layoutLoading.root.visibility = View.VISIBLE
        binding.layoutNoResult.root.visibility = View.GONE
        binding.layoutDefault.root.visibility = View.GONE
    }

    private fun showNoResultView() {
        binding.recyclerViewRepo.visibility = View.GONE
        binding.layoutLoading.root.visibility = View.GONE
        binding.layoutNoResult.root.visibility = View.VISIBLE
        binding.layoutDefault.root.visibility = View.GONE
    }

    private fun showResultView() {
        binding.recyclerViewRepo.visibility = View.VISIBLE
        binding.layoutLoading.root.visibility = View.GONE
        binding.layoutNoResult.root.visibility = View.GONE
        binding.layoutDefault.root.visibility = View.GONE
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun hideSoftKeyboard() {

    }

    class CustomTimer(
        millisInFuture: Long, countDownInterval: Long,
        var onFinishCallback: () -> Unit
    ) : CountDownTimer(millisInFuture, countDownInterval) {

        override fun onFinish() {
            onFinishCallback()
        }

        override fun onTick(p0: Long) {}
    }
}
