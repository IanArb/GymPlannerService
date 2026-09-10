package com.ianarbuckle.gymplannerservice.fitnessclass

/**
 * Port for sending a push notification. Implemented by an adapter in :app so
 * :fitness-class does not depend on :push-notifications.
 */
interface NotificationSender {
    fun sendMessage(
        token: String,
        title: String,
        body: String,
    )
}
