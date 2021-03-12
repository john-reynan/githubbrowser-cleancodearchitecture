package com.reynandeocampo.domain.models

data class GitRepo(
    var owner: Owner,
    var title: String,
    var description: String? = null,
    var url: String,
    var createdAt: String,
    var updatedAt: String,
    var id: Long = 0
)
