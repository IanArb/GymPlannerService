package com.ianarbuckle.gymplannerservice.faultreport

import com.ianarbuckle.gymplannerservice.faultReporting.FaultReportController
import com.ianarbuckle.gymplannerservice.faultReporting.data.FaultReport
import com.ianarbuckle.gymplannerservice.faultReporting.data.FaultReportService
import com.ianarbuckle.gymplannerservice.mocks.FaultReportDataProvider
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient

@ExtendWith(SpringExtension::class)
@WebFluxTest(
    controllers = [FaultReportController::class],
    excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class],
)
@TestPropertySource("classpath:application-test.properties")
@ActiveProfiles("test")
class FaultReportControllerTests {
    @Autowired private lateinit var webTestClient: WebTestClient

    @MockBean private lateinit var faultReportService: FaultReportService

    @Test
    fun `should return all fault reports`() = runTest {
        val faultReports =
            listOf(
                FaultReportDataProvider.createFaultReport(),
                FaultReportDataProvider.createFaultReport(
                    id = "2",
                    description = "Faulty machine 2"
                ),
            )
        `when`(faultReportService.reports()).thenReturn(flowOf(*faultReports.toTypedArray()))

        webTestClient
            .get()
            .uri("/api/v1/fault")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
            .expectBodyList(FaultReport::class.java)
            .hasSize(2)
            .contains(faultReports[0], faultReports[1])
    }

    @Test
    fun `should create fault report`() = runTest {
        val faultReport = FaultReportDataProvider.createFaultReport()
        `when`(faultReportService.save(faultReport)).thenReturn(faultReport)

        webTestClient
            .post()
            .uri("/api/v1/fault")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(faultReport)
            .exchange()
            .expectStatus()
            .isCreated
            .expectBody(FaultReport::class.java)
            .isEqualTo(faultReport)
    }

    @Test
    fun `should delete fault report by id`() = runTest {
        `when`(faultReportService.deleteReportById("1")).thenReturn(Unit)

        webTestClient.delete().uri("/api/v1/fault/1").exchange().expectStatus().isOk
    }
}
