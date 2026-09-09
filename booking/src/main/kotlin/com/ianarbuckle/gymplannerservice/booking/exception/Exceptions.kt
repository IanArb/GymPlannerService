package com.ianarbuckle.gymplannerservice.booking.exception

class PersonalTrainerAlreadyBookedException : RuntimeException("This personal trainer is already booked at the specified date and time")

class BookingsNotFoundException : RuntimeException("Bookings not found")
