package com.ianarbuckle.gymplannerservice.mocks

import com.ianarbuckle.gymplannerservice.authentication.ERole
import com.ianarbuckle.gymplannerservice.authentication.User

object UserDataProvider {
    fun createUser(
        id: String = "123456",
        username: String = "testuser",
        password: String = "encodedPassword",
        email: String = "user@mail.com",
        roles: Set<ERole> = setOf(ERole.ROLE_USER),
        pushNotificationToken: String = "pushToken",
    ): User =
        User(
            id = id,
            username = username,
            password = password,
            email = email,
            roles = roles,
            pushNotificationToken = pushNotificationToken,
        )
}
