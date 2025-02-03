package com.ianarbuckle.gymplannerservice.fitnessclass

import com.ianarbuckle.gymplannerservice.fitnessclass.data.FitnessClass
import com.ianarbuckle.gymplannerservice.fitnessclass.data.FitnessClassesService
import com.ianarbuckle.gymplannerservice.fitnessclass.exception.NoFitnessClassFoundException
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/fitness_class")
@Tag(name = "Fitness Class", description = "Endpoints for fitness classes")
class ClassesController(
    private val fitnessClassService: FitnessClassesService,
) {
    @GetMapping
    suspend fun findAllFitnessClassesByDayOfWeek(
        @RequestParam dayOfWeek: String? = null,
    ): Flow<FitnessClass> =
        try {
            fitnessClassService.fitnessClassesByDayOfWeek(dayOfWeek ?: "")
        } catch (exception: NoFitnessClassFoundException) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Class not found",
                exception,
            )
        }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun saveFitnessClass(
        @Valid @RequestBody fitnessClass: FitnessClass,
    ) = fitnessClassService.createFitnessClass(fitnessClass)

    @PutMapping
    suspend fun updateFitnessClass(
        @Valid @RequestBody fitnessClass: FitnessClass,
    ) = fitnessClassService.updateFitnessClass(fitnessClass)

    @DeleteMapping("/{id}")
    suspend fun deleteFitnessClassById(
        @PathVariable id: String,
    ) = fitnessClassService.deleteFitnessClassById(id)
}
