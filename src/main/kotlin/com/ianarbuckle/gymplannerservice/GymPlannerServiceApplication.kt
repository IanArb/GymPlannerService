package com.ianarbuckle.gymplannerservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GymPlannerServiceApplication

fun main(args: Array<String>) {
    runApplication<GymPlannerServiceApplication>(*args)
}
