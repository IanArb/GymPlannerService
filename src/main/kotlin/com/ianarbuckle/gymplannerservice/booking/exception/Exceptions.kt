package com.ianarbuckle.gymplannerservice.booking.exception

class PersonalTrainerAlreadyBookedException :
    RuntimeException("This personal trainer is already booked at the specified date and time")

class PersonalTrainerNotFoundException : RuntimeException("Personal trainer not found")

class BookingsNotFoundException : RuntimeException("Bookings not found")

class UserNotFoundException : RuntimeException("User not found")
