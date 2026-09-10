package com.ianarbuckle.gymplannerservice.fitnessclass

import kotlinx.coroutines.flow.Flow

/**
 * Port for the user lookup the class-reminder scheduler needs (the push
 * notification tokens of every user that has one registered). Implemented by an
 * adapter in :app so :fitness-class does not depend on :authentication.
 */
interface UserGateway {
    fun findAllPushNotificationTokens(): Flow<String>
}
