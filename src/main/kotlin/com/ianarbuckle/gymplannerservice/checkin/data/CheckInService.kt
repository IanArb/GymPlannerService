package com.ianarbuckle.gymplannerservice.checkin.data

import com.ianarbuckle.gymplannerservice.checkin.exception.TrainerAlreadyCheckedInException
import com.ianarbuckle.gymplannerservice.checkin.exception.TrainerAlreadyCheckedOutException
import com.ianarbuckle.gymplannerservice.checkin.exception.TrainerNotCheckedInException
import com.ianarbuckle.gymplannerservice.checkin.exception.TrainerNotFoundException
import com.ianarbuckle.gymplannerservice.checkin.exception.TrainerNotScheduledException
import com.ianarbuckle.gymplannerservice.trainers.data.PersonalTrainerRepository
import com.ianarbuckle.gymplannerservice.trainers.data.TrainerAvailabilityStatus
import java.time.LocalDateTime
import org.springframework.stereotype.Service

interface CheckInService {
    suspend fun checkIn(trainerId: String, checkInTime: LocalDateTime): CheckIn

    suspend fun checkOut(trainerId: String, checkOutTime: LocalDateTime): CheckIn
}

@Service
class CheckInServiceImpl(
    private val checkInRepository: CheckInRepository,
    private val personalTrainerRepository: PersonalTrainerRepository,
) : CheckInService {

    override suspend fun checkIn(trainerId: String, checkInTime: LocalDateTime): CheckIn {
        val trainer =
            personalTrainerRepository.findById(trainerId) ?: throw TrainerNotFoundException()

        val startOfDay = checkInTime.toLocalDate().atStartOfDay()
        val endOfDay = startOfDay.plusDays(1)
        val existing =
            checkInRepository.findByTrainerIdAndCheckInTimeBetween(trainerId, startOfDay, endOfDay)
        if (existing != null) throw TrainerAlreadyCheckedInException()

        val shift =
            trainer.schedule.find { it.dayOfWeek == checkInTime.dayOfWeek }
                ?: throw TrainerNotScheduledException()

        if (!checkInTime.toLocalTime().isBefore(shift.endTime)) throw TrainerNotScheduledException()

        val status =
            if (!checkInTime.toLocalTime().isAfter(shift.startTime)) {
                CheckInStatus.ON_TIME
            } else {
                CheckInStatus.LATE
            }

        val checkIn =
            checkInRepository.save(
                CheckIn(
                    trainerId = trainerId,
                    checkInTime = checkInTime,
                    status = status,
                )
            )

        personalTrainerRepository.save(
            trainer.copy(availabilityStatus = TrainerAvailabilityStatus.AVAILABLE)
        )

        return checkIn
    }

    override suspend fun checkOut(trainerId: String, checkOutTime: LocalDateTime): CheckIn {
        personalTrainerRepository.findById(trainerId) ?: throw TrainerNotFoundException()

        val startOfDay = checkOutTime.toLocalDate().atStartOfDay()
        val endOfDay = startOfDay.plusDays(1)
        val existing =
            checkInRepository.findByTrainerIdAndCheckInTimeBetween(trainerId, startOfDay, endOfDay)
                ?: throw TrainerNotCheckedInException()

        if (existing.checkOutTime != null) throw TrainerAlreadyCheckedOutException()

        val updatedCheckIn = checkInRepository.save(existing.copy(checkOutTime = checkOutTime))

        val updatedTrainer = personalTrainerRepository.findById(trainerId)
        updatedTrainer?.let {
            personalTrainerRepository.save(
                updatedTrainer.copy(availabilityStatus = TrainerAvailabilityStatus.UNAVAILABLE)
            )
        }

        return updatedCheckIn
    }
}
