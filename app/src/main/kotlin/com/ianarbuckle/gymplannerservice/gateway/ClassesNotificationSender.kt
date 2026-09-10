package com.ianarbuckle.gymplannerservice.gateway

import com.ianarbuckle.gymplannerservice.fcm.FcmSender
import com.ianarbuckle.gymplannerservice.fitnessclass.NotificationSender
import org.springframework.stereotype.Component

/**
 * Adapter wiring fitness-class's [NotificationSender] port to the push-notifications
 * feature's [FcmSender]. Lives in :app (the composition root) so neither
 * :fitness-class nor :push-notifications depends on the other.
 */
@Component
class ClassesNotificationSender(
    private val fcmSender: FcmSender,
) : NotificationSender {
    override fun sendMessage(
        token: String,
        title: String,
        body: String,
    ) = fcmSender.sendMessage(token, title, body)
}
