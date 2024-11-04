package com.ianarbuckle.gymplannerservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
class GymPlannerServiceApplication

fun main(args: Array<String>) {
    runApplication<GymPlannerServiceApplication>(*args)
}
