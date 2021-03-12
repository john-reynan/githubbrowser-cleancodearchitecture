package com.reynandeocampo.githubbrowser.presentation.home

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.reynandeocampo.data.api.Status
import com.reynandeocampo.domain.models.GitRepo
import com.reynandeocampo.githubbrowser.databinding.FragmentHomeBinding
import com.reynandeocampo.githubbrowser.presentation.GitHubRepoAdapter
import com.reynandeocampo.githubbrowser.presentation.OnClickListener
import com.reynandeocampo.githubbrowser.presentation.PaginationScrollListener
import com.reynandeocampo.githubbrowser.utils.ConnectivityHelper

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var gitHubRepoAdapter: GitHubRepoAdapter

    private var customTimer: CustomTimer? = null

    private val PAGE_START = 1
    private var currentPage = PAGE_START

    private var isLoading = false
    private var isLastPage = false

    private var searchText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        binding = FragmentHomeBinding.inflate(layoutInflater)
        homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        setupRecyclerView()
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
        observeViewModels()
        setupSearchView()
    }

    private fun setupRecyclerView() {
        gitHubRepoAdapter = GitHubRepoAdapter(OnClickListener {
            openUrlInBrowser(it.url)
        })

        binding.recyclerViewRepo.adapter = gitHubRepoAdapter
        binding.recyclerViewRepo.addOnScrollListener(object :
            PaginationScrollListener(binding.recyclerViewRepo.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage += 1

                loadNextPage()
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })
    }

    private fun setupSearchView() {
        binding.searchView.queryHint = "Search repositories"
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isNotBlank()) {
                    searchText = query
                    currentPage = 1
                    searchRepo(query)
                } else {
                    homeViewModel.setObservablesToPending()
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
                        searchText = newText
                        currentPage = 1
                        searchRepo(newText)
                    }
                    customTimer!!.start()
                } else {
                    homeViewModel.setObservablesToPending()
                }

                return false
            }
        })

        binding.searchView.setOnCloseListener {
            homeViewModel.setObservablesToPending()
            hideSoftKeyboard()
            false
        }
    }

    private fun observeViewModels() {
        homeViewModel.gitHubRepoList.observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.PENDING -> {
                        showPendingView()
                    }
                    Status.LOADING -> {
                        showLoadingView()
                    }
                    Status.SUCCESS -> {
                        resource.data?.let { data ->
                            if (data.isNotEmpty()) {
                                if (currentPage == 1) {
                                    renderData(data as MutableList<GitRepo>)
                                    showResultView()
                                } else if (currentPage > 1) {
                                    isLoading = false
                                    gitHubRepoAdapter.addNewItems(data)
                                }

                            } else {
                                showNoResultView()
                            }
                        }
                    }
                    Status.ERROR -> {
                        resource.message?.let { message ->
                            showToastMessage(message)
                        }
                        showNoResultView()
                    }
                }
            }
        })

        homeViewModel.currentPage.observe(viewLifecycleOwner, {
            it?.let { page ->
                if (page == 1) {
                    gitHubRepoAdapter.addLoadingView()
                }
            }
        })
    }

    private fun loadNextPage() {
        if (ConnectivityHelper.isConnectedToNetwork(requireContext())) {
            homeViewModel.searchGitHubRepo(searchText, 15, currentPage)
        } else {
            homeViewModel.setObservablesToPending()
            showToastMessage("Please turn on your internet connection.")
        }
    }

    private fun searchRepo(query: String) {
        if (ConnectivityHelper.isConnectedToNetwork(requireContext())) {
            homeViewModel.searchGitHubRepo(query, 15, 1)
        } else {
            homeViewModel.setObservablesToPending()
            showToastMessage("Please turn on your internet connection.")
        }
    }

    private fun openUrlInBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun renderData(data: MutableList<GitRepo>) {
        (binding.recyclerViewRepo.adapter as GitHubRepoAdapter).gitRepos = data
    }

    private fun showPendingView() {
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
