package com.reynandeocampo.githubbrowser.presentation.home.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.reynandeocampo.data.UseCases
import com.reynandeocampo.data.api.Resource
import com.reynandeocampo.data.api.Status
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

    var networkStatus: MutableLiveData<Resource<Status>> = MutableLiveData()
    var viewStatus: MutableLiveData<Status> = MutableLiveData()

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, GitRepo>
    ) {
        retryQuery = { loadInitial(params, callback) }

        if (query.isEmpty()) {
            updateViewStatus(Status.IDLE)
        } else {
            updateNetworkStatus(Resource.loading(data = null))
            updateViewStatus(Status.LOADING)
            coroutineScope.launch {
                try {
                    val data = useCases.searchGitRepo(query, params.requestedLoadSize, 1)
                    updateNetworkStatus(Resource.success(data = Status.SUCCESS))
                    updateViewStatus(Status.SUCCESS)
                    callback.onResult(data, null, 2)
                } catch (e: HttpException) {
                    updateNetworkStatus(
                        Resource.error(
                            data = null,
                            message = e.message ?: "Unknown error occurred"
                        )
                    )
                    updateViewStatus(Status.ERROR)
                } catch (e: Exception) {
                    updateNetworkStatus(
                        Resource.error(
                            data = null,
                            message = e.message ?: "Unknown error occurred"
                        )
                    )
                    updateViewStatus(Status.ERROR)
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
                updateNetworkStatus(Resource.success(data = Status.SUCCESS))
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

    private fun updateNetworkStatus(resource: Resource<Status>) {
        this.networkStatus.postValue(resource)
    }

    private fun updateViewStatus(state: Status) {
        this.viewStatus.postValue(state)
    }

    fun retryFailedQuery() {
        val prevQuery = retryQuery
        retryQuery = null
        prevQuery?.invoke()
    }
}
