package com.ianarbuckle.gymplannerservice.gateway

import com.ianarbuckle.gymplannerservice.booking.NotificationSender
import com.ianarbuckle.gymplannerservice.fcm.FcmSender
import org.springframework.stereotype.Component

/**
 * Adapter wiring booking's [NotificationSender] port to the push-notifications
 * feature's [FcmSender].
 */
@Component
class FcmNotificationSender(
    private val fcmSender: FcmSender,
) : NotificationSender {
    override fun sendMessage(
        token: String,
        title: String,
        body: String,
    ) = fcmSender.sendMessage(token, title, body)
}
