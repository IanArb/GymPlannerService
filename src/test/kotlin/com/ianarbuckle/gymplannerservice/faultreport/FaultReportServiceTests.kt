package com.ianarbuckle.gymplannerservice.faultreport

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.ianarbuckle.gymplannerservice.faultReporting.data.FaultReportRepository
import com.ianarbuckle.gymplannerservice.faultReporting.data.FaultReportService
import com.ianarbuckle.gymplannerservice.faultReporting.data.FaultReportServiceImpl
import com.ianarbuckle.gymplannerservice.faultReporting.exception.FaultReportAlreadyExistsException
import com.ianarbuckle.gymplannerservice.mocks.FaultReportDataProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.LocalDateTime
import kotlin.test.Test
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.assertThrows

class FaultReportServiceTests {
    private val faultReportRepository: FaultReportRepository = mockk()

    private val faultReportService: FaultReportService =
        FaultReportServiceImpl(faultReportRepository)

    @Test
    fun `should return all fault reports`() = runTest {
        val faultReports =
            listOf(
                FaultReportDataProvider.createFaultReport(),
                FaultReportDataProvider.createFaultReport(
                    id = "2",
                    description = "Faulty machine 2",
                    machineNumber = 456,
                    photoUri = "https://www.gym-b.com",
                    date = LocalDateTime.of(2021, 10, 10, 10, 10),
                ),
            )
        coEvery { faultReportRepository.findAll() } returns flowOf(*faultReports.toTypedArray())

        faultReportService.reports().test {
            assertThat(awaitItem()).isEqualTo(faultReports.first())
            assertThat(awaitItem()).isEqualTo(faultReports.last())
            awaitComplete()
        }

        coVerify { faultReportRepository.findAll() }
    }

    @Test
    fun `should save fault report`() = runTest {
        val faultReport = FaultReportDataProvider.createFaultReport()
        coEvery { faultReportRepository.findAll() } returns flowOf()
        coEvery { faultReportRepository.save(faultReport) } returns faultReport

        val result = faultReportService.save(faultReport)

        assertThat(faultReport).isEqualTo(result)

        coVerify { faultReportRepository.save(faultReport) }
    }

    @Test
    fun `test save report should throw exception if already exists`() = runTest {
        val faultReport = FaultReportDataProvider.createFaultReport()
        coEvery { faultReportRepository.findAll() } returns flowOf(faultReport)

        val result =
            assertThrows<FaultReportAlreadyExistsException> { faultReportService.save(faultReport) }

        assertThat(result).isInstanceOf(FaultReportAlreadyExistsException::class.java)

        coVerify(exactly = 0) { faultReportRepository.save(faultReport) }
    }

    @Test
    fun `should delete fault report by id`() = runTest {
        val faultReportId = "1"
        coEvery { faultReportRepository.deleteById(faultReportId) } returns Unit

        faultReportService.deleteReportById(faultReportId)

        coVerify { faultReportRepository.deleteById(faultReportId) }
    }
}
