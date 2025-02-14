package com.ianarbuckle.gymplannerservice.clients

import com.ianarbuckle.gymplannerservice.clients.data.Client
import com.ianarbuckle.gymplannerservice.clients.data.ClientGymPlansService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/clients")
@Tag(
    name = "Clients",
    description = "Endpoints for clients",
)
class ClientController(
    private val clientGymPlansService: ClientGymPlansService,
) {
    @GetMapping
    suspend fun findAllClients(): Flow<Client> = clientGymPlansService.findAllClients()

    @GetMapping("/{id}")
    suspend fun findClientById(
        @PathVariable id: String,
    ): Client? =
        clientGymPlansService.findClientById(id) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Client not found",
        )

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun saveGymPlan(
        @Valid @RequestBody client: Client,
    ): Client = clientGymPlansService.createClient(client)

    @PutMapping()
    suspend fun updateGymPlan(
        @Valid @RequestBody client: Client,
    ) = clientGymPlansService.updateClient(client)

    @DeleteMapping("/{id}")
    suspend fun deleteClientById(
        @PathVariable id: String,
    ) = clientGymPlansService.deleteById(id)
}
