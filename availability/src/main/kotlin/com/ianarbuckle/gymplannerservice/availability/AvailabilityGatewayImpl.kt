package com.ianarbuckle.gymplannerservice.availability

import com.ianarbuckle.gymplannerservice.booking.AvailabilityGateway
import org.springframework.stereotype.Component

/**
 * Implements booking's [AvailabilityGateway] port using this feature's repository,
 * so :booking can read/update availability without depending on :availability.
 */
@Component
class AvailabilityGatewayImpl(
    private val availabilityRepository: AvailabilityRepository,
) : AvailabilityGateway {
    override suspend fun findByTimeId(timeId: String): Availability? = availabilityRepository.findByTimeId(timeId)

    override suspend fun save(availability: Availability): Availability = availabilityRepository.save(availability)
}
