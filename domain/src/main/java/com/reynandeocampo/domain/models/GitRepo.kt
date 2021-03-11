package com.reynandeocampo.domain.models

data class GitRepo(
    var owner: Owner,
    var url: String,
    var description: String? = null,
    var id: Long = 0
)
