package com.ianarbuckle.gymplannerservice.messages

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import com.ianarbuckle.gymplannerservice.messages.data.Message

@RestController
@RequestMapping("/api/v1/messages")
class MessagesController(
    private val messagesService: MessagesService,
) {
    @Operation(
        summary = "Get all messages",
        description = "Retrieve all messages from the system",
    )
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "Messages retrieved successfully",
                ),
            ],
    )
    @GetMapping
    fun getMessages(): Flow<Message> = messagesService.findAlMessages()

    @Operation(
        summary = "Insert a new message",
        description = "Add a new message to the system",
    )
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "201",
                    description = "Message created successfully",
                ),
            ],
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun insertMessage(
        @RequestBody @Valid message: Message,
    ) {
        messagesService.insertMessage(message)
    }
}
