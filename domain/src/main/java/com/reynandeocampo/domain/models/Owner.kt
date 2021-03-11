package com.reynandeocampo.domain.models

data class Owner(
    var userName: String,
    var avatar: String,
    var gitUrl: String,
    var id: Long = 0
)
