package com.ianarbuckle.gymplannerservice.availability.data

import com.ianarbuckle.gymplannerservice.availability.exception.AvailabilityNotFoundException
import com.ianarbuckle.gymplannerservice.booking.exception.PersonalTrainerNotFoundException
import com.ianarbuckle.gymplannerservice.trainers.data.PersonalTrainerRepository
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalDate

interface AvailabilityService {
    suspend fun getAvailability(
        personalTrainerId: String,
        month: String,
    ): Availability

    suspend fun updateAvailability(availability: Availability)

    suspend fun saveAvailability(availability: Availability): Availability

    suspend fun deleteAvailability(id: String)

    suspend fun isAvailable(
        personalTrainerId: String,
        month: String,
    ): CheckAvailability
}

@Service
class AvailabilityServiceImpl(
    private val availabilityRepository: AvailabilityRepository,
    private val personalTrainerRepository: PersonalTrainerRepository,
    private val clock: Clock = Clock.systemDefaultZone(),
) : AvailabilityService {
    override suspend fun getAvailability(
        personalTrainerId: String,
        month: String,
    ): Availability {
        personalTrainerRepository.findById(personalTrainerId) ?: throw PersonalTrainerNotFoundException()

        val availability =
            availabilityRepository.findByPersonalTrainerIdAndMonth(personalTrainerId, month)
                ?: throw AvailabilityNotFoundException()

        val currentDate = LocalDate.now(clock)
        val updatedSlots =
            availability.slots.map { slot ->
                slot.copy(
                    times =
                        slot.times.map { time ->
                            if (slot.date.isBefore(currentDate)) {
                                time.copy(status = Status.UNAVAILABLE)
                            } else {
                                time
                            }
                        },
                )
            }

        val updatedAvailability = availability.copy(slots = updatedSlots)
        if (updatedAvailability.slots.any { it.date.isBefore(currentDate) }) {
            availabilityRepository.save(updatedAvailability)
        }

        return availability
    }

    override suspend fun saveAvailability(availability: Availability): Availability {
        personalTrainerRepository.findById(availability.personalTrainerId) ?: throw PersonalTrainerNotFoundException()

        return availabilityRepository.save(availability)
    }

    override suspend fun updateAvailability(availability: Availability) {
        availabilityRepository
            .save(
                availability,
            ).takeIf { availabilityRepository.existsByPersonalTrainerId(availability.personalTrainerId) }
    }

    override suspend fun deleteAvailability(id: String) {
        availabilityRepository.deleteById(id)
    }

    override suspend fun isAvailable(
        personalTrainerId: String,
        month: String,
    ): CheckAvailability {
        val availability = getAvailability(personalTrainerId, month)
        val isAvailable =
            availability.slots.any { slot ->
                slot.times.any { time ->
                    time.status == Status.AVAILABLE
                }
            }

        return CheckAvailability(
            personalTrainerId = personalTrainerId,
            isAvailable = isAvailable,
        )
    }
}
