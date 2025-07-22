package com.ianarbuckle.gymplannerservice.userProfile.data

import com.google.common.truth.Truth.assertThat
import com.ianarbuckle.gymplannerservice.booking.exception.UserNotFoundException
import com.ianarbuckle.gymplannerservice.mocks.UserProfileDataProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UserProfileServiceTests {
    private val userProfileRepository = mockk<UserProfileRepository>()
    private val userProfileService: UserProfileService =
        UserProfileServiceImpl(userProfileRepository)

    @Test
    fun `userProfile should return UserProfile when found`() = runTest {
        val userId = "123456"
        val userProfile = UserProfileDataProvider.createUserProfile(userId = userId)
        coEvery { userProfileRepository.findByUserId(userId) } returns userProfile

        val result = userProfileService.userProfile(userId)

        assertThat(result).isEqualTo(userProfile)
    }

    @Test
    fun `userProfile should throw UserNotFoundException when not found`() = runTest {
        val userId = "nonexistentUserId"
        coEvery { userProfileRepository.findByUserId(userId) } returns null

        val exception =
            assertThrows<UserNotFoundException> { userProfileService.userProfile(userId) }

        assertThat(exception).isInstanceOf(UserNotFoundException::class.java)
        assertThat(exception).hasMessageThat().contains("User not found")
    }

    @Test
    fun `deleteUserProfile should delete UserProfile`() = runTest {
        val userId = "123456"
        coEvery { userProfileRepository.deleteById(userId) } returns Unit

        userProfileService.deleteUserProfile(userId)

        coVerify { userProfileRepository.deleteById(userId) }
    }

    @Test
    fun `updateUserProfile should update UserProfile when exists`() = runTest {
        val userProfile = UserProfileDataProvider.createUserProfile()
        coEvery { userProfileRepository.existsByUserId(userProfile.userId) } returns true
        coEvery { userProfileRepository.save(userProfile) } returns userProfile

        userProfileService.updateUserProfile(userProfile)

        coVerify { userProfileRepository.save(userProfile) }
    }

    @Test
    fun `updateUserProfile should throw UserNotFoundException when not exists`() = runTest {
        val userProfile = UserProfileDataProvider.createUserProfile()
        coEvery { userProfileRepository.existsByUserId(userProfile.userId) } returns false

        val exception =
            assertThrows<UserNotFoundException> {
                userProfileService.updateUserProfile(userProfile)
            }

        assertThat(exception).isInstanceOf(UserNotFoundException::class.java)
        assertThat(exception).hasMessageThat().contains("User not found")
    }
}
