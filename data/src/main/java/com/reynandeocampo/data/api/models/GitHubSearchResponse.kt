package com.reynandeocampo.data.api.models

import com.reynandeocampo.domain.models.GitRepo
import com.reynandeocampo.domain.models.Owner
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GitHubResponse(
    @Json(name = "total_count")
    var totalCount: Long,

    @Json(name = "incomplete_results")
    var incompleteResults: Boolean,

    @Json(name = "items")
    var items: List<ItemJson>
) {
    fun toGitRepoList(): List<GitRepo> {
        return items.map {
            GitRepo(
                Owner(it.owner.login, it.owner.avatarUrl, it.owner.url),
                it.fullName,
                it.description,
                it.htmlUrl,
                it.createdAt,
                it.updatedAt
            )
        }
    }
}

data class ItemJson(
    @Json(name = "id")
    var id: Long,

    @Json(name = "node_id")
    var nodeId: String,

    @Json(name = "name")
    var name: String,

    @Json(name = "full_name")
    var fullName: String,

    @Json(name = "private")
    var private: Boolean,

    @Json(name = "owner")
    var owner: OwnerJson,

    @Json(name = "html_url")
    var htmlUrl: String,

    @Json(name = "description")
    var description: String? = null,

    @Json(name = "created_at")
    var createdAt: String,

    @Json(name = "updated_at")
    var updatedAt: String
)

data class OwnerJson(
    @Json(name = "login")
    var login: String,

    @Json(name = "id")
    var id: Long,

    @Json(name = "node_id")
    var nodeId: String,

    @Json(name = "avatar_url")
    var avatarUrl: String,

    @Json(name = "url")
    var url: String,

    @Json(name = "html_url")
    var htmlUrl: String,

    @Json(name = "followers_url")
    var followersUrl: String,

    @Json(name = "following_url")
    var followingUrl: String,

    @Json(name = "gists_url")
    var gistsUrl: String,

    @Json(name = "starred_url")
    var starredUrl: String,

    @Json(name = "subscriptions_url")
    var subscriptionsUrl: String,

    @Json(name = "organizations_url")
    var organizationsUrl: String,

    @Json(name = "repos_url")
    var reposUrl: String,

    @Json(name = "events_url")
    var eventsUrl: String,

    @Json(name = "received_events_url")
    var receivedEventsUrl: String,

    @Json(name = "type")
    var type: String,

    @Json(name = "site_admin")
    var siteAdmin: Boolean
)
