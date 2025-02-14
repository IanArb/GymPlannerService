package com.ianarbuckle.gymplannerservice.trainers

import com.ianarbuckle.gymplannerservice.mocks.PersonalTrainerDataProvider
import com.ianarbuckle.gymplannerservice.trainers.data.GymLocation
import com.ianarbuckle.gymplannerservice.trainers.data.PersonalTrainer
import com.ianarbuckle.gymplannerservice.trainers.data.PersonalTrainersService
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
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
import kotlin.test.Test

@ExtendWith(SpringExtension::class)
@WebFluxTest(
    controllers = [PersonalTrainerController::class],
    excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class]
)
@TestPropertySource("classpath:application-test.properties")
@ActiveProfiles("test")
@AutoConfigureDataMongo
class PersonalTrainerControllerTests {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var personalTrainersService: PersonalTrainersService

    @Test
    fun `should return all personal trainers by gym location`() =
        runTest {
            val personalTrainers = PersonalTrainerDataProvider.personalTrainers()
            `when`(personalTrainersService.findTrainersByGymLocation(GymLocation.CLONTARF)).thenReturn(personalTrainers)

            webTestClient
                .get()
                .uri("/api/v1/personal_trainers?gymLocation=CLONTARF")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk
                .expectBodyList(PersonalTrainer::class.java)
                .hasSize(2)
        }

    @Test
    fun `should create personal trainer`() =
        runTest {
            val personalTrainer = PersonalTrainerDataProvider.createPersonalTrainer()
            `when`(personalTrainersService.createTrainer(personalTrainer)).thenReturn(personalTrainer)

            webTestClient
                .post()
                .uri("/api/v1/personal_trainers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(personalTrainer)
                .exchange()
                .expectStatus()
                .isCreated
                .expectBody(PersonalTrainer::class.java)
                .isEqualTo(personalTrainer)
        }

    @Test
    fun `should update personal trainer`() =
        runTest {
            val personalTrainer = PersonalTrainerDataProvider.createPersonalTrainer()
            `when`(personalTrainersService.updateTrainer(personalTrainer)).thenReturn(Unit)

            webTestClient
                .put()
                .uri("/api/v1/personal_trainers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(personalTrainer)
                .exchange()
                .expectStatus()
                .isOk
        }

    @Test
    fun `should delete personal trainer by id`() =
        runTest {
            `when`(personalTrainersService.deleteTrainerById("1")).thenReturn(Unit)

            webTestClient
                .delete()
                .uri("/api/v1/personal_trainers/1")
                .exchange()
                .expectStatus()
                .isOk
        }
}
