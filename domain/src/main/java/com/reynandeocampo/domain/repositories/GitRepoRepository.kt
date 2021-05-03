package com.reynandeocampo.domain.repositories

import com.reynandeocampo.domain.models.GitRepo
import com.reynandeocampo.domain.models.Resource

interface GitRepoRepository {
    suspend fun searchGitRepo(query: String, perPage: Int, page: Int): Resource<List<GitRepo>>
}
