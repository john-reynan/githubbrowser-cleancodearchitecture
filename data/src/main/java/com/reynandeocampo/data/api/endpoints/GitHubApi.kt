package com.reynandeocampo.data.api.endpoints

import com.reynandeocampo.data.GIT_HUB_PATH_SEARCH_REPOSITORIES
import com.reynandeocampo.data.api.models.GitHubResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubApi {

    @GET(GIT_HUB_PATH_SEARCH_REPOSITORIES)
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ): GitHubResponse
}
