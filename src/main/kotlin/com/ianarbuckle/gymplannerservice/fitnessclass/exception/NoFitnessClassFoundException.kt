package com.ianarbuckle.gymplannerservice.fitnessclass.exception

class NoFitnessClassFoundException : Exception() {
    override val message: String = "No Fitness Class found"
}