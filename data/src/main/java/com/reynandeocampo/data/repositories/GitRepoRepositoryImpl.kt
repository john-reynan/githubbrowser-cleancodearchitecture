package com.reynandeocampo.data.repositories

import com.reynandeocampo.data.repositories.datasources.GitHubDataSourceRemote
import com.reynandeocampo.domain.models.GitRepo
import com.reynandeocampo.domain.models.Resource
import com.reynandeocampo.domain.repositories.GitRepoRepository

class GitRepoRepositoryImpl(private val gitHubDataSourceRemote: GitHubDataSourceRemote) :
    GitRepoRepository {

    override suspend fun searchGitRepo(
        query: String,
        perPage: Int,
        page: Int
    ): Resource<List<GitRepo>> {
        return gitHubDataSourceRemote.searchGitHubRepositories(query, perPage, page)
    }
}
