package com.ianarbuckle.gymplannerservice.checkin.exception

class TrainerNotFoundException : RuntimeException("Personal trainer not found")

class TrainerNotScheduledException :
    RuntimeException("Trainer is not scheduled to work at this time")

class TrainerAlreadyCheckedInException : RuntimeException("Trainer has already checked in today")

class TrainerNotCheckedInException : RuntimeException("Trainer has not checked in today")

class TrainerAlreadyCheckedOutException : RuntimeException("Trainer has already checked out today")

class InvalidCheckOutTimeException : RuntimeException("Check-out time must be after check-in time")
