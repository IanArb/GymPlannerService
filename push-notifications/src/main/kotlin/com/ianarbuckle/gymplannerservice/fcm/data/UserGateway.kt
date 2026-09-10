package com.ianarbuckle.gymplannerservice.fcm.data

import com.ianarbuckle.gymplannerservice.authentication.User

/**
 * Port for reading and updating the user record that stores the push notification
 * token. Implemented by an adapter in :app so :push-notifications does not depend on
 * :authentication.
 */
interface UserGateway {
    suspend fun findById(userId: String): User?

    suspend fun save(user: User): User
}
