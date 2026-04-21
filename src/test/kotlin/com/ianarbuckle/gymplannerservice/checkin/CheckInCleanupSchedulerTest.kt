package com.ianarbuckle.gymplannerservice.checkin

import com.ianarbuckle.gymplannerservice.checkin.data.CheckInRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class CheckInCleanupSchedulerTest {

    private val checkInRepository = mockk<CheckInRepository>()
    private val scheduler = CheckInCleanupScheduler(checkInRepository)

    @Test
    fun `should delete all check-ins`() = runTest {
        coEvery { checkInRepository.deleteAll() } returns Unit

        scheduler.deleteAllCheckIns()

        coVerify { checkInRepository.deleteAll() }
    }
}
