package com.ianarbuckle.gymplannerservice.fitnessclass

import com.ianarbuckle.gymplannerservice.fitnessclass.data.FitnessClass
import com.ianarbuckle.gymplannerservice.fitnessclass.data.FitnessClassesService
import com.ianarbuckle.gymplannerservice.mocks.FitnessClassDataProvider
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
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
import kotlin.test.Test

@ExtendWith(SpringExtension::class)
@WebFluxTest(
    controllers = [ClassesController::class],
    excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class],
)
@TestPropertySource("classpath:application-test.properties")
@ActiveProfiles("test")
class ClassesControllerTests {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var fitnessClassesService: FitnessClassesService

    @Test
    fun `should return all fitness classes by day of week`() =
        runTest {
            val fitnessClass = FitnessClassDataProvider.createClass()

            // Given
            `when`(fitnessClassesService.fitnessClassesByDayOfWeek("Monday")).thenReturn(flowOf(fitnessClass))

            // When & Then
            webTestClient
                .get()
                .uri("/api/v1/fitness_class?dayOfWeek=Monday")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk
                .expectBodyList(FitnessClass::class.java)
                .contains(fitnessClass)
        }

    @Test
    fun `should save fitness class`() =
        runTest {
            val fitnessClass = FitnessClassDataProvider.createClass()

            // Given
            `when`(fitnessClassesService.createFitnessClass(fitnessClass)).thenReturn(fitnessClass)

            val fitnessClassJson =
                """
             {
                    "id": "${fitnessClass.id}",
                    "dayOfWeek": "${fitnessClass.dayOfWeek}",
                    "startTime": "${fitnessClass.startTime}",
                    "endTime": "${fitnessClass.endTime}",
                    "duration": {
                        "value": ${fitnessClass.duration.value},
                        "unit": "${fitnessClass.duration.unit}"
                    },
                    "name": "${fitnessClass.name}",
                    "description": "${fitnessClass.description}",
                    "imageUrl": "${fitnessClass.imageUrl}"
            }
            """

            // When & Then
            // When & Then
            webTestClient
                .post()
                .uri("/api/v1/fitness_class")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(fitnessClassJson)
                .exchange()
                .expectStatus()
                .isCreated
                .expectBody()
        }

    @Test
    fun `should update fitness class`() =
        runTest {
            val fitnessClass = FitnessClassDataProvider.createClass()

            // Given
            `when`(fitnessClassesService.updateFitnessClass(fitnessClass)).thenReturn(Unit)

            // When & Then
            webTestClient
                .put()
                .uri("/api/v1/fitness_class")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(fitnessClass)
                .exchange()
                .expectStatus()
                .isOk
        }

    @Test
    fun `should delete fitness class by id`() =
        runTest {
            // Given
            `when`(fitnessClassesService.deleteFitnessClassById("1")).thenReturn(Unit)

            // When & Then
            webTestClient
                .delete()
                .uri("/api/v1/fitness_class/1")
                .exchange()
                .expectStatus()
                .isOk
        }
}
