package com.ianarbuckle.gymplannerservice.clients

import com.ianarbuckle.gymplannerservice.model.Client
import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
class ClientController(private val clientGymPlansService: ClientGymPlansService) {

    @GetMapping("/clients")
    suspend fun findAllClients(): Flow<Client> = clientGymPlansService.findAllClients()

    @GetMapping("{id}")
    suspend fun findClientById(@PathVariable id: String): Client? = clientGymPlansService.findClientById(id)

    @PostMapping("/clients")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun saveGymPlan(@Valid @RequestBody client: Client) = clientGymPlansService.createClient(client)

    @PutMapping("/clients")
    suspend fun updateGymPlan(@Valid @RequestBody client: Client) = clientGymPlansService.updateClient(client)

    @DeleteMapping("/clients/{id}")
    suspend fun deleteClientById(@PathVariable id: String) = clientGymPlansService.deleteById(id)

}