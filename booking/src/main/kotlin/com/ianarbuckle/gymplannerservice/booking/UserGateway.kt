package com.ianarbuckle.gymplannerservice.booking

/**
 * Port for the user lookup booking's reminder scheduler needs (the push
 * notification token registered for a user). Implemented by an adapter in :app so
 * :booking does not depend on :authentication.
 */
interface UserGateway {
    suspend fun findPushNotificationToken(userId: String): String?
}
