package com.reynandeocampo.data.repositories.datasources

import com.reynandeocampo.data.api.endpoints.GitHubApi
import com.reynandeocampo.domain.models.GitRepo
import com.reynandeocampo.domain.models.Resource
import retrofit2.HttpException

class GitHubDataSourceRemoteImpl(private val gitHubApi: GitHubApi) : GitHubDataSourceRemote {

    override suspend fun searchGitHubRepositories(
        query: String,
        perPage: Int,
        page: Int
    ): Resource<List<GitRepo>> {
        return try {
            val result = gitHubApi.searchRepositories(
                query,
                perPage,
                page
            ).toGitRepoList()
            Resource.success(data = result)
        } catch (e: HttpException) {
            Resource.error(data = null, message = e.message ?: "Unknown error occurred")
        } catch (e: Exception) {
            Resource.error(data = null, message = e.message ?: "Unknown error occurred")
        }
    }
}
