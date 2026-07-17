package com.careersandbox.app.data.remote

data class UserProfileResponse(
    val userId: String,
    val name: String,
    val email: String,
    val school: String,
    val department: String,
    val year: String,
    val interests: List<String>,
    val skillsHave: List<String>,
    val skillsWant: List<String>,
)