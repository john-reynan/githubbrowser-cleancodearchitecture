package com.reynandeocampo.data.repositories

import com.reynandeocampo.data.repositories.datasources.remote.GitHubDataSourceRemote
import com.reynandeocampo.domain.models.GitRepo
import com.reynandeocampo.domain.repositories.GitRepository

class GitRepositoryImpl(private val gitHubDataSourceRemote: GitHubDataSourceRemote) :
    GitRepository {

    override suspend fun searchGitRepo(query: String, perPage: Int, page: Int): List<GitRepo> {
        return gitHubDataSourceRemote.searchGitHubRepositories(query, perPage, page)
    }
}
