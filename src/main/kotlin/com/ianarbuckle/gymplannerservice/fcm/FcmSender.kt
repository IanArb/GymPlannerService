package com.ianarbuckle.gymplannerservice.fcm

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class FcmSender(
    private val firebaseMessaging: FirebaseMessaging = FirebaseMessaging.getInstance()
) {

    private val logger = LoggerFactory.getLogger(FcmSender::class.java)

    fun sendMessage(token: String, title: String, body: String) {
        val message =
            Message.builder()
                .setToken(token)
                .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                .build()

        try {
            val response = firebaseMessaging.send(message)
            logger.info("Successfully sent message: $response")
        } catch (e: Exception) {
            logger.error("Error sending message: $e")
        }
    }
}
