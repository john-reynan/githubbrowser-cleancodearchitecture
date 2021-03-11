package com.reynandeocampo.domain.usecases

import com.reynandeocampo.domain.repositories.GitRepository

class SearchGitRepo(private val gitRepository: GitRepository) {
    suspend operator fun invoke(query: String, perPage: Int, page: Int) =
        gitRepository.searchGitRepo(query, perPage, page)
}
