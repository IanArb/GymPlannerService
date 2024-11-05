package com.ianarbuckle.gymplannerservice.fitnessclass

import com.ianarbuckle.gymplannerservice.fitnessclass.data.FitnessClass
import com.ianarbuckle.gymplannerservice.fitnessclass.data.FitnessClassesService
import com.ianarbuckle.gymplannerservice.fitnessclass.exception.NoFitnessClassFoundException
import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/fitness_class")
class ClassesController(
    private val fitnessClassService: FitnessClassesService
) {

    @GetMapping
    suspend fun findAllFitnessClassesByDayOfWeek(@RequestParam dayOfWeek: String? = null): Flow<FitnessClass> {
         return try {
             fitnessClassService.fitnessClassesByDayOfWeek(dayOfWeek ?: "")
         } catch (exception: NoFitnessClassFoundException) {
             throw ResponseStatusException(
                 HttpStatus.NOT_FOUND, "Class not found", exception
             )
         }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun saveFitnessClass(
        @Valid @RequestBody fitnessClass: FitnessClass,
    ) = fitnessClassService.createFitnessClass(fitnessClass)

    @PutMapping
    suspend fun updateFitnessClass(@Valid @RequestBody fitnessClass: FitnessClass) = fitnessClassService.updateFitnessClass(fitnessClass)

    @DeleteMapping("/{id}")
    suspend fun deleteFitnessClassById(@PathVariable id: String) = fitnessClassService.deleteFitnessClassById(id)
}