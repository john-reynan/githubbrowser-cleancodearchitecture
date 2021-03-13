package com.reynandeocampo.githubbrowser.presentation.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.switchMap
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.reynandeocampo.data.UseCases
import com.reynandeocampo.data.api.Status
import com.reynandeocampo.githubbrowser.App
import com.reynandeocampo.githubbrowser.presentation.home.data.GitRepoDataSourceFactory
import javax.inject.Inject

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var useCases: UseCases

    init {
        (application as App).mainComponent.inject(this)
    }

    private val pageSize = 15
    private val dataSource = GitRepoDataSourceFactory(useCases)

    val gitHubRepoList = LivePagedListBuilder(dataSource, pagedListConfig()).build()
    val networkStatus = switchMap(dataSource.gitRepoDataSourceLiveData) { it.networkStatus }
    val viewStatus = switchMap(dataSource.gitRepoDataSourceLiveData) { it.viewStatus }

    fun searchRepo(query: String) {
        if (dataSource.getQuery() == query) return
        dataSource.updateQuery(query)
    }

    fun listIsEmpty(): Boolean {
        return gitHubRepoList.value?.isEmpty() ?: true
    }

    fun retry() {
        dataSource.getDataSource()?.retryFailedQuery()
    }

    fun updateViewStatus(status: Status) {
        dataSource.updateViewStatus(status)
        dataSource.updateQuery("")
    }

    private fun pagedListConfig() = PagedList.Config.Builder()
        .setPageSize(pageSize)
        .setInitialLoadSizeHint(pageSize)
        .setEnablePlaceholders(false)
        .build()
}
