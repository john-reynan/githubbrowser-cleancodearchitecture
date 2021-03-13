package com.reynandeocampo.githubbrowser.presentation.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.reynandeocampo.data.UseCases
import com.reynandeocampo.data.api.Resource
import com.reynandeocampo.domain.models.GitRepo
import com.reynandeocampo.githubbrowser.App
import com.reynandeocampo.githubbrowser.presentation.home.data.GitRepoDataSource
import com.reynandeocampo.githubbrowser.presentation.home.data.GitRepoDataSourceFactory
import com.reynandeocampo.githubbrowser.presentation.home.data.State
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var useCases: UseCases

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    val pageSize = 15

    private val gitRepoDataSourceFactory: GitRepoDataSourceFactory

    val gitHubRepoList: LiveData<PagedList<GitRepo>>

    init {
        (application as App).mainComponent.inject(this)
        gitRepoDataSourceFactory = GitRepoDataSourceFactory(application)
        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setInitialLoadSizeHint(pageSize)
            .setEnablePlaceholders(false)
            .build()
        gitHubRepoList = LivePagedListBuilder(gitRepoDataSourceFactory, config).build()
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun getState(): LiveData<State> = Transformations.switchMap(
        gitRepoDataSourceFactory.gitRepoDataSourceLiveData,
        GitRepoDataSource::state
    )

    fun listIsEmpty(): Boolean {
        return gitHubRepoList.value?.isEmpty() ?: true
    }
}
