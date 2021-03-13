package com.reynandeocampo.githubbrowser.presentation.home.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.reynandeocampo.data.UseCases
import com.reynandeocampo.domain.models.GitRepo

class GitRepoDataSourceFactory(private val useCases: UseCases, private var query: String = "") :
    DataSource.Factory<Int, GitRepo>() {

    val gitRepoDataSourceLiveData = MutableLiveData<GitRepoDataSource>()

    override fun create(): DataSource<Int, GitRepo> {
        val gitRepoDataSource = GitRepoDataSource(useCases, query)
        gitRepoDataSourceLiveData.postValue(gitRepoDataSource)
        return gitRepoDataSource
    }

    fun getQuery(): String {
        return query
    }

    fun getDataSource(): GitRepoDataSource? {
        return gitRepoDataSourceLiveData.value
    }

    fun updateQuery(query: String) {
        this.query = query
        getDataSource()?.invalidate()
    }
}
