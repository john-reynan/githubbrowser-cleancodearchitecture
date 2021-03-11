package com.reynandeocampo.githubbrowser.di.modules

import com.reynandeocampo.data.api.endpoints.GitHubApi
import com.reynandeocampo.data.repositories.GitRepositoryImpl
import com.reynandeocampo.data.repositories.datasources.remote.GitHubDataSourceRemoteImpl
import com.reynandeocampo.domain.repositories.GitRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun providesGitRepository(gitHubApi: GitHubApi): GitRepository {
        val gitHubDataSourceRemoteImpl = GitHubDataSourceRemoteImpl(gitHubApi)
        return GitRepositoryImpl(gitHubDataSourceRemoteImpl)
    }
}
