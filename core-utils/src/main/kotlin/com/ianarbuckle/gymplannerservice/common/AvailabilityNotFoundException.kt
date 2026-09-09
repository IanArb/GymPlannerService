package com.ianarbuckle.gymplannerservice.common

/**
 * Shared exception for missing availability. Lives in :core-utils because it is
 * thrown by both the availability and booking features.
 */
class AvailabilityNotFoundException : RuntimeException("Availability not found")
