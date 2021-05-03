package com.reynandeocampo.data.repositories.datasources

import com.reynandeocampo.domain.models.GitRepo
import com.reynandeocampo.domain.models.Resource

interface GitHubDataSourceRemote {
    suspend fun searchGitHubRepositories(
        query: String,
        perPage: Int,
        page: Int
    ): Resource<List<GitRepo>>
}
