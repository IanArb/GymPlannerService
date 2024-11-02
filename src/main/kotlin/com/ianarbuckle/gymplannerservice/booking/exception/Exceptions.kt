package com.ianarbuckle.gymplannerservice.booking.exception

class PersonalTrainerAlreadyBookedException : Exception() {
    override val message: String
        get() = "This personal trainer is already booked at the specified date and time"
}

class PersonalTrainerNotFoundException : Exception() {
    override val message: String
        get() = "Personal trainer not found"
}

class BookingsNotFoundException : Exception() {
    override val message: String
        get() = "Bookings not found"
}