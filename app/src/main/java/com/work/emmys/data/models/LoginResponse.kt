package com.work.emmys.data.models

/**
 * Created by PaL on 22,January,2023
 */
data class LoginResponse(
    val action: String,
    val message: String,
    val response: List<Response>,
    val status: Int
)

data class Response(
    val token: Token,
    val user: User
)

data class Token(
    val access: String,
    val refresh: String
)

data class User(
    val active: Boolean,
    val fullName: String,
    val id: Int,
    val role: String,
    val userName: String
)