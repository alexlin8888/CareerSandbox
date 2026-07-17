package com.careersandbox.app.data.remote

// Request body for POST /auth/register
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val school: String = "",
    val department: String = "",
    val year: String = "",
    val interests: List<String> = emptyList(),
    val skillsHave: List<String> = emptyList(),
    val skillsWant: List<String> = emptyList(),
)

// Response body from the backend (201)
data class RegisterResponse(
    val token: String? = null,
    val userId: String,
    val email: String,
    val name: String
)

data class LoginRequest(
    val email: String,
    val password: String,
)

data class LoginResponse(
    val token: String,
    val userId: String,
    val email: String,
    val name: String,
)