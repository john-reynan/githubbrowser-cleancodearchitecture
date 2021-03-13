package com.reynandeocampo.githubbrowser.presentation.pagination

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.reynandeocampo.data.UseCases
import com.reynandeocampo.data.api.Resource
import com.reynandeocampo.domain.models.GitRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.HttpException

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
                try {
                    val data = useCases.searchGitRepo(query, params.requestedLoadSize, 1)
                    updateNetworkStatus(Resource.success(data = data))
                    updateViewStatus(Resource.success(data = data))
                    callback.onResult(data, null, 2)
                } catch (e: HttpException) {
                    updateNetworkStatus(
                        Resource.error(
                            data = null,
                            message = e.message ?: "Unknown error occurred"
                        )
                    )
                    updateViewStatus(
                        Resource.error(
                            data = null,
                            message = e.message ?: "Unknown error occurred"
                        )
                    )
                } catch (e: Exception) {
                    updateNetworkStatus(
                        Resource.error(
                            data = null,
                            message = e.message ?: "Unknown error occurred"
                        )
                    )
                    updateViewStatus(
                        Resource.error(
                            data = null,
                            message = e.message ?: "Unknown error occurred"
                        )
                    )
                }
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, GitRepo>) {
        retryQuery = { loadAfter(params, callback) }

        updateNetworkStatus(Resource.loading(data = null))
        coroutineScope.launch {
            try {
                val data = useCases.searchGitRepo(query, params.requestedLoadSize, params.key)
                updateNetworkStatus(Resource.success(data = data))
                callback.onResult(data, params.key + 1)
            } catch (e: HttpException) {
                updateNetworkStatus(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Unknown error occurred"
                    )
                )
            } catch (e: Exception) {
                updateNetworkStatus(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Unknown error occurred"
                    )
                )
            }
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
