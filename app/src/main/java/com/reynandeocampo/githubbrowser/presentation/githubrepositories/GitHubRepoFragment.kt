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
import com.reynandeocampo.data.api.Status
import com.reynandeocampo.domain.models.GitRepo
import com.reynandeocampo.githubbrowser.databinding.FragmentRepositoriesBinding
import com.reynandeocampo.githubbrowser.presentation.SharedViewModel

class GitHubRepoFragment : Fragment() {

    private lateinit var binding: FragmentRepositoriesBinding
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        binding = FragmentRepositoriesBinding.inflate(layoutInflater)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        binding.recyclerViewRepo.adapter = GitHubRepoAdapter(OnClickListener {
            openUrlInBrowser(it.url)
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
                    }
                    Status.LOADING -> {
                    }
                    Status.SUCCESS -> {
                        resource.data?.let { data ->
                            renderData(data)
                        }
                    }
                    Status.ERROR -> {
                        resource.message?.let { message ->
                            showToastMessage(message)
                        }
                    }
                }
            }
        })
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun renderData(data: List<GitRepo>) {
        (binding.recyclerViewRepo.adapter as GitHubRepoAdapter).gitRepos = data
    }

    private fun openUrlInBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
