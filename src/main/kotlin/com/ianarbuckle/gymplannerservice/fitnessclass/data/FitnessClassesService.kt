package com.ianarbuckle.gymplannerservice.fitnessclass.data

import com.ianarbuckle.gymplannerservice.fitnessclass.exception.NoFitnessClassFoundException
import java.time.DayOfWeek
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

interface FitnessClassesService {
    suspend fun fitnessClasses(): Flow<FitnessClass>

    suspend fun fitnessClassesByDayOfWeek(dayOfWeek: String): Flow<FitnessClass>

    suspend fun createFitnessClass(fitnessClass: FitnessClass): FitnessClass

    suspend fun updateFitnessClass(fitnessClass: FitnessClass)

    suspend fun deleteFitnessClassById(id: String)
}

@Service
class FitnessClassesServiceImpl(
    private val repository: FitnessClassRepository,
) : FitnessClassesService {
    override suspend fun fitnessClasses(): Flow<FitnessClass> = repository.findAll()

    override suspend fun fitnessClassesByDayOfWeek(dayOfWeek: String): Flow<FitnessClass> {
        when (dayOfWeek) {
            MONDAY -> {
                return fetchClassesByDayOfWeek(DayOfWeek.MONDAY)
            }
            TUESDAY -> {
                return fetchClassesByDayOfWeek(DayOfWeek.TUESDAY)
            }
            WEDNESDAY -> {
                return fetchClassesByDayOfWeek(DayOfWeek.WEDNESDAY)
            }
            THURSDAY -> {
                return fetchClassesByDayOfWeek(DayOfWeek.THURSDAY)
            }
            FRIDAY -> {
                return fetchClassesByDayOfWeek(DayOfWeek.FRIDAY)
            }
            SATURDAY -> {
                return fetchClassesByDayOfWeek(DayOfWeek.SATURDAY)
            }
            SUNDAY -> {
                return fetchClassesByDayOfWeek(DayOfWeek.SUNDAY)
            }
        }
        throw NoFitnessClassFoundException()
    }

    private suspend fun fetchClassesByDayOfWeek(dayOfWeek: DayOfWeek) =
        repository.findFitnessClassesByDayOfWeek(dayOfWeek)

    override suspend fun createFitnessClass(fitnessClass: FitnessClass): FitnessClass =
        repository.save(fitnessClass)

    override suspend fun updateFitnessClass(fitnessClass: FitnessClass) {
        repository.save(fitnessClass).takeIf { repository.existsById(fitnessClass.id ?: "") }
    }

    override suspend fun deleteFitnessClassById(id: String) {
        repository.deleteById(id)
    }

    companion object {
        const val MONDAY = "MONDAY"
        const val TUESDAY = "TUESDAY"
        const val WEDNESDAY = "WEDNESDAY"
        const val THURSDAY = "THURSDAY"
        const val FRIDAY = "FRIDAY"
        const val SATURDAY = "SATURDAY"
        const val SUNDAY = "SUNDAY"
    }
}
