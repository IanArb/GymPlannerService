package com.ianarbuckle.gymplannerservice.messages

import com.ianarbuckle.gymplannerservice.messages.data.Message
import kotlin.test.Test
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime
import java.time.OffsetDateTime

@ExtendWith(SpringExtension::class)
@WebFluxTest(
    controllers = [MessagesController::class],
    excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class],
)
@TestPropertySource("classpath:application-test.properties")
@ActiveProfiles("test")
@AutoConfigureDataMongo
class MessagesControllerTests {
    @Autowired private lateinit var webTestClient: WebTestClient

    @MockitoBean private lateinit var messagesService: MessagesService

    @Test
    fun `should return all messages`() = runTest {
        val messages =
            listOf(
                Message(
                    id = "1",
                    username = "Bob",
                    userId = "user1",
                    content = "Hello, world!",
                    timestamp = LocalDateTime.now()
                ),
                Message(
                    id = "2",
                    username = "Lisa",
                    userId = "user2",
                    content = "Hi there!",
                    timestamp = LocalDateTime.now()
                ),
            )
        `when`(messagesService.findAlMessages()).thenReturn(flowOf(*messages.toTypedArray()))

        webTestClient
            .get()
            .uri("/api/v1/messages")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$[0].id")
            .isEqualTo(messages[0].id)
            .jsonPath("$[1].id")
            .isEqualTo(messages[1].id)
    }

    @Test
    fun `should save message`() = runTest {
        val message =
            Message(
                id = "1",
                username = "Bob",
                userId = "user1",
                content = "Hello, world!",
                timestamp = LocalDateTime.now().plusMinutes(1)
            )

        `when`(messagesService.insertMessage(message)).thenReturn(Unit)

        webTestClient
            .post()
            .uri("/api/v1/messages")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(message)
            .exchange()
            .expectStatus()
            .isCreated
    }
}
