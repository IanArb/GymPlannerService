package com.ianarbuckle.gymplannerservice.gateway

import com.ianarbuckle.gymplannerservice.availability.Availability
import com.ianarbuckle.gymplannerservice.availability.AvailabilityRepository
import com.ianarbuckle.gymplannerservice.booking.AvailabilityGateway
import org.springframework.stereotype.Component

/**
 * Adapter wiring booking's [AvailabilityGateway] port to the availability feature's
 * repository.
 */
@Component
class AvailabilityGateway(
    private val availabilityRepository: AvailabilityRepository,
) : AvailabilityGateway {
    override suspend fun findByTimeId(timeId: String): Availability? = availabilityRepository.findByTimeId(timeId)

    override suspend fun save(availability: Availability): Availability = availabilityRepository.save(availability)
}
