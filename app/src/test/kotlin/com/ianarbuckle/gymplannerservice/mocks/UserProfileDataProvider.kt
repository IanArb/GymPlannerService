package com.ianarbuckle.gymplannerservice.mocks

import com.ianarbuckle.gymplannerservice.authentication.data.model.UserProfile

object UserProfileDataProvider {
    fun createUserProfile(
        userId: String = "12345",
        username: String = "username",
        firstName: String = "firstName",
        surname: String = "surname",
        email: String = "email",
    ): UserProfile =
        UserProfile(
            userId = userId,
            username = username,
            firstName = firstName,
            surname = surname,
            email = email,
        )
}
