package com.reynandeocampo.data.repositories.datasources

import com.reynandeocampo.domain.models.GitRepo

interface GitHubDataSourceRemote {
    suspend fun searchGitHubRepositories(query: String, perPage: Int, page: Int): List<GitRepo>
}
