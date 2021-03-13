package com.reynandeocampo.githubbrowser.presentation.home.data

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.reynandeocampo.data.UseCases
import com.reynandeocampo.domain.models.GitRepo
import com.reynandeocampo.githubbrowser.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

class GitRepoDataSource(application: Application) : PageKeyedDataSource<Int, GitRepo>() {

    @Inject
    lateinit var useCases: UseCases

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    var state: MutableLiveData<State> = MutableLiveData()

    init {
        (application as App).mainComponent.inject(this)
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, GitRepo>
    ) {
        updateState(State.LOADING)
        coroutineScope.launch {
            try {
                val data: List<GitRepo> = useCases.searchGitRepo("u", params.requestedLoadSize, 1)
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
        coroutineScope.launch {
            try {
                val data: List<GitRepo> = useCases.searchGitRepo("u", params.requestedLoadSize, params.key)
                updateState(State.DONE)
                callback.onResult(data, params.key + 1)
            } catch (e: HttpException) {
                updateState(State.ERROR)
            } catch (e: Exception) {
                updateState(State.ERROR)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, GitRepo>) {
    }

    private fun searchGitHubRepo(query: String) {

    }

    private fun updateState(state: State) {
        this.state.postValue(state)
    }
}
