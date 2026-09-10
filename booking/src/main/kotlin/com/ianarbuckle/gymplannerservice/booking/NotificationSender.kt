package com.ianarbuckle.gymplannerservice.booking

/**
 * Port for sending a push notification. Implemented by an adapter in :app so
 * :booking does not depend on :push-notifications.
 */
interface NotificationSender {
    fun sendMessage(
        token: String,
        title: String,
        body: String,
    )
}
