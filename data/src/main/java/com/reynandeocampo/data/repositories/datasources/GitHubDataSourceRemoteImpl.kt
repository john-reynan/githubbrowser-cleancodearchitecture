package com.reynandeocampo.data.repositories.datasources

import com.reynandeocampo.data.api.endpoints.GitHubApi
import com.reynandeocampo.domain.models.GitRepo

class GitHubDataSourceRemoteImpl(private val gitHubApi: GitHubApi) : GitHubDataSourceRemote {

    override suspend fun searchGitHubRepositories(
        query: String,
        perPage: Int,
        page: Int
    ): List<GitRepo> {
        return gitHubApi.searchRepositories(
            query,
            perPage,
            page
        ).toGitRepoList()
    }
}
