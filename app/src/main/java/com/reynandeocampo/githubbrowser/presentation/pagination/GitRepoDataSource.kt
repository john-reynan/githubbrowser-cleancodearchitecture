package com.reynandeocampo.githubbrowser.presentation.pagination

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.reynandeocampo.domain.UseCases
import com.reynandeocampo.domain.models.GitRepo
import com.reynandeocampo.domain.models.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class GitRepoDataSource(
    private val useCases: UseCases,
    private val query: String
) : PageKeyedDataSource<Int, GitRepo>() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var retryQuery: (() -> Any)? = null

    var networkStatus: MutableLiveData<Resource<List<GitRepo>>> = MutableLiveData()
    var viewStatus: MutableLiveData<Resource<List<GitRepo>>> = MutableLiveData()

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, GitRepo>
    ) {
        retryQuery = { loadInitial(params, callback) }

        if (query.isEmpty()) {
            updateViewStatus(Resource.idle(data = null))
        } else {
            updateNetworkStatus(Resource.loading(data = null))
            updateViewStatus(Resource.loading(data = null))
            coroutineScope.launch {
                val data = useCases.searchGitRepo(query, params.requestedLoadSize, 1)
                updateNetworkStatus(data)
                updateViewStatus(data)
                data.data?.let { callback.onResult(it, null, 2) }
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, GitRepo>) {
        retryQuery = { loadAfter(params, callback) }

        updateNetworkStatus(Resource.loading(data = null))
        coroutineScope.launch {
            val data = useCases.searchGitRepo(query, params.requestedLoadSize, params.key)
            updateNetworkStatus(data)
            data.data?.let { callback.onResult(it, params.key + 1) }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, GitRepo>) {}

    override fun invalidate() {
        super.invalidate()
        coroutineScope.cancel()
    }

    private fun updateNetworkStatus(resource: Resource<List<GitRepo>>) {
        this.networkStatus.postValue(resource)
    }

    private fun updateViewStatus(resource: Resource<List<GitRepo>>) {
        this.viewStatus.postValue(resource)
    }

    fun retryFailedQuery() {
        val prevQuery = retryQuery
        retryQuery = null
        prevQuery?.invoke()
    }
}
