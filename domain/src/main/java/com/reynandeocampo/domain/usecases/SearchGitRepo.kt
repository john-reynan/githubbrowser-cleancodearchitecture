package com.reynandeocampo.domain.usecases

import com.reynandeocampo.domain.repositories.GitRepoRepository

class SearchGitRepo(private val gitRepoRepository: GitRepoRepository) {
    suspend operator fun invoke(query: String, perPage: Int, page: Int) =
        gitRepoRepository.searchGitRepo(query, perPage, page)
}
