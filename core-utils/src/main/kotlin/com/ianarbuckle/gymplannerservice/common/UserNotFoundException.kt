package com.ianarbuckle.gymplannerservice.common

/**
 * Shared exception for a missing user. Lives in :core-utils because it is thrown
 * and handled across several features (authentication, booking, user profile).
 */
class UserNotFoundException : RuntimeException("User not found")
