package com.ianarbuckle.gymplannerservice.common

/**
 * Shared exception for a missing personal trainer. Lives in :core-utils because
 * it is thrown by both the availability and booking features.
 */
class PersonalTrainerNotFoundException : RuntimeException("Personal trainer not found")
