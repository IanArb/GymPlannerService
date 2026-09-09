package com.ianarbuckle.gymplannerservice.booking

import com.ianarbuckle.gymplannerservice.availability.Availability

/**
 * Port for reading/updating trainer availability when a slot is booked. Works with
 * the [Availability] domain type (in :domain). Implemented by the availability
 * feature so :booking does not depend on it.
 */
interface AvailabilityGateway {
    suspend fun findByTimeId(timeId: String): Availability?

    suspend fun save(availability: Availability): Availability
}
