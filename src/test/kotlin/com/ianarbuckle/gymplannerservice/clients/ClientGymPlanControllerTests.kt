package com.ianarbuckle.gymplannerservice.clients

import com.ianarbuckle.gymplannerservice.clients.data.ClientGymPlansService
import com.ianarbuckle.gymplannerservice.data.ClientsDataProvider
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import org.mockito.Mockito.`when` as whenever

@ExtendWith(SpringExtension::class)
@WebFluxTest(controllers = [ClientController::class], excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class])
@TestPropertySource("classpath:application-test.properties")
@ActiveProfiles("test")
class ClientGymPlanControllerTests {
    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var clientService: ClientGymPlansService

    @Test
    fun `saveClient should return created client`() {
        runTest {
            val createClient = ClientsDataProvider.createClient()

            val clientJson =
                """
{
    "firstName": "Pablo",
    "surname": "Escobar",
    "strengthLevel": "advanced",
    "gymPlan": {
        "name": "Pablo's December plan",
        "personalTrainer": {
            "firstName": "Ben",
            "surname": "Westwood"
        },
        "startDate": "2024-11-01T10:53:33.010",
        "endDate": "2024-11-01T10:53:33.010",
        "sessions": [
            {
                "name": "Chest",
                "workouts": [
                    {
                        "name": "Chest press",
                        "sets": 3,
                        "repetitions": 10,
                        "weight": {
                            "value": 15,
                            "unit": "kg"
                        },
                        "note": "Push heavy!"
                    },
                    {
                        "name": "Decline bench press",
                        "sets": 3,
                        "repetitions": 10,
                        "weight": {
                            "value": 15,
                            "unit": "kg"
                        }
                    },
                    {
                        "name": "Cable pull down",
                        "sets": 3,
                        "repetitions": 10,
                        "weight": {
                            "value": 45,
                            "unit": "lbs"
                        }
                    }
                ]
            }
        ]
    }
}
                """.trimIndent()

            webTestClient
                .post()
                .uri("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(clientJson))
                .exchange()
                .expectStatus()
                .isCreated()
        }
    }

    @Test
    fun `get all clients endpoint should return 200`() =
        runTest {
            webTestClient
                .get()
                .uri("/api/v1/clients")
                .exchange()
                .expectStatus()
                .isOk
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
        }

    @Test
    fun `getClient should return 404 if client not found`() =
        runTest {
            whenever(clientService.findClientById(anyString())).thenReturn(null)

            webTestClient
                .get()
                .uri("/clients/2")
                .exchange()
                .expectStatus()
                .isNotFound
        }

    @Test
    fun `test delete endpoint returns 200`() =
        runTest {
            webTestClient
                .delete()
                .uri("/api/v1/clients/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk
        }
}
