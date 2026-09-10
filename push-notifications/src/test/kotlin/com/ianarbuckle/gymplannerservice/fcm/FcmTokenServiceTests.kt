package com.ianarbuckle.gymplannerservice.fcm

import com.ianarbuckle.gymplannerservice.authentication.User
import com.ianarbuckle.gymplannerservice.fcm.data.FcmTokenServiceImpl
import com.ianarbuckle.gymplannerservice.fcm.data.UserGateway
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class FcmTokenServiceTests {
    private val userGateway = mockk<UserGateway>()

    private val fcmTokenService = FcmTokenServiceImpl(userGateway)

    @Test
    fun `should register token when user exists and has no existing token`() =
        runTest {
            val userId = "user123"
            val token = "fcm-token-123"
            val user =
                mockk<User> {
                    coEvery { pushNotificationToken } returns null
                    coEvery { copy(pushNotificationToken = token) } returns mockk()
                }
            val updatedUser = mockk<User>()

            coEvery { userGateway.findById(userId) } returns user
            coEvery { user.copy(pushNotificationToken = token) } returns updatedUser
            coEvery { updatedUser.pushNotificationToken } returns token
            coEvery { userGateway.save(updatedUser) } returns updatedUser

            fcmTokenService.registerToken(userId, token)

            coVerify { userGateway.findById(userId) }
            coVerify { userGateway.save(updatedUser) }
        }

    @Test
    fun `should not register token when user already has existing token`() =
        runTest {
            val userId = "user123"
            val token = "fcm-token-123"
            val user = mockk<User> { coEvery { pushNotificationToken } returns "existing-token" }

            coEvery { userGateway.findById(userId) } returns user

            fcmTokenService.registerToken(userId, token)

            coVerify { userGateway.findById(userId) }
            coVerify(exactly = 0) { userGateway.save(any()) }
        }

    @Test
    fun `should not register token when user does not exist`() =
        runTest {
            val userId = "nonexistent-user"
            val token = "fcm-token-123"

            coEvery { userGateway.findById(userId) } returns null

            fcmTokenService.registerToken(userId, token)

            coVerify { userGateway.findById(userId) }
            coVerify(exactly = 0) { userGateway.save(any()) }
        }

    @Test
    fun `should delete token when user exists and has token`() =
        runTest {
            val userId = "user123"
            val user =
                mockk<User> {
                    coEvery { pushNotificationToken } returns "existing-token"
                    coEvery { copy(pushNotificationToken = null) } returns mockk()
                }
            val updatedUser = mockk<User>()

            coEvery { userGateway.findById(userId) } returns user
            coEvery { user.copy(pushNotificationToken = null) } returns updatedUser
            coEvery { userGateway.save(updatedUser) } returns updatedUser

            fcmTokenService.deleteToken(userId)

            coVerify { userGateway.findById(userId) }
            coVerify { userGateway.save(updatedUser) }
        }

    @Test
    fun `should not delete token when user has no token`() =
        runTest {
            val userId = "user123"
            val user = mockk<User> { coEvery { pushNotificationToken } returns null }

            coEvery { userGateway.findById(userId) } returns user

            fcmTokenService.deleteToken(userId)

            coVerify { userGateway.findById(userId) }
            coVerify(exactly = 0) { userGateway.save(any()) }
        }

    @Test
    fun `should not delete token when user does not exist`() =
        runTest {
            val userId = "nonexistent-user"

            coEvery { userGateway.findById(userId) } returns null

            fcmTokenService.deleteToken(userId)

            coVerify { userGateway.findById(userId) }
            coVerify(exactly = 0) { userGateway.save(any()) }
        }
}
