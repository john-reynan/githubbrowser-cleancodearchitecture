package com.reynandeocampo.githubbrowser.presentation.githubrepositories

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.reynandeocampo.data.api.Status
import com.reynandeocampo.domain.models.GitRepo
import com.reynandeocampo.githubbrowser.databinding.FragmentRepositoriesBinding
import com.reynandeocampo.githubbrowser.presentation.SharedViewModel
import com.reynandeocampo.githubbrowser.utils.ConnectivityHelper

class GitHubRepoFragment : Fragment() {

    private lateinit var binding: FragmentRepositoriesBinding
    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var gitHubRepoAdapter: GitHubRepoAdapter

    private val PAGE_START = 1

    private var isLoading = false
    private var isLastPage = false

    private var currentPage = PAGE_START

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        binding = FragmentRepositoriesBinding.inflate(layoutInflater)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

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
    }

    private fun observeViewModels() {
        sharedViewModel.gitHubRepoList.observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.PENDING -> {
                        showPendingView()
                    }
                    Status.LOADING -> {
//                        showLoadingView()
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

        sharedViewModel.currentPage.observe(viewLifecycleOwner, {
            it?.let { page ->
                if (page == 1) {
                    gitHubRepoAdapter.addLoadingView()
                }
            }
        })
    }

    private fun loadNextPage() {
        if (ConnectivityHelper.isConnectedToNetwork(requireContext())) {
            sharedViewModel.searchGitHubRepo("u", 15, currentPage)
        } else {
            sharedViewModel.setObservablesToPending()
            showToastMessage("Please turn on your internet connection.")
        }
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun renderData(data: MutableList<GitRepo>) {
        (binding.recyclerViewRepo.adapter as GitHubRepoAdapter).gitRepos = data
    }

    private fun showPendingView() {
        binding.recyclerViewRepo.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.noResultLayout.visibility = View.GONE
        binding.defaultLayout.visibility = View.VISIBLE
    }

    private fun showLoadingView() {
        binding.recyclerViewRepo.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.noResultLayout.visibility = View.GONE
        binding.defaultLayout.visibility = View.GONE
    }

    private fun showNoResultView() {
        binding.recyclerViewRepo.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.noResultLayout.visibility = View.VISIBLE
        binding.defaultLayout.visibility = View.GONE
    }

    private fun showResultView() {
        binding.recyclerViewRepo.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.noResultLayout.visibility = View.GONE
        binding.defaultLayout.visibility = View.GONE
    }

    private fun openUrlInBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
