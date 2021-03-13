package com.reynandeocampo.githubbrowser.presentation.home.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.reynandeocampo.data.UseCases
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

    var state: MutableLiveData<State> = MutableLiveData()

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, GitRepo>
    ) {
        updateState(State.LOADING)
        retryQuery = { loadInitial(params, callback) }
        coroutineScope.launch {
            try {
                val data: List<GitRepo> = useCases.searchGitRepo(query, params.requestedLoadSize, 1)
                updateState(State.DONE)
                callback.onResult(data, null, 2)
            } catch (e: HttpException) {
                updateState(State.ERROR)
            } catch (e: Exception) {
                updateState(State.ERROR)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, GitRepo>) {
        updateState(State.LOADING)
        retryQuery = { loadAfter(params, callback) }
        coroutineScope.launch {
            try {
                val data: List<GitRepo> =
                    useCases.searchGitRepo(query, params.requestedLoadSize, params.key)
                updateState(State.DONE)
                callback.onResult(data, params.key + 1)
            } catch (e: HttpException) {
                updateState(State.ERROR)
            } catch (e: Exception) {
                updateState(State.ERROR)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, GitRepo>) { }

    override fun invalidate() {
        super.invalidate()
        coroutineScope.cancel()
    }

    private fun updateState(state: State) {
        this.state.postValue(state)
    }

    fun retryFailedQuery() {
        val prevQuery = retryQuery
        retryQuery = null
        prevQuery?.invoke()
    }
}
