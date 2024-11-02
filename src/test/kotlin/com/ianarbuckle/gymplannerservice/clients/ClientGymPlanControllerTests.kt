package com.ianarbuckle.gymplannerservice.clients

import com.ianarbuckle.gymplannerservice.clients.data.ClientGymPlansService
import com.ianarbuckle.gymplannerservice.data.ClientsDataProvider
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
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
@AutoConfigureDataMongo
class ClientGymPlanControllerTests {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var clientService: ClientGymPlansService

    @Test
    fun `saveClient should return created client`() {
        runTest {
            val createClient = ClientsDataProvider.createClient()

            whenever(clientService.createClient(createClient)).thenReturn(createClient)

            val clientJson =
                """
                {
                    "id": "${createClient.id}",
                    "firstName": "${createClient.firstName}",
                    "surname": "${createClient.surname}",
                    "strengthLevel": "${createClient.strengthLevel}",
                    "gymPlan": {
                        "name": "${createClient.gymPlan?.name}",
                        "personalTrainer": {
                            "id": "${createClient.gymPlan?.personalTrainer?.id}",
                        "firstName": "${createClient.gymPlan?.personalTrainer?.firstName}",
                        "surname": "${createClient.gymPlan?.personalTrainer?.surname}",
                        "imageUrl": "${createClient.gymPlan?.personalTrainer?.imageUrl}",
                        "bio": "${createClient.gymPlan?.personalTrainer?.bio}",
                        "socials": {
                            "instagram": "https://www.instagram.com/vinicius.fedyna"
                        },
                        "qualifications": [
                            "LesMillis BodyPump",
                            "Strength Development",
                            "Functional Strength"
                        ],
                        "gymLocation": "${createClient.gymPlan?.personalTrainer?.gymLocation?.name}"
                        },
                        "startDate": "${createClient.gymPlan?.startDate}",
                        "endDate": "${createClient.gymPlan?.endDate}",
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

            webTestClient.post().uri("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(clientJson))
                .exchange()
                .expectStatus().isCreated
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
        }
    }

    @Test
    fun `get all clients endpoint should return 200`() {
        val client = ClientsDataProvider.createClient()
        runTest {
            whenever(clientService.findAllClients()).thenReturn(flowOf(client))

            webTestClient.get().uri("/api/v1/clients")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(client.id ?: "")
        }
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
