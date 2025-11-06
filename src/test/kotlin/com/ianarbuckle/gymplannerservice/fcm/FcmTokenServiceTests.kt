package com.ianarbuckle.gymplannerservice.fcm

import com.ianarbuckle.gymplannerservice.authentication.data.model.User
import com.ianarbuckle.gymplannerservice.authentication.data.repository.UserRepository
import com.ianarbuckle.gymplannerservice.fcm.data.FcmTokenServiceImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.test.Test
import kotlinx.coroutines.test.runTest

class FcmTokenServiceTests {
    private val userRepository = mockk<UserRepository>()

    private val fcmTokenService = FcmTokenServiceImpl(userRepository)

    @Test
    fun `should register token when user exists and has no existing token`() = runTest {
        val userId = "user123"
        val token = "fcm-token-123"
        val user =
            mockk<User> {
                coEvery { pushNotificationToken } returns null
                coEvery { copy(pushNotificationToken = token) } returns mockk()
            }
        val updatedUser = mockk<User>()

        coEvery { userRepository.findById(userId) } returns user
        coEvery { user.copy(pushNotificationToken = token) } returns updatedUser
        coEvery { updatedUser.pushNotificationToken } returns token
        coEvery { userRepository.save(updatedUser) } returns updatedUser

        fcmTokenService.registerToken(userId, token)

        coVerify { userRepository.findById(userId) }
        coVerify { userRepository.save(updatedUser) }
    }

    @Test
    fun `should not register token when user already has existing token`() = runTest {
        val userId = "user123"
        val token = "fcm-token-123"
        val user = mockk<User> { coEvery { pushNotificationToken } returns "existing-token" }

        coEvery { userRepository.findById(userId) } returns user

        fcmTokenService.registerToken(userId, token)

        coVerify { userRepository.findById(userId) }
        coVerify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `should not register token when user does not exist`() = runTest {
        val userId = "nonexistent-user"
        val token = "fcm-token-123"

        coEvery { userRepository.findById(userId) } returns null

        fcmTokenService.registerToken(userId, token)

        coVerify { userRepository.findById(userId) }
        coVerify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `should delete token when user exists and has token`() = runTest {
        val userId = "user123"
        val user =
            mockk<User> {
                coEvery { pushNotificationToken } returns "existing-token"
                coEvery { copy(pushNotificationToken = null) } returns mockk()
            }
        val updatedUser = mockk<User>()

        coEvery { userRepository.findById(userId) } returns user
        coEvery { user.copy(pushNotificationToken = null) } returns updatedUser
        coEvery { userRepository.save(updatedUser) } returns updatedUser

        fcmTokenService.deleteToken(userId)

        coVerify { userRepository.findById(userId) }
        coVerify { userRepository.save(updatedUser) }
    }

    @Test
    fun `should not delete token when user has no token`() = runTest {
        val userId = "user123"
        val user = mockk<User> { coEvery { pushNotificationToken } returns null }

        coEvery { userRepository.findById(userId) } returns user

        fcmTokenService.deleteToken(userId)

        coVerify { userRepository.findById(userId) }
        coVerify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `should not delete token when user does not exist`() = runTest {
        val userId = "nonexistent-user"

        coEvery { userRepository.findById(userId) } returns null

        fcmTokenService.deleteToken(userId)

        coVerify { userRepository.findById(userId) }
        coVerify(exactly = 0) { userRepository.save(any()) }
    }
}
