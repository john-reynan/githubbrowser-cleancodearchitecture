package com.reynandeocampo.githubbrowser.presentation.search

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.reynandeocampo.githubbrowser.databinding.FragmentSearchBinding
import com.reynandeocampo.githubbrowser.presentation.SharedViewModel
import com.reynandeocampo.githubbrowser.utils.ConnectivityHelper

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var sharedViewModel: SharedViewModel

    private var customTimer: CustomTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        binding = FragmentSearchBinding.inflate(layoutInflater)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
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

        binding.searchRepo.isIconified = false
        binding.searchRepo.queryHint = "Search places"
        binding.searchRepo.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query!!.isNotBlank()) {
                    searchRepo(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (customTimer != null) {
                    customTimer!!.cancel()
                    customTimer = null
                }

                if (newText!!.isNotBlank()) {
                    customTimer = CustomTimer(1000, 500) {
                        searchRepo(newText)
                    }
                    customTimer!!.start()
                } else {
//                    showDefaultView()
                }

                return false
            }
        })
    }

    private fun searchRepo(query: String) {
        if (ConnectivityHelper.isConnectedToNetwork(requireContext())) {
            sharedViewModel.searchGitHubRepo(query, 0, 0)
        } else {
//            showNoResult()
            showToastMessage("Please turn on your internet connection.")
        }
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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
