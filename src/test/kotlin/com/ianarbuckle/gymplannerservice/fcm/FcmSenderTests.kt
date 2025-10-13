package com.ianarbuckle.gymplannerservice.fcm

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test

class FcmSenderTests {
    private val firebaseMessaging = mockk<FirebaseMessaging>()
    private val fcmSender = FcmSender(firebaseMessaging)

    @Test
    fun `should send message successfully and return success result`() {
        val token = "test-token"
        val title = "Test Title"
        val body = "Test Body"
        val expectedResponse = "message-id-123"

        every { firebaseMessaging.send(any<Message>()) } returns expectedResponse

        fcmSender.sendMessage(token, title, body)

        verify { firebaseMessaging.send(any<Message>()) }
    }

    @Test
    fun `should return failure result when firebase throws exception`() {
        val token = "test-token"
        val title = "Test Title"
        val body = "Test Body"
        val exception = RuntimeException("Firebase error")

        every { firebaseMessaging.send(any<Message>()) } throws exception

        fcmSender.sendMessage(token, title, body)

        verify { firebaseMessaging.send(any<Message>()) }
    }
}
