package com.ianarbuckle.gymplannerservice.gymlocations.data

import org.springframework.data.repository.kotlin.CoroutineCrudRepository


interface GymLocationsRepository : CoroutineCrudRepository<GymLocation, String>