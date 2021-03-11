package com.reynandeocampo.domain.repositories

import com.reynandeocampo.domain.models.GitRepo

interface GitRepository {
    suspend fun searchGitRepo(query: String, perPage: Int, page: Int): List<GitRepo>
}
