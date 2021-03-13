package com.reynandeocampo.githubbrowser.presentation.home.data

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.reynandeocampo.domain.models.GitRepo

class GitRepoDataSourceFactory(val application: Application) : DataSource.Factory<Int, GitRepo>() {

    val gitRepoDataSourceLiveData = MutableLiveData<GitRepoDataSource>()

    override fun create(): DataSource<Int, GitRepo> {
        val gitRepoDataSource = GitRepoDataSource(application)
        gitRepoDataSourceLiveData.postValue(gitRepoDataSource)
        return gitRepoDataSource
    }
}
