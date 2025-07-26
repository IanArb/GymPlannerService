package com.ianarbuckle.gymplannerservice.messages.data

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.ianarbuckle.gymplannerservice.messages.MessagesServiceImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import net.bytebuddy.asm.Advice
import java.time.LocalDateTime

class MessagesServiceTests {
    private val repository: MessagesRepository = mockk()
    private val service = MessagesServiceImpl(repository)

    @Test
    fun `findAlMessages returns all messages from repository`() = runTest {
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
                    id = "1",
                    username = "Lisa",
                    userId = "user2",
                    content = "Hello, world!",
                    timestamp = LocalDateTime.now()
                ),
            )
        every { repository.findAll() } returns flowOf(*messages.toTypedArray())

        service.findAlMessages().test {
            assertThat(awaitItem()).isEqualTo(messages[0])
            assertThat(awaitItem()).isEqualTo(messages[1])
            awaitComplete()
        }

        verify { repository.findAll() }
    }

    @Test
    fun `insertMessage saves message to repository`() = runTest {
        val message =
            Message(
                id = "1",
                username = "Bob",
                userId = "user1",
                content = "Hello, world!",
                timestamp = LocalDateTime.now()
            )

        coEvery { repository.save(message) } returns message

        service.insertMessage(message)

        coVerify { repository.save(message) }
    }
}
