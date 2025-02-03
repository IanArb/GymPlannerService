package com.ianarbuckle.gymplannerservice.booking.exception

class PersonalTrainerAlreadyBookedException : RuntimeException() {
    override val message: String
        get() = "This personal trainer is already booked at the specified date and time"
}

class PersonalTrainerNotFoundException : RuntimeException() {
    override val message: String
        get() = "Personal trainer not found"
}

class BookingsNotFoundException : RuntimeException() {
    override val message: String
        get() = "Bookings not found"
}

class UserNotFoundException : RuntimeException() {
    override val message: String
        get() = "User not found"
}
