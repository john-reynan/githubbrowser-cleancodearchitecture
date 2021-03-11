package com.reynandeocampo.githubbrowser.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.reynandeocampo.data.UseCases
import com.reynandeocampo.data.api.Resource
import com.reynandeocampo.domain.models.GitRepo
import com.reynandeocampo.githubbrowser.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var useCases: UseCases

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    val gitHubRepoList = MutableLiveData<Resource<List<GitRepo>>>()

    init {
        (application as App).mainComponent.inject(this)
        setObservablesToPending()
    }

    fun searchGitHubRepo(query: String, perPage: Int, page: Int) {
        coroutineScope.launch {
            gitHubRepoList.postValue(Resource.loading(data = null))

            try {
                val data: List<GitRepo> = useCases.searchGitRepo(query, perPage, page)

                gitHubRepoList.postValue(Resource.success(data = data))
            } catch (e: HttpException) {
                gitHubRepoList.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Unknown error occurred"
                    )
                )
            } catch (e: Exception) {
                gitHubRepoList.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Unknown error occurred"
                    )
                )
            }
        }
    }

    fun setObservablesToPending() {
        gitHubRepoList.postValue(Resource.pending(data = null))
    }
}
