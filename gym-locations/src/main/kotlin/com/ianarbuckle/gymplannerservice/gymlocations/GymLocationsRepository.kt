package com.ianarbuckle.gymplannerservice.gymlocations

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface GymLocationsRepository : CoroutineCrudRepository<GymLocation, String>
