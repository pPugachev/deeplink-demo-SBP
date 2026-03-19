package ru.demo.raiffeisen.test

data class MembersResponse(
    val version: String?,
    val platform: String?,
    val type: String?,
    val members: List<Member>
)

data class Member(
    val id: String,
    val name: String,
    val logo: String,
    val url: String
)